package com.payu.baas.coreUI.view.ui.activity.myDetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityMyDetailsBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetUserDetailsResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.BaaSConstantsUI.EDIT_ADDRESS_SNACKBAR_FLAG_KEY
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.editCurrentAddress.EditCurrentAddressActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*

class MyDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityMyDetailsBinding
    private lateinit var viewModel: UserDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_details)
        activity = this
        binding.mobileNumber.text = SessionManagerUI.getInstance(applicationContext).registeredMobileNumber
        setupViewModel()
        setupObserver()
        setUpUI()
    }

    fun myDetailPreviousScreen(view: View) {
        finish()
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_GO_BACK_MY_DETAILS,
            BaaSConstantsUI.CL_USER_GO_BACK_MY_DETAILS_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            SessionManagerUI.getInstance(applicationContext).userMobileNumber,
            Date(),""
        )
    }



    fun edit_current_address(view: View) {
        callNextScreen(Intent(this, EditCurrentAddressActivity::class.java), null,true)
    }

    private fun setUpUI() {
        var extra = intent.extras
        if (extra != null && extra.get(EDIT_ADDRESS_SNACKBAR_FLAG_KEY) == "1") {
            SimpleCustomSnackbar.addressChange.make(
                binding.container,
                getString(R.string.update_address_message),
                Snackbar.LENGTH_LONG
            )?.show()
        }

    }

    private fun setupObserver() {
        viewModel.userDetailsResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_USER_DETAILS)
        })

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            UserDetailsViewModel.UserDetailsViewModelFactory(this, this)
        )[UserDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }



    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_USER_DETAILS -> {
                        it.data?.let {
                            if (it is GetUserDetailsResponse) {
                                Glide.with(applicationContext)
                                    .load((it as GetUserDetailsResponse).selfieLink)
                                    .error(R.drawable.delivery_boy)
                                    .into(binding.profilePic)
                            }
                        }
                    }
                }
            }
            Status.LOADING -> {
                showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()
                var errorResponse = it.data as ErrorResponse
                if (!(it.message.isNullOrEmpty()) && (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED))
                ) {
                    viewModel.reGenerateAccessToken(false)
                } else if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    var msg = ""
                    try {
                        var errorUiRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                            it.message,
                            com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                        ) as com.payu.baas.coreUI.model.ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.container,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
            }
        }


    }
    override fun onResume() {
        super.onResume()
        viewModel.userDetailResponse()
    }
}