package com.payu.baas.core.view.ui.activity.otpverification

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.core.R
import com.payu.baas.core.R.drawable
import com.payu.baas.core.databinding.ActivityOtpverificationBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.enums.UserState
import com.payu.baas.core.view.ui.activity.kyc.CompleteKYCActivity
import com.payu.baas.core.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*


class OTPVerificationActivity : BaseActivity() {
    lateinit var binding: ActivityOtpverificationBinding
    private lateinit var viewModel: OTPVerificationViewModel
    var mobileNumber: String? = null

    //string declaration otp field
    private var enterOtp1String: String? = null
    private var enterOtp2String: String? = null
    private var enterOtp3String: String? = null
    private var enterOtp4String: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otpverification)
        activity = this
        setupViewModel()
        setupObserver()
        setupResendOTPObserver()
        setupUI()
    }

    private fun applyFocusOnOTPField() {
        binding.enterOtp2.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterOtp2.text.toString().isEmpty()) {
                    binding.enterOtp1.requestFocus()
                    binding.enterOtp1.isCursorVisible = true

                }
            }
            false
        }

        binding.enterOtp3.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterOtp3.text.toString().isEmpty()) {
                    binding.enterOtp2.requestFocus()
                    binding.enterOtp2.isCursorVisible = true

                }
            }
            false
        }

        binding.enterOtp4.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterOtp4.text.toString().isEmpty()) {
                    binding.enterOtp3.requestFocus()
                    binding.enterOtp3.isCursorVisible = true
                }
            }
            false
        }

        binding.enterOtp1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterOtp1String = binding.enterOtp1.text.toString().trim()
                if (enterOtp1String?.length == 1) {
                    moveToNext()
                }
            }
        })

        binding.enterOtp2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                enterOtp2String = binding.enterOtp2.text.toString().trim()
                if (enterOtp2String?.length == 1) {
                    moveToNext()
                }
            }
        })
        binding.enterOtp3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterOtp3String = binding.enterOtp3.text.toString().trim()
                if (enterOtp3String?.length == 1) {
                    moveToNext()
                }
            }
        })
        binding.enterOtp4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterOtp4String = binding.enterOtp4.text.toString().trim()
                if (enterOtp4String?.length == 1) {
                    moveToNext()
                    binding.enterOtp4.requestFocus()
                }
            }
        })

        /*binding.enterOtp1.background = resources.getDrawable(drawable.otp_white_border_view)
        binding.enterOtp1.requestFocus()
        binding.enterOtp1.isCursorVisible = true

        binding.enterOtp1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp1String = binding.enterOtp1.text.toString().trim()
                if(enterOtp1String.isNullOrEmpty()){
                    binding.verify.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                binding.enterOtp2.background = resources.getDrawable(drawable.otp_white_border_view)
                binding.enterOtp1.clearFocus()
                binding.enterOtp2.requestFocus()
                binding.enterOtp2.isCursorVisible = true
                binding.enterOtp1.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(drawable.otp_background)
            }
        })
        binding.enterOtp2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp2String = binding.enterOtp2.text.toString().trim()
                if(enterOtp2String.isNullOrEmpty()){
                    binding.verify.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                binding.enterOtp2.clearFocus()
                binding.enterOtp3.requestFocus()
                binding.enterOtp3.isCursorVisible = true
                binding.enterOtp3.background = resources.getDrawable(drawable.otp_white_border_view)
                binding.enterOtp1.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(drawable.otp_background)
            }
        })
        binding.enterOtp3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp3String = binding.enterOtp3.text.toString().trim()
                if(enterOtp3String.isNullOrEmpty()){
                    binding.verify.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                binding.enterOtp3.clearFocus()
                binding.enterOtp4.requestFocus()
                binding.enterOtp4.isCursorVisible = true
                binding.enterOtp4.background = resources.getDrawable(drawable.otp_white_border_view)
                binding.enterOtp1.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(drawable.otp_background)
            }
        })
        binding.enterOtp4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp4String = binding.enterOtp4.text.toString().trim()
                if(enterOtp4String.isNullOrEmpty()){
                    binding.verify.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                binding.enterOtp4.clearFocus()
                binding.enterOtp4.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp1.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(drawable.otp_background)
                if(!enterOtp1String.isNullOrEmpty() && !enterOtp2String.isNullOrEmpty() && !enterOtp3String.isNullOrEmpty() && !enterOtp4String.isNullOrEmpty())
                binding.verify.isEnabled = true
            }
        })*/
    }

    private fun otpConvertAstric() {
        binding.enterOtp1.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp2.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp3.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp4.transformationMethod = AsteriskPasswordTransformationMethod()

        //white background otp field
    }

    private fun moveToNext() {
        when {
            binding.enterOtp1.text.toString().isEmpty() -> {
                binding.enterOtp1.background =
                    ContextCompat.getDrawable(this, drawable.otp_white_border_view)
                binding.enterOtp1.requestFocus()
                binding.enterOtp1.isCursorVisible = true

                binding.enterOtp2.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp3.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp4.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.verify.isEnabled = false
            }
            binding.enterOtp2.text.toString().isEmpty() -> {
                binding.enterOtp2.background =
                    ContextCompat.getDrawable(this, drawable.otp_white_border_view)
                binding.enterOtp2.requestFocus()
                binding.enterOtp2.isCursorVisible = true

                binding.enterOtp1.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp3.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp4.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)

                binding.verify.isEnabled = false
            }
            binding.enterOtp3.text.toString().isEmpty() -> {
                binding.enterOtp3.background =
                    ContextCompat.getDrawable(this, drawable.otp_white_border_view)
                binding.enterOtp3.requestFocus()
                binding.enterOtp3.isCursorVisible = true

                binding.enterOtp1.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp2.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp4.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)

                binding.verify.isEnabled = false
            }
            binding.enterOtp4.text.toString().isEmpty() -> {
                binding.enterOtp4.background =
                    ContextCompat.getDrawable(this, drawable.otp_white_border_view)
                binding.enterOtp4.requestFocus()
                binding.enterOtp4.isCursorVisible = true

                binding.enterOtp1.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp2.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp3.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)

                binding.verify.isEnabled = false
            }
            else -> {
                binding.enterOtp1.clearFocus()
                binding.enterOtp2.clearFocus()
                binding.enterOtp3.clearFocus()
                binding.enterOtp4.clearFocus()

                binding.enterOtp1.isCursorVisible = false
                binding.enterOtp2.isCursorVisible = false
                binding.enterOtp3.isCursorVisible = false
                binding.enterOtp4.isCursorVisible = false

                binding.enterOtp1.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp2.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp3.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.enterOtp4.background =
                    ContextCompat.getDrawable(this, drawable.otp_background)
                binding.verify.isEnabled = true
            }
        }
    }

    fun otp_backarrow(view: View) {
        finish()
        viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_BACK_OTP_VERIFY,
            BaaSConstantsUI.CL_USER_BACK_OTP_VERIFY_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )

    }


    fun editNumber(view: View) {
        var bundle = Bundle()
        bundle.putString(BaaSConstantsUI.MOBILE_NUMBER, mobileNumber)
        var intent = Intent(applicationContext, MobileVerificationActivity::class.java)
//        intent.putExtras(bundle)
        callNextScreen(intent, null, true)
    }

    private fun setupUI() {
//        OTPReceiver().setEditText(binding.enterOtp1)
        showKeyboard(this, binding.enterOtp1, InputType.TYPE_CLASS_NUMBER)

//        var extra = intent.extras
//        if (extra != null && extra.containsKey(BaaSConstantsUI.MOBILE_NUMBER)) {
//            mobileNumber = intent.getStringExtra(BaaSConstantsUI.MOBILE_NUMBER)
//            binding.registeredMobileNumber.text = "+91 " + mobileNumber
//        }
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        binding.registeredMobileNumber.text =
            SessionManagerUI.getInstance(applicationContext).registeredMobileNumber
        otpConvertAstric()
        applyFocusOnOTPField()
    }

    private fun setupResendOTPObserver() {
        viewModel.getResendOtpResponseObs().observe(this, {
            parseResendOTPResponse(it, ApiName.SEND_OTP)
        })
    }

    private fun setupObserver() {
        viewModel.verifyOtpResponseObs().observe(this, {
            parseResponse(it, ApiName.VERIFY_OTP)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        hideKeyboard(this)
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_OTP -> {
                        it.data?.let {
                            if (it is VerifyOtpResponse) {
                                if ((it).message.equals(BaaSConstantsUI.OTP_VERIFIED)) {
//                                    showToastMessage(it.message)

//                                    SessionManagerUI.getInstance(applicationContext).registeredMobileNumber =
//                                        binding.registeredMobileNumber.text.toString()
//                                    SessionManagerUI.getInstance(applicationContext).userMobileNumber =
//                                        mobileNumber
//                                    otpVerificationBaseCallback.cleverTapUserProfile(
//                                        BaaSConstantsUI.CL_USER_DUMMY_NAME,
//                                        mobileNumber,
//                                        BaaSConstantsUI.CL_USER_DUMMY_EMAIL,
//                                        mobileNumber,
//                                        "",
//                                        "",
//                                        "",
//                                        "",
//                                        BaaSConstantsUI.CL_USER_PROFILE_PHOTO,
//                                        ""
//                                    )
//                                    otpVerificationBaseCallback.cleverTapUserOnBoardingEvent(
//                                        BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION,
//                                        BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION_EVENT_ID,
//                                        "", imei,
//                                        mobileNumber,
//                                        Date()
//                                    )
                                    if (SessionManagerUI.getInstance(this).userStatusCode == UserState.PERMISSION_ASSIGNED.getValue())
                                        SessionManagerUI.getInstance(this).userStatusCode =
                                            UserState.MOBILE_VERIFIED.getValue()
                                    callNextScreen(
                                        Intent(applicationContext, CompleteKYCActivity::class.java),
                                        null, true
                                    )
                                }
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
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    clearOTPField()
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
                        binding.errorMessage.visibility = View.VISIBLE
                        binding.errorMessage.setText(msg)
                        binding.verify.isEnabled = false
                        binding.lOTP.startAnimation(shakeView())

                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_OTP -> {
                        viewModel.verify()
                    }
                }
            }
        }
    }

    private fun clearOTPField() {
        binding.enterOtp1.text = null
        binding.enterOtp2.text = null
        binding.enterOtp3.text = null
        binding.enterOtp4.text = null
        binding.enterOtp1.background =
            ContextCompat.getDrawable(this, drawable.otp_white_border_view)
        binding.enterOtp1.requestFocus()
        binding.enterOtp1.isCursorVisible = true
    }


    private fun parseResendOTPResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.SEND_OTP -> {
                        it.data?.let {
                            if (it is SendOtpResponse) {
                                if ((it).message.equals(BaaSConstantsUI.OTP_SENT)) {
                                    hideKeyboard(this)
                                    SimpleCustomSnackbar.resendOTPSnackBar.make(
                                        binding.container,
                                        BaaSConstantsUI.SECOND_TIME_OTP,
                                        Snackbar.LENGTH_LONG
                                    )?.show()
                                }
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
                showToastMessage(it.message)
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_OTP -> {
                        viewModel.verify()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            OTPVerificationViewModel.OTPVerificationViewModelFactory(this, this)
        )[OTPVerificationViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei
    }


}