package com.payu.baas.coreUI.view.ui.activity.relogin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityReloginBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.otpverification.OTPVerificationActivity
import com.payu.baas.coreUI.util.Status
import java.util.*

class ReloginActivity : BaseActivity() {
    lateinit var binding: ActivityReloginBinding
    private lateinit var viewModel: ReLoginViewModel
    var strMobileNumber: String? = null
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_relogin)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }


    private fun setupObserver() {
        viewModel.getOtpResponseObs().observe(this, {
            parseResponse(it, ApiName.SEND_OTP)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.SEND_OTP -> {
                        it.data?.let {
                            if (it is SendOtpResponse) {
                                    if ((it as SendOtpResponse).message.equals(
                                            BaaSConstantsUI.OTP_SENT)) {
                                        showToastMessage(it.message)
                                        val countryCode: String =
                                            binding.countryCode.text.toString().trim()
                                        SessionManagerUI.getInstance(
                                            applicationContext
                                        ).registeredMobileNumber =
                                            "$countryCode $strMobileNumber"
                                        SessionManagerUI.getInstance(
                                            applicationContext
                                        ).userMobileNumber =
                                            strMobileNumber
                                        viewModel.baseCallBack!!.cleverTapUserProfile(
                                            BaaSConstantsUI.CL_USER_DUMMY_NAME,
                                            strMobileNumber,
                                            BaaSConstantsUI.CL_USER_DUMMY_EMAIL,
                                            strMobileNumber,
                                            "",
                                            "",
                                            "",
                                            "",
                                            BaaSConstantsUI.CL_USER_PROFILE_PHOTO,
                                            ""
                                        )
                                        viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
                                            BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION,
                                            BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION_EVENT_ID,
                                            SessionManager.getInstance(this).accessToken, imei,
                                            strMobileNumber,
                                            Date()
                                        )
                                        callNextScreen(
                                            Intent(
                                                applicationContext,
                                                OTPVerificationActivity::class.java
                                            ), null, true
                                        )
                                    }
//

//                                if ((it as SendOtpResponse).message.equals("OTP_SENT")) {
//                                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                                    val countryCode: String =
//                                        binding.countryCode.text.toString().trim()
//                                    SessionManagerUI.getInstance(applicationContext).registeredMobileNumber =
//                                        "$countryCode $strMobileNumber"
////                                    SessionManager.getInstance(applicationContext).userMobileNumber = strMobileNumber
//                                    callNextScreen(
//                                        Intent(
//                                            applicationContext,
//                                            OTPVerificationActivity::class.java
//                                        ), null
//                                    )
//                                }
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
                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ){
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
                        msg =it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.rlParent,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.SEND_OTP -> {
                        viewModel.sendOtp()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReLoginViewModel.ReLoginViewModelFactory(this, this)
        )[ReLoginViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun setupUI() {
        var extra = intent.extras
        if (extra != null) {
            if (extra.containsKey(BaaSConstantsUI.MOBILE_MISMATCH_KEY)) {
                binding.reloginMessage.setText(
                    extra.get(BaaSConstantsUI.MOBILE_MISMATCH_KEY).toString()
                )
            }
            if (extra.containsKey(BaaSConstantsUI.USER_NAME)) {
                binding.name.setText(
                    "Dear " +
                            extra.get(BaaSConstantsUI.USER_NAME).toString() + ","
                )
            }
        }
        binding.countryCode.setText(R.string.country_code)
        mobileTextChange()
//        findIMEI()
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_KYC_FAILED,
            BaaSConstantsUI.CL_USER_KYC_FAILED_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )
    }

    fun openPreviousScreen(view: View) {
        finish()
    }

    private fun mobileTextChange() {
        binding.mobilenumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                strMobileNumber = binding.mobilenumber.text.toString().trim()
//                when {
//                    strMobileNumber!!.length != 10 -> {
//                        binding.errorMessage.setText(R.string.mobile_number_length)
//                        binding.failedIcon.visibility = View.VISIBLE
//                        binding.correct.visibility = View.GONE
//                    }
//                    strMobileNumber!!.length == 10 -> {
//                        binding.failedIcon.visibility = View.GONE
//                        binding.correct.visibility = View.VISIBLE
//                        binding.errorMessage.visibility = View.GONE
//
//                    }
//                }


                    when {
                        strMobileNumber!!.length != 10 -> {
                            if (!onlyDigits(strMobileNumber!!)) {
                                binding.errorMessage.setText(R.string.mobile_number_allowed_only_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.btSendOTP.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if(binding.errorMessage.visibility==View.VISIBLE)
                                        binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            } else {
                                binding.errorMessage.setText(R.string.mobile_length_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.btSendOTP.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if(binding.errorMessage.visibility==View.VISIBLE)
                                        binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            }
                        }
                        strMobileNumber!!.length == 10 -> {
                            if (!onlyDigits(strMobileNumber!!)) {
                                binding.errorMessage.setText(R.string.mobile_number_allowed_only_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.btSendOTP.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if(binding.errorMessage.visibility==View.VISIBLE)
                                        binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            } else {
                                binding.failedIcon.visibility = View.GONE
                                binding.btSendOTP.isEnabled = true
                                binding.correct.visibility = View.VISIBLE
                                binding.errorMessage.visibility = View.GONE
                            }
                        }
                    }
            }
        })
    }

}