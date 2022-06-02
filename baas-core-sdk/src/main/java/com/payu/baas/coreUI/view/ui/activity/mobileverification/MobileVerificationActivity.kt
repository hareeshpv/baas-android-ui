package com.payu.baas.coreUI.view.ui.activity.mobileverification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityMobileVerificationBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.JsonUtil
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.util.Status
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.otpverification.OTPVerificationActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*


class MobileVerificationActivity : BaseActivity() {
    lateinit var binding: ActivityMobileVerificationBinding
    private lateinit var viewModel: MobileVerificationViewModel
    var mobileNumber: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_verification)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun mobileTextChange() {
        binding.mobilenumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int,
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                mobileNumber = binding.mobilenumber.text.toString().trim()
                if ((!mobileNumber!!.isNullOrEmpty())) {
                    binding.mobilenumber.post(Runnable {
                        binding.mobilenumber.setSelection(mobileNumber!!.length)
                    })
                    when {
                        mobileNumber!!.length != 10 -> {
                            if (!onlyDigits(mobileNumber!!)) {
                                binding.errorMessage.setText(R.string.mobile_number_allowed_only_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.sendOtp.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if (binding.errorMessage.visibility == View.VISIBLE)
                                            binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            } else {
                                binding.errorMessage.setText(R.string.mobile_length_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.sendOtp.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if (binding.errorMessage.visibility == View.VISIBLE)
                                            binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            }
                        }
                        mobileNumber!!.length == 10 -> {
                            if (!onlyDigits(mobileNumber!!)) {
                                binding.errorMessage.setText(R.string.mobile_number_allowed_only_error)
                                binding.errorMessage.visibility = View.VISIBLE
                                binding.sendOtp.isEnabled = false
                                binding.failedIcon.visibility = View.VISIBLE
                                binding.correct.visibility = View.GONE
                                binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
                                    if (i == EditorInfo.IME_ACTION_DONE) {
                                        if (binding.errorMessage.visibility == View.VISIBLE)
                                            binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
                                    }
                                    false
                                }
                            } else {
                                binding.failedIcon.visibility = View.GONE
                                binding.sendOtp.isEnabled = true
                                binding.correct.visibility = View.VISIBLE
                                binding.errorMessage.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        })
        // to check user pressed done or tick mark from soft keyboard
        binding.mobilenumber.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                binding.mobilenumber.startAnimation(shakeError(binding.mobilenumber))
//                Toast.makeText(applicationContext, "Done pressed", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    fun mobile_backarrow(view: View) {
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupUI() {
        var extra = intent.extras
//        if (extra != null && extra.containsKey(BaaSConstantsUI.MOBILE_NUMBER)) {
//            viewModel.strMobileNo.set(intent.getStringExtra(BaaSConstantsUI.MOBILE_NUMBER))
//        }else{
        viewModel.strMobileNo.set(SessionManagerUI.getInstance(applicationContext).userMobileNumber)
//        }
        binding.countryCode.setText(R.string.country_code)
        binding.mobilenumber.requestFocus()
        binding.mobilenumber.isCursorVisible = true
        mobileTextChange()
        showKeyboard(this, binding.mobilenumber, InputType.TYPE_CLASS_NUMBER)
        // viewModel.baseCallBack!!.showKeyboard(this, binding.mobilenumber)
//        if (!viewModel.strMobileNo.get().isNullOrEmpty()) {
//            binding.mobilenumber.post(Runnable {
//                binding.mobilenumber.setSelection(viewModel.strMobileNo.get()!!.length)
//            })
//        }
    }

    private fun setupObserver() {
        viewModel.getOtpResponseObs().observe(this) {
            parseResponse(it, ApiName.SEND_OTP)
        }
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.SEND_OTP -> {
                        it.data?.let {
                            if (it is SendOtpResponse) {
                                if ((it as SendOtpResponse).message.equals(BaaSConstantsUI.OTP_SENT)) {
                                    showToastMessage(it.message)
                                    val countryCode: String =
                                        binding.countryCode.text.toString().trim()
                                    SessionManagerUI.getInstance(applicationContext).registeredMobileNumber =
                                        "$countryCode $mobileNumber"
                                    SessionManagerUI.getInstance(applicationContext).userMobileNumber =
                                        mobileNumber
                                    viewModel.baseCallBack!!.cleverTapUserProfile(
                                        BaaSConstantsUI.CL_USER_DUMMY_NAME,
                                        mobileNumber,
                                        BaaSConstantsUI.CL_USER_DUMMY_EMAIL,
                                        mobileNumber,
                                        "",
                                        "",
                                        "",
                                        "",
                                        BaaSConstantsUI.CL_USER_PROFILE_PHOTO,
                                        ""
                                    )
                                    callNextScreen(
                                        Intent(
                                            applicationContext,
                                            OTPVerificationActivity::class.java
                                        ), null, true
                                    )

//                                    var bundle = Bundle()
//                                    bundle.putString(BaaSConstantsUI.MOBILE_NUMBER, mobileNumber)
//                                    var intent = Intent(applicationContext, OTPVerificationActivity::class.java)
//                                    intent.putExtras(bundle)
//                                    callNextScreen(intent, null,true)
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
                    if (msg.isNullOrEmpty()) {
                        SimpleCustomSnackbar.showSwitchMsg.make(
                            binding.rlParent,
                            msg,
                            Snackbar.LENGTH_LONG,
                            false
                        )?.show()
                    } else {
                        binding.errorMessage.setText(msg)
                        binding.errorMessage.visibility = View.VISIBLE
                        binding.sendOtp.isEnabled = false
                        binding.failedIcon.visibility = View.VISIBLE
                        binding.correct.visibility = View.GONE
                    }
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
            MobileVerificationViewModel.MobileVerificationViewModelFactory(this, this)
        )[MobileVerificationViewModel::class.java]
        binding.viewModel = viewModel
//        mobileVerificationBaseCallback = this
//        viewModel.baseCallBack!!.showKeyboard(this, binding.mobilenumber)
//        viewModel.mobileVerificationBaseCallback = mobileVerificationBaseCallback

    }


}
