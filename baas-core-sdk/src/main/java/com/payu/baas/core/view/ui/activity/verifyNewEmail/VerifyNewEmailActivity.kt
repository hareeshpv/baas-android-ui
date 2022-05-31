package com.payu.baas.core.view.ui.activity.verifyNewEmail

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityVerifyNewEmailBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.util.AsteriskPasswordTransformationMethod
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.Status
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.profile.UserProfileActivity
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar

class VerifyNewEmailActivity : BaseActivity() {
    lateinit var binding: ActivityVerifyNewEmailBinding
    var strVerifyEmail: String? = null
    lateinit var emailOtpVerificationBaseCallback: BaseCallback
    private lateinit var viewModel: VerifyNewEmailViewModel
    //string declaration otp field
    private var enterOtp1String: String? = null
    private var enterOtp2String: String? = null
    private var enterOtp3String: String? = null
    private var enterOtp4String: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_new_email)
        activity = this
        setupUI()
        setupViewModel()
        setupObserver()
        setupResendOTPObserver()
        var extra = intent.extras
        if (extra != null) {
            strVerifyEmail = extra.getString(BaaSConstantsUI.EMAIL_ADDRESS)
            binding.registeredEmailAddress.text = strVerifyEmail
        }
    }

    fun verify_new_email_backarrow(view: View) {
        finish()
    }

    fun editEmail(view: View) {
        finish()
    }
    private fun setupUI() {
        emailOtpVerificationBaseCallback = this
        emailOtpVerificationBaseCallback.showKeyboard(this, binding.enterOtp1)
        otpConvertAstric()
        applyFocusOnOTPField()
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[VerifyNewEmailViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.strVerifyEmail = strVerifyEmail
        viewModel.emailOtpVerificationBaseCallback = emailOtpVerificationBaseCallback
    }
    private fun applyFocusOnOTPField() {
        binding.enterOtp2.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                //this is for backspace
                if (binding.enterOtp2.text.toString().isEmpty()) {
                    binding.enterOtp1.requestFocus()
                }
            }
            false;
        }

        binding.enterOtp3.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                //this is for backspace
                if (binding.enterOtp3.text.toString().isEmpty()) {
                    binding.enterOtp2.requestFocus()
                }
            }
            false;
        }

        binding.enterOtp4.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                //this is for backspace
                if (binding.enterOtp4.text.toString().isEmpty()) {
                    binding.enterOtp3.requestFocus()
                }
            }
            false;
        }

        binding.enterOtp1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp1String = binding.enterOtp1.text.toString().trim()
                if (enterOtp1String?.length == 1) {
                    moveToNext()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.enterOtp2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp2String = binding.enterOtp2.text.toString().trim()
                if (enterOtp2String?.length == 1) {
                    moveToNext()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.enterOtp3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp3String = binding.enterOtp3.text.toString().trim()
                if (enterOtp3String?.length == 1) {
                    moveToNext()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.enterOtp4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enterOtp4String = binding.enterOtp4.text.toString().trim()
                if (enterOtp4String?.length == 1) {
                    moveToNext()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }
    private fun otpConvertAstric() {
        binding.enterOtp1.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp2.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp3.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterOtp4.transformationMethod = AsteriskPasswordTransformationMethod()
        //white background otp field
    }
    private fun parseResendOTPResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.SEND_OTP -> {
                        it.data?.let {
                            if (it is SendOtpResponse) {
                                if ((it as SendOtpResponse).message.equals(BaaSConstantsUI.OTP_SENT)) {
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
                        viewModel.resendEmailOTP()
                    }
                }
            }
        }
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
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_OTP -> {
                        it.data?.let {
                            if (it is VerifyOtpResponse) {
                                if ((it as VerifyOtpResponse).message.equals(BaaSConstantsUI.OTP_VERIFIED)) {
                                    showToastMessage(it.message)
                                    callNextScreen(
                                        Intent(
                                            applicationContext,
                                            UserProfileActivity::class.java
                                        ), null
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
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = getString(R.string.wrong_otp_message)
                binding.verify.isEnabled = false
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_OTP -> {
                        viewModel.verifyNewEmail()
                    }
                }
            }
        }
    }
    private fun moveToNext() {
        when {
            binding.enterOtp1.text.toString().isEmpty() -> {
                binding.enterOtp1.background = resources.getDrawable(R.drawable.otp_white_border_view)
                binding.enterOtp1.requestFocus()
                binding.enterOtp1.isCursorVisible = true

                binding.enterOtp2.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(R.drawable.otp_background)

                binding.verify.isEnabled = false
            }
            binding.enterOtp2.text.toString().isEmpty() -> {
                binding.enterOtp2.background = resources.getDrawable(R.drawable.otp_white_border_view)
                binding.enterOtp2.requestFocus()
                binding.enterOtp2.isCursorVisible = true

                binding.enterOtp1.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(R.drawable.otp_background)

                binding.verify.isEnabled = false
            }
            binding.enterOtp3.text.toString().isEmpty() -> {
                binding.enterOtp3.background = resources.getDrawable(R.drawable.otp_white_border_view)
                binding.enterOtp3.requestFocus()
                binding.enterOtp3.isCursorVisible = true

                binding.enterOtp1.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(R.drawable.otp_background)

                binding.verify.isEnabled = false
            }
            binding.enterOtp4.text.toString().isEmpty() -> {
                binding.enterOtp4.background = resources.getDrawable(R.drawable.otp_white_border_view)
                binding.enterOtp4.requestFocus()
                binding.enterOtp4.isCursorVisible = true

                binding.enterOtp1.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(R.drawable.otp_background)

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

                binding.enterOtp1.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp2.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp3.background = resources.getDrawable(R.drawable.otp_background)
                binding.enterOtp4.background = resources.getDrawable(R.drawable.otp_background)

                binding.verify.isEnabled = true
            }
        }
    }

}