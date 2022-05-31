package com.payu.baas.core.view.ui.activity.splash

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivitySplashScreenBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetUserStateResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.*
import com.payu.baas.core.util.enums.UserState
import com.payu.baas.core.view.ui.BaseActivity

class SplashScreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: SplashScreenViewModel
    var userRegisteredNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        activity = this
        setupViewModel()
        setupObserver()
        userRegisteredNumber = SessionManagerUI.getInstance(this).registeredMobileNumber.toString()
        var userState = SessionManagerUI.getInstance(this).userStatusCode.toString()
        if (userState.isNullOrEmpty() || userState == "null") {
            if (userRegisteredNumber.isNullOrEmpty() || userRegisteredNumber.equals("null"))
                setupUI(UserState.MOBILE_NOT_SUBMITTED.getValue())
            else
                viewModel.getUserState(userRegisteredNumber)
        } else {
            setupUI(userState)
        }
    }

    private fun setupUI(userState: String) {
        val thread: Thread = object : Thread() {
            @SuppressLint("ResourceAsColor")
            override fun run() {
                var progressCount = 0
                while (progressCount < 100) {
                    runOnUiThread {
                        binding.pBar.progress = progressCount
                        val progressDrawable: Drawable = binding.pBar.progressDrawable.mutate()
                        progressDrawable.colorFilter =
                            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                Color.GREEN,
                                BlendModeCompat.SRC_ATOP
                            )
                        binding.pBar.progressDrawable = progressDrawable
                    }
                    try {
                        sleep(50)
                    } catch (e: InterruptedException) {
                    }
                    progressCount++
                }
                viewModel.showScreenAsPerUserState(userState)
            }
        }
        thread.start()

    }

    private fun setupObserver() {
        viewModel.getUserStateResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_USER_STATE)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            SplashScreenViewModel.SplashViewModelFactory(this, this)
        )[SplashScreenViewModel::class.java]
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_USER_STATE -> {
                        it.data?.let {
                            if (it is GetUserStateResponse) {
                                if (it.message.isNullOrEmpty()) // right now we are not having logout so getting this as not clearing data in testing mode after crash or anything.
                                    setupUI(UserState.MOBILE_NOT_SUBMITTED.getValue())  // so added this check
                                else
                                    setupUI(it.message!!)
                            }
                        }
                    }
                }

            }
            Status.LOADING -> {
//                showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()
                var errorResponse = it.data as ErrorResponse
                if (!(it.message.isNullOrEmpty()) && (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED))
                ) {
                    viewModel.reGenerateAccessToken(true)
                } else if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    var msg = ""
                    try {
                        var errorUiRes = JsonUtil.toObject(
                            it.message,
                            ErrorResponseUI::class.java
                        ) as ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.llParent,
                        msg,
                        false
                    )
                }
//                if (it.message!!.contains("error"))
//                    viewModel.baseCallBack?.showToastMessage(it.message)
//                else if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)) {
//                    viewModel.reGenerateAccessToken(true)
//                } else
//
//                    when (apiName) {
//                        ApiName.GET_USER_STATE -> {
//                            var errorRes = JsonUtil.toObject(
//                                it.message,
//                                GetUserStateResponse::class.java
//                            ) as GetUserStateResponse
//                            if (!errorRes.userMessage.isNullOrEmpty() && errorRes.userMessage.equals(
//                                    BaaSConstantsUI.INVALID_ACCESS_TOKEN
//                                )
//                            ) {
//                                viewModel.reGenerateAccessToken(true)
//                            } else {
//                                setupUI(UserState.MOBILE_NOT_SUBMITTED.getValue())
//                            }
//
//                        }
//                    }
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_USER_STATE -> {
                        viewModel.getUserState(userRegisteredNumber)
                    }
                    ApiName.GET_CLIENT_TOKEN -> {
                        viewModel.reGenerateAccessToken(true)
                    }
                }
            }
        }
    }
}
