package com.payu.baas.core.view.ui.activity.moneyTransfer

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityTransactionSuccessBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.AlertDialogMultipleCallback
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.passbook.PassBookViewModel
import com.payu.baas.core.view.ui.activity.passbook.TransactionDetailActivity
import java.util.*

class TransactionSuccessActivity : BaseActivity(), ClickHandler {


    private var mBinding: ActivityTransactionSuccessBinding? = null
    private var mViewModel: PassBookViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_success)
        activity = this

        setupViewModel()
        setupObserver()
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PassBookViewModel.PassBookViewModelFactory(this, this, this)
        )[PassBookViewModel::class.java]

        /**
         * Here we get Transaction ID from bundle
         */
        intent.extras?.let { bundle ->
            if (bundle.containsKey(
                    BaaSConstantsUI
                        .ARGUMENTS_TRANSACTION_ID
                )
            ) {
                val transactionID = bundle.getString(
                    BaaSConstantsUI
                        .ARGUMENTS_TRANSACTION_ID
                )
                mViewModel?.transactionID = transactionID

                //Here we call API to fetch transaction details based on transaction id
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        mViewModel?.getTransactionStatusDetails()
                    }
                }, 1000)
            }
        }
    }

    private fun setupObserver() {
        mViewModel?.getTransactionStatusDetailsResponseObs()?.observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_DETAIL)
        }
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {

        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_TRANSACTION_DETAIL -> {
                        it.data?.let {
                            if (it is TransactionDetailsResponse) {
                                showTransactionDetails(it)
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
            Status.LOADING -> {
                showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()
                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                        )
                    ) {
                        mViewModel!!.reGenerateAccessToken(false)
                    } else {

                        try {
                            val errorResponse = it.data as ErrorResponse
                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                                && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                            ) {
                                showTechinicalError()
                            } else {
                                var msg = ""
                                try {
                                    val errorUiRes = JsonUtil.toObject(
                                        it.message,
                                        ErrorResponseUI::class.java
                                    ) as ErrorResponseUI
                                    msg = errorUiRes.userMessage!!
                                } catch (e: Exception) {
                                    msg = it.message
                                }
                                mBinding?.let { binding ->
                                    UtilsUI.showSnackbarOnSwitchAction(
                                        binding.root,
                                        msg,
                                        false
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            mBinding?.let { binding ->
                                UtilsUI.showSnackbarOnSwitchAction(
                                    binding.root,
                                    it.message,
                                    false
                                )
                            }
                        }
                    }
            }
            Status.RETRY -> {
                hideProgress()

                showAlertWithMultipleCallBack(
                    getString(R.string.error),
                    it.message ?: "",
                    getString(R.string.re_try),
                    getString(R.string.cancel),
                    object : AlertDialogMultipleCallback {
                        override fun onPositiveActionButtonClick() {
                            mViewModel?.getTransactionStatusDetails()
                        }

                        override fun onNegativeActionButtonClick() {
                        }

                    }
                )
            }
        }
    }

    /**
     * Show Transaction Details
     */
    private fun showTransactionDetails(model: TransactionDetailsResponse) {

        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL,
            Gson().toJson(model)
        )

        intent.extras?.let { bndl ->
            if (bndl.containsKey(
                    BaaSConstantsUI
                        .ARGUMENTS_AMOUNT_CHARGED
                )
            ) {
                bundle.putString(
                    BaaSConstantsUI.ARGUMENTS_AMOUNT_CHARGED,
                    bndl.getString(
                        BaaSConstantsUI
                            .ARGUMENTS_AMOUNT_CHARGED
                    )
                )
            }
        }

        bundle.putString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE, "MoneyTransfer")
        val intent = Intent(this, TransactionDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

        finish()
    }

    override fun click(view: View) {

    }

}