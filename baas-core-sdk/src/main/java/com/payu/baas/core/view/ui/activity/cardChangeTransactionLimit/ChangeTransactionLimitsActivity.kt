package com.payu.baas.core.view.ui.activity.cardChangeTransactionLimit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityChangeTransactionLimitsBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.model.TransactionLimitModel
import com.payu.baas.core.model.responseModels.SetLimitResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TEXT_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_VERIFY_PAN_BUTTON_CLICK
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import java.util.*

class ChangeTransactionLimitsActivity : BaseActivity() {
    lateinit var binding: ActivityChangeTransactionLimitsBinding
    private lateinit var viewModel: ChangeTransactionLimitViewModel

    //    var oldLimitValue = 10000
    var maxLimitForAtmWithdrawal = 20000
    var maxLimitForCardSwipe = 50000
    var maxLimitForOnlineTransaction = 50000
    lateinit var oldSwipePerTransactionValue: String
    lateinit var oldSwipeDailyValue: String
    lateinit var oldTransPerTransactionValue: String
    lateinit var oldTransDailyValue: String
    lateinit var oldAtmPerTransactionValue: String
    lateinit var oldAtmDailyValue: String
    var CODE = 103
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_transaction_limits)
        activity = this
        setMaxLimitForAtmWithdrawal()
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.setTextForLimitTitles()
        if (intent.extras!!.containsKey(BaaSConstantsUI.BS_KEY_TRANSACTION_LIMIT_VALUES)) {
            var transactionLimitsString =
                intent.getStringExtra(BaaSConstantsUI.BS_KEY_TRANSACTION_LIMIT_VALUES)
            var transactionLimits =
                JsonUtil.toObject(transactionLimitsString, TransactionLimitModel::class.java)
                        as TransactionLimitModel
            viewModel.setLimtValues(transactionLimits!!)
            setOldLimts(transactionLimits!!)

        }
        onTextChange(binding.lSwipeLimit.etNewPerTransaction)
        onTextChange(binding.lSwipeLimit.etNewDailyLimit)
        onTextChange(binding.lTransactionLimit.etNewPerTransaction)
        onTextChange(binding.lTransactionLimit.etNewDailyLimit)
        onTextChange(binding.lAtmWithdrawalLimit.etNewPerTransaction)
        onTextChange(binding.lAtmWithdrawalLimit.etNewDailyLimit)
        binding.btSave.isEnabled = false
    }

    fun setOldLimts(transactionLimits: TransactionLimitModel) {
        oldSwipePerTransactionValue = transactionLimits.swipePerTransaction!!
        oldSwipeDailyValue = transactionLimits.swipeDaily!!
        oldAtmPerTransactionValue = transactionLimits.atmPerTransaction!!
        oldAtmDailyValue = transactionLimits.atmDaily!!
        oldTransPerTransactionValue = transactionLimits.onlinePerTransaction!!
        oldTransDailyValue = transactionLimits.onlineDaily!!
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ChangeTransactionLimitViewModel.ChangeTransactionLimitViewModelFactory(this, this)
        )[ChangeTransactionLimitViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getLimitResponseObs().observe(this, {
            parseResponse(it, ApiName.SET_LIMITS)
        })
        viewModel.getAccessTokenResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.SET_LIMITS -> {
                        it.data?.let {
                            if (it is SetLimitResponse) {
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                                return
                            }
                        }
                    }
//                    /* No other call in this screen hence recalled the setLimits
//                     after generating accessToken
//                   */
//                    ApiName.GET_CLIENT_TOKEN -> {
//                        viewModel.setTransactionLimits()
//                    }
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
                        binding.nested,
                        msg,
                        false
                    )
                }
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                if (!(it.message.isNullOrEmpty()))
//                    if (it.message.contains("error") || it.message.contains("msg")) {
//                        viewModel.baseCallBack?.showToastMessage(it.message)
//                    } else if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
//                            BaaSConstantsUI.DEVICE_BINDING_FAILED
//                        )
//                    ) {
//                        viewModel.reGenerateAccessToken(false)
//                    }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.SET_LIMITS -> {
                        viewModel.setTransactionLimits()
                    }
                }
            }
        }
    }

    fun setMaxLimitForAtmWithdrawal() {
        binding.lAtmWithdrawalLimit.tvPerTransactionMaxLimit.setText(resources.getString(R.string.max_limit_value_for_atm_withdrawal_text))
        binding.lAtmWithdrawalLimit.tvDailyMaxLimit.setText(resources.getString(R.string.max_limit_value_for_atm_withdrawal_text))
    }

    private fun onTextChange(editText: EditText?) {
        editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (viewModel.strSwipePerTransactionValue.get()!!.trim()!!
                        .equals(oldSwipePerTransactionValue) == true && viewModel.strSwipeDailyValue.get()!!
                        .trim()!!.equals(oldSwipeDailyValue) == true
                    && viewModel.strTransPerTransactionValue.get()!!.trim()!!
                        .equals(oldTransPerTransactionValue) == true && viewModel.strTransDailyValue.get()!!
                        .trim()!!.equals(oldTransDailyValue) == true
                    && viewModel.strAtmPerTransactionValue.get()!!.trim()!!
                        .equals(oldAtmPerTransactionValue) == true && viewModel.strAtmDailyValue.get()!!
                        .trim()!!.equals(oldAtmDailyValue) == true
                ) {
                    hideAllErrors()
                    binding.btSave.isEnabled = false
                } else {
                    onChangeCheckForMaxLimit()
//                    binding.btSave.isEnabled = true
                }
            }
        })
    }

    fun hideAllErrors() {
        showOrHideErrorForCrossingSwipePTMaxLimit(false)
        showOrHideErrorForCrossingSwipeDailyMaxLimit(false)
        showOrHideErrorForCrossingAtmDailyMaxLimit(false)
        showOrHideErrorForCrossingAtmPTMaxLimit(false)
        showOrHideErrorForCrossingOnlineDailyMaxLimit(false)
        showOrHideErrorForCrossingOnlinePTMaxLimit(false)
    }

    fun onChangeCheckForMaxLimit() {
        // Swipe Per Transaction max limit check
        if (!viewModel.strSwipePerTransactionValue.get()
                .isNullOrEmpty() && viewModel.strSwipePerTransactionValue.get()!!.trim()!!
                .toLong() > maxLimitForCardSwipe
        ) {
            showOrHideErrorForCrossingSwipePTMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingSwipePTMaxLimit(false)
//            binding.btSave.isEnabled = true
        }
        // Swipe Daily max limit check
        if (!viewModel.strSwipeDailyValue.get()
                .isNullOrEmpty() && viewModel.strSwipeDailyValue.get()!!.trim()!!
                .toLong() > maxLimitForCardSwipe
        ) {
            showOrHideErrorForCrossingSwipeDailyMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingSwipeDailyMaxLimit(false)
//            binding.btSave.isEnabled = true
        }

        // ATM Withdrwals Daily max limit check
        if (!viewModel.strAtmDailyValue.get()
                .isNullOrEmpty() && viewModel.strAtmDailyValue.get()!!.trim()!!
                .toLong() > maxLimitForAtmWithdrawal
        ) {
            showOrHideErrorForCrossingAtmDailyMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingAtmDailyMaxLimit(false)
//            binding.btSave.isEnabled = true
        }
        // ATM Withdrwals Per Transaction max limit check
        if (!viewModel.strAtmPerTransactionValue.get()
                .isNullOrEmpty() && viewModel.strAtmPerTransactionValue.get()!!.trim()!!
                .toLong() > maxLimitForAtmWithdrawal
        ) {
            showOrHideErrorForCrossingAtmPTMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingAtmPTMaxLimit(false)
//            binding.btSave.isEnabled = true
        }
        // Online Daily max limit check
        if (!viewModel.strTransDailyValue.get()
                .isNullOrEmpty() && viewModel.strTransDailyValue.get()!!.trim()!!
                .toLong() > maxLimitForOnlineTransaction
        ) {
            showOrHideErrorForCrossingOnlineDailyMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingOnlineDailyMaxLimit(false)
//            binding.btSave.isEnabled = true
        }
        // Online Per Transaction max limit check
        if (!viewModel.strTransPerTransactionValue.get()
                .isNullOrEmpty() && viewModel.strTransPerTransactionValue.get()!!.trim()!!
                .toLong() > maxLimitForOnlineTransaction
        ) {
            showOrHideErrorForCrossingOnlinePTMaxLimit(true)
//            binding.btSave.isEnabled = false
        } else {
            showOrHideErrorForCrossingOnlinePTMaxLimit(false)
//            binding.btSave.isEnabled = true
        }

        if (binding.lTransactionLimit.ivErrorIconForTransactionLimit.visibility == View.VISIBLE ||
            binding.lTransactionLimit.ivErrorIconForDailyLimit.visibility == View.VISIBLE ||
            binding.lAtmWithdrawalLimit.ivErrorIconForTransactionLimit.visibility == View.VISIBLE ||
            binding.lAtmWithdrawalLimit.ivErrorIconForDailyLimit.visibility == View.VISIBLE ||
            binding.lSwipeLimit.ivErrorIconForTransactionLimit.visibility == View.VISIBLE ||
            binding.lSwipeLimit.ivErrorIconForDailyLimit.visibility == View.VISIBLE
        )
            binding.btSave.isEnabled = false
        else
            binding.btSave.isEnabled = true
    }

    fun showOrHideErrorForCrossingSwipeDailyMaxLimit(show: Boolean) {
        if (show) {
            binding.lSwipeLimit.ivErrorIconForDailyLimit.visibility = View.VISIBLE
            binding.lSwipeLimit.tvErrorMsgForDailyLimit.visibility = View.VISIBLE
        } else {
            binding.lSwipeLimit.ivErrorIconForDailyLimit.visibility = View.GONE
            binding.lSwipeLimit.tvErrorMsgForDailyLimit.visibility = View.GONE
        }
    }

    fun showOrHideErrorForCrossingSwipePTMaxLimit(show: Boolean) {
        if (show) {
            binding.lSwipeLimit.ivErrorIconForTransactionLimit.visibility = View.VISIBLE
            binding.lSwipeLimit.tvErrorMsgForPerTransaction.visibility = View.VISIBLE
        } else {
            binding.lSwipeLimit.ivErrorIconForTransactionLimit.visibility = View.GONE
            binding.lSwipeLimit.tvErrorMsgForPerTransaction.visibility = View.GONE
        }
    }

    fun showOrHideErrorForCrossingAtmDailyMaxLimit(show: Boolean) {
        if (show) {
            binding.lAtmWithdrawalLimit.ivErrorIconForDailyLimit.visibility = View.VISIBLE
            binding.lAtmWithdrawalLimit.tvErrorMsgForDailyLimit.visibility = View.VISIBLE
        } else {
            binding.lAtmWithdrawalLimit.ivErrorIconForDailyLimit.visibility = View.GONE
            binding.lAtmWithdrawalLimit.tvErrorMsgForDailyLimit.visibility = View.GONE
        }
    }

    fun showOrHideErrorForCrossingAtmPTMaxLimit(show: Boolean) {
        if (show) {
            binding.lAtmWithdrawalLimit.ivErrorIconForTransactionLimit.visibility = View.VISIBLE
            binding.lAtmWithdrawalLimit.tvErrorMsgForPerTransaction.visibility = View.VISIBLE
        } else {
            binding.lAtmWithdrawalLimit.ivErrorIconForTransactionLimit.visibility = View.GONE
            binding.lAtmWithdrawalLimit.tvErrorMsgForPerTransaction.visibility = View.GONE
        }
    }

    fun showOrHideErrorForCrossingOnlineDailyMaxLimit(show: Boolean) {
        if (show) {
            binding.lTransactionLimit.ivErrorIconForDailyLimit.visibility = View.VISIBLE
            binding.lTransactionLimit.tvErrorMsgForDailyLimit.visibility = View.VISIBLE
        } else {
            binding.lTransactionLimit.ivErrorIconForDailyLimit.visibility = View.GONE
            binding.lTransactionLimit.tvErrorMsgForDailyLimit.visibility = View.GONE
        }
    }

    fun showOrHideErrorForCrossingOnlinePTMaxLimit(show: Boolean) {
        if (show) {
            binding.lTransactionLimit.ivErrorIconForTransactionLimit.visibility = View.VISIBLE
            binding.lTransactionLimit.tvErrorMsgForPerTransaction.visibility = View.VISIBLE
        } else {
            binding.lTransactionLimit.ivErrorIconForTransactionLimit.visibility = View.GONE
            binding.lTransactionLimit.tvErrorMsgForPerTransaction.visibility = View.GONE
        }
    }

    fun openPreviousScreen(view: android.view.View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_EDOT_TRANSACTION_LIMITS_GO_BACK,
            BaaSConstantsUI.CL_USER_EDIT_TRANSACTION_LIMITS_GO_BACK_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        finish()
    }

    fun changeAndSave(view: android.view.View) {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_CHANGE_AND_SAVE_TRANASACTION_LIMIT,
            BaaSConstantsUI.CL_USER_CHANGE_AND_SAVE_TRANASACTION_LIMIT_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        CL_USER_FORGOT_PASSCODE_TEXT_CLICK = "3"
        CL_USER_VERIFY_PAN_BUTTON_CLICK = "3"
        CL_USER_RESET_PASSCODE_BUTTON_CLICK = "3"
        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
            BaaSConstantsUI.BS_KEY_IS_FROM_CHANGED_TRANSACTION_LIMIT_SCREEN
        )
        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == CODE
        ) {
            viewModel.setTransactionLimits()
        } else if (resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
            return
        }
    }
}