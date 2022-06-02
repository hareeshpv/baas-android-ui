package com.payu.baas.coreUI.view.ui.activity.panvalidate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityVerifyPancardNumberBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.PanValidateResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.reset_passcode.ResetPasscodeActivity
import java.util.*

class VerifyPANCardNumberActivity : BaseActivity() {
    lateinit var binding: ActivityVerifyPancardNumberBinding
    private lateinit var viewModel: VerifyPanViewModel
    var verifyPANNumber: String? = null
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_pancard_number)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
        onPanChanged()
    }

    private fun setupObserver() {
        viewModel.getVerifyPANResponseObs().observe(this) {
            parseResponse(it, ApiName.PAN_VALIDATE)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            VerifyPanViewModel.VerifyPanViewModelFactory(this, this)
        )[VerifyPanViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack!!.showKeyboard(binding.parentPanInput.etPanNumber)
        binding.parentPanInput.etPanNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                verifyPANNumber = binding.parentPanInput.etPanNumber.text.toString()
                if (validatePanNumber(verifyPANNumber!!)) {
                    showOrHidePanErrorMessage(View.GONE)
                    binding.verifyPanNumber.isEnabled = true
                } else {
                    showOrHidePanErrorMessage(View.VISIBLE)
                    binding.verifyPanNumber.isEnabled = false
                }
            }
        }
        )

        if (userFromMoneyTransferModule())
            BaaSConstantsUI.CL_USER_VERIFY_PAN_BUTTON_CLICK = "4"
    }


    fun verify_pancard_backarrow(view: View) {
        onBackPressed()

    }

    fun showOrHidePanErrorMessage(value: Int) {
        binding.parentPanInput.tvPanError.visibility = value
        binding.parentPanInput.errorIcon.visibility = value
    }

    fun openPanCardFormat(view: View) {
        binding.parentPanHelp.parentPan.visibility = View.VISIBLE
    }

    fun closeHelp(view: View) {
        binding.parentPanHelp.parentPan.visibility = View.GONE
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.PAN_VALIDATE -> {
                        it.data?.let {
                            if (it is PanValidateResponse) {
                                //From Money Transfer Module
                                var bundle: Bundle? = null
                                if (userFromMoneyTransferModule()
                                    || userFromChangeTransactionLimitModule()
                                    || userFromCardBlockModule()) {
                                    bundle = intent.extras
                                }
                                callNextScreen(
                                    Intent(
                                        applicationContext,
                                        ResetPasscodeActivity::class.java
                                    ), bundle, true
                                )
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
                        binding.rlParent,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.PAN_VALIDATE -> {
                        viewModel.verifyPAN()
                    }
                }
            }
        }
    }

    private fun userFromMoneyTransferModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == "MoneyTransfer"
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun userFromChangeTransactionLimitModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == BaaSConstantsUI.BS_KEY_IS_FROM_CHANGED_TRANSACTION_LIMIT_SCREEN
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun userFromCardBlockModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun onPanChanged() {
        binding.parentPanInput.etPanNumber.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                if (validatePanNumber(binding.parentPanInput.etPanNumber.text.toString()))
                    showOrHidePanErrorMessage(View.GONE)
                else
                    showOrHidePanErrorMessage(View.VISIBLE)
            }
            false
        }
    }

    override fun onBackPressed() {

        //From Money Transfer Module
        if (userFromMoneyTransferModule() == true) {
            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_GO_BACK_PAN_VERIFY_FP_MONEY_TRANSFER,
                BaaSConstantsUI.CL_GO_BACK_PAN_VERIFY_FP_MONEY_TRANSFER_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(),
                "",
                "",
                "",
                "",
                ""
            )
        } else if (userFromChangeTransactionLimitModule() == true) {
            viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_TRANSACTION_LIMIT_GO_BACK,
                BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_TRANSACTION_LIMIT_GO_BACK_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(), "", ""
            )
        } else if (userFromCardBlockModule() == true) {
            viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_BLOCK_CARD_GO_BACK,
                BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_TRANSACTION_LIMIT_GO_BACK_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(), "", ""
            )
        } else {
            viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
                BaaSConstantsUI.CL_USER_BACK_VERIFY_PAN,
                BaaSConstantsUI.CL_USER_BACK_VERIFY_PAN_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                Date()
            )
        }
        finish()
    }

}