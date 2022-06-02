package com.payu.baas.coreUI.view.ui.activity.cardTransactionLimit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityTransactionLimitsBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.model.TransactionLimitModel
import com.payu.baas.core.model.responseModels.GetLimitsResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.cardChangeTransactionLimit.ChangeTransactionLimitsActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*

class TransactionLimitsActivity : BaseActivity() {
    lateinit var binding: ActivityTransactionLimitsBinding
    private lateinit var viewModel: TransactionLimitViewModel
    lateinit var transactionLimits: TransactionLimitModel
    var CODE = 104
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_limits)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
        viewModel.setTextForLimitTitles()
        viewModel.setLimtValues()
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            TransactionLimitViewModel.TransactionLimitViewModelFactory(this, this)
        )[TransactionLimitViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getLimitResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_LIMITS)
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
                    ApiName.GET_LIMITS -> {
                        it.data?.let {
                            if (it is GetLimitsResponse) {
                                transactionLimits = TransactionLimitModel()
                                transactionLimits.atmDaily =
                                    it.limitConfigs!!.atmChannel!!.daily.toString()
                                transactionLimits.atmPerTransaction =
                                    it.limitConfigs!!.atmChannel!!.perTransaction.toString()
                                transactionLimits.swipeDaily =
                                    it.limitConfigs!!.swipeChannel!!.daily.toString()
                                transactionLimits.swipePerTransaction =
                                    it.limitConfigs!!.swipeChannel!!.perTransaction.toString()
                                transactionLimits.onlineDaily =
                                    it.limitConfigs!!.ecomChannel!!.daily.toString()
                                transactionLimits.onlinePerTransaction =
                                    it.limitConfigs!!.ecomChannel!!.perTransaction.toString()
                                viewModel.updateLimtValues(transactionLimits)
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
                    if(it.message!!.contains("Missing required")){
                        viewModel.baseCallBack!!.showToastMessage("Limits not set from backend yet.")
                        UtilsUI.showSnackbarOnSwitchAction(
                            binding.parent,
                            "Limits not set from backend yet.",
                            false
                        )
                        finish()
                    }else{
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
                            binding.parent,
                            msg,
                            false
                        )
                    }
                }
//                if (!(it.message.isNullOrEmpty()))
//                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
//                            BaaSConstantsUI.DEVICE_BINDING_FAILED
//                        )
//                    ) {
//                        viewModel.reGenerateAccessToken(false)
//                    } else {
//                        viewModel.baseCallBack!!.showToastMessage(it.message)
//                        if(it.message.contains("Missing required")){
//                            viewModel.baseCallBack!!.showToastMessage("Limits not set from backend yet.")
//                            finish()
//                        }
//                    }
            }
            Status.RETRY -> {
                hideProgress()
            }
        }
    }

    fun openPreviousScreen(view: android.view.View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_VIEW_TRANSACTION_LIMITS_GO_BACK,
            BaaSConstantsUI.CL_USER_VIEW_TRANSACTION_LIMITS_GO_BACK_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        finish()
    }
    fun changeLimits(view: android.view.View) {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_CHANGE_TRANASACTION_LIMIT_ATTEMPT,
            BaaSConstantsUI.CL_USER_CHANGE_TRANASACTION_LIMIT_ATTEMPT_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        if (transactionLimits != null) {
            val bundle = Bundle()
            bundle.putString(
                BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
                BaaSConstantsUI.BS_KEY_CARD_SCREEN
            )
            bundle.putString(
                BaaSConstantsUI.BS_KEY_TRANSACTION_LIMIT_VALUES,
                com.payu.baas.coreUI.util.JsonUtil.toString(transactionLimits)
            )
            val intent = Intent(this, ChangeTransactionLimitsActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, CODE)
        }
//        var bundle = Bundle()
//        bundle.putString(
//            BaaSConstantsUI.BS_KEY_TRANSACTION_LIMIT_VALUES,
//            JsonUtil.toString(transactionLimits)
//        )
//        callNextScreen(Intent(this, ChangeTransactionLimitsActivity::class.java), bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CODE) {
            SimpleCustomSnackbar.showSwitchMsg.make(
                binding.parent, BaaSConstantsUI.TRANSACTION_LIMIT_CHANGED_SUCCESS_MSG,
                Snackbar.LENGTH_LONG, true
            )!!.show()
            viewModel.getTransactionLimits()
        }else if (resultCode == Activity.RESULT_CANCELED && requestCode == CODE) {
            SimpleCustomSnackbar.showSwitchMsg.make(
                binding.parent, BaaSConstantsUI.TRANSACTION_LIMIT_CHANGED_FAILURE_MSG,
                Snackbar.LENGTH_LONG, false
            )!!.show()
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.getTransactionLimits()
    }
}
