package com.payu.baas.core.view.ui.activity.editEmailAddress

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityEditEmailAddressBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.UpdateEmailResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.*
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.myDetails.MyDetailsActivity
import java.util.*

class EditEmailAddressActivity : BaseActivity() {
    lateinit var binding: ActivityEditEmailAddressBinding
    private lateinit var viewModel: EditEmailAddressViewModel
    var verifyEmail:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_email_address)
        activity = this
        setupUI()
        setupViewModel()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getUpdateEmailResponseObs().observe(this, {
            parseResponse(it, ApiName.UPDATE_USER_EMAIL)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            EditEmailAddressViewModel.EditEmailtAddressViewModelFactory(this, this)
        )[EditEmailAddressViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun setupUI() {
        binding.emailAddress.requestFocus()
        binding.emailAddress.isCursorVisible = true
        emailTextChange()
    }

    private fun emailTextChange() {
        binding.emailAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                verifyEmail = binding.emailAddress.text.toString()
                if (isEmailValid(verifyEmail!!)) {
                    binding.failedIcon.visibility = View.GONE
                    binding.correct.visibility = View.VISIBLE
                    binding.sendEmailOtp.isEnabled = true
                } else {
                    binding.failedIcon.visibility = View.VISIBLE
                    binding.correct.visibility = View.GONE
                    binding.sendEmailOtp.isEnabled = false
                }
            }
        })
    }

    fun edit_email_backarrow(view: View) {
       finish()
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_GO_BACK_ENTER_EMAIL,
            BaaSConstantsUI.CL_USER_GO_BACK_ENTER_EMAIL_EVENT_ID,
            "", imei,
            "",
            Date(),""
        )    }


    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_USER_EMAIL -> {
                        it.data?.let {
                            if (it is UpdateEmailResponse) {
                                viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                                    BaaSConstantsUI.CL_USER_CHANGE_EMAIL_SUCCESSFUL,
                                    BaaSConstantsUI.CL_USER_CHANGE_EMAIL_SUCCESSFULL_EVENT_ID,
                                    "", imei,
                                    SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                                    Date(),""
                                )
                                val mBundle = Bundle()
                                mBundle.putString(BaaSConstantsUI.EMAIL_ADDRESS, verifyEmail)
                                callNextScreen(Intent(this, MyDetailsActivity::class.java),mBundle)
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
                } else  if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ){
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
                    ApiName.UPDATE_USER_EMAIL -> {
                        viewModel.sendEmailOTP()
                    }
                }
            }
        }
    }
}