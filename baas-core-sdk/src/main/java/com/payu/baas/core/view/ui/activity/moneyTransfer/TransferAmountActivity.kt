package com.payu.baas.core.view.ui.activity.moneyTransfer

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityTransferAmountBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.model.BeneficiaryModel
import com.payu.baas.core.model.model.IFSCDetailsModel
import com.payu.baas.core.model.responseModels.BeneficiaryBankTransferResponse
import com.payu.baas.core.model.responseModels.GetTransactionChargesResponse
import com.payu.baas.core.model.responseModels.PrevalidateTransactionResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.callback.ImageLoadingListener
import com.payu.baas.core.view.callback.SnackBarListener
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*


class TransferAmountActivity : BaseActivity(), ClickHandler {
    private lateinit var mBinding: ActivityTransferAmountBinding
    private lateinit var mViewModel: MoneyTransferViewModel
    private var sessionID: String? = null
    private var mobileNumber: String? = null
    private var prevalidateTransactionResponse: PrevalidateTransactionResponse? = null

    private var verifyPassCode =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mViewModel.transferAmount()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_amount)
        activity = this

        applyThemeToUI()
        setupViewModel()
        setupUI() // Don't change Position
        setupObserver()
    }

    private fun applyThemeToUI() {

        UtilsUI.applyColorToIcon(
            mBinding.ivBackArrow,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvToolbarTitle,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvShortName,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvPayeeName,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvBankName,
            Color.parseColor("#555F63")
        )

        UtilsUI.applyColorToText(
            mBinding.tvCurrencySymbol,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.etAmount,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorAsHint(
            mBinding.etAmount,
            ContextCompat.getColor(this, R.color.powered_by_payu)
        )

        UtilsUI.applyColorToText(
            mBinding.etNote,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorAsHint(
            mBinding.etNote,
            ContextCompat.getColor(this, R.color.subtext_color)
        )

        UtilsUI.applyColorToText(
            mBinding.tvBankTransferFree,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvBankTransferPaid,
            SessionManagerUI.getInstance(this).accentColor
        )

    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MoneyTransferViewModel.MoneyTransferViewModelFactory(this, this, this)
        )[MoneyTransferViewModel::class.java]
        mBinding.viewModel = mViewModel
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        intent.extras?.let { bundle ->
            val gson = Gson()
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_BENEFICIARY_MODEL)) {
                val str = bundle.getString(BaaSConstantsUI.ARGUMENTS_BENEFICIARY_MODEL)
                str?.let {
                    if (it.isNotEmpty()) {
                        val beneficiaryModel = gson.fromJson(it, BeneficiaryModel::class.java)
                        mViewModel.addedBeneficiary = beneficiaryModel
                    }
                }
            }
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_IFSC_CODE_DETAIL_MODEL)) {
                val str = bundle.getString(BaaSConstantsUI.ARGUMENTS_IFSC_CODE_DETAIL_MODEL)
                str?.let {
                    if (it.isNotEmpty()) {
                        val ifscDetailModel = gson.fromJson(it, IFSCDetailsModel::class.java)
                        mViewModel.verifiedIFSCDetails = ifscDetailModel
                    }
                }
            }
        }

        //Bind Bank Icon
        var strBankName: String? = null
        mViewModel.verifiedIFSCDetails?.let {
            strBankName = it.bank
            if (!it.icon.isNullOrEmpty())
                UtilsUI.loadUrl(mBinding.ivBankIcon, it.icon, null,
                    object : ImageLoadingListener {
                        override fun loadingFailed() {

                        }

                    })
        }
        if (mViewModel.addedBeneficiary != null) {
            mBinding.tvShortName.text =
                UtilsUI.getShortName(mViewModel.addedBeneficiary?.beneficiaryName)
            mBinding.tvPayeeName.text =
                UtilsUI.toCamelCase(mViewModel.addedBeneficiary?.beneficiaryName)
            mBinding.tvBankName.text = (strBankName ?: "")
                .plus(" A/c XX ")
                .plus(mViewModel.addedBeneficiary?.last4Digits ?: "")
        } else {
            finish()
        }

        showNumericKeyboardWithDecimal(this, mBinding.etAmount)
        mBinding.etAmount.filters = arrayOf<InputFilter>(InputFilterMinMax(0.0, 200000.0))
        mBinding.tvAmountExceedsErrorMessage.visibility = View.GONE
        mBinding.tvBankTransferPaid.visibility = View.GONE
        mBinding.clBankTransferFree.visibility = View.GONE
    }

    private fun setupObserver() {
        mViewModel.getTransactionChargesResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_CHARGES)
        }

        mViewModel.getPreValidateTransactionResponseObs().observe(this) {
            parseResponse(it, ApiName.PREVALIDATE_TRANSACTION)
        }

        mViewModel.getTransferAmountResponseObs().observe(this) {
            parseResponse(it, ApiName.BENEFICIARY_BANK_TRANSFER)
        }

        mViewModel.getTransactionCharges()
    }

    override fun click(view: View) {
        if (view.id == R.id.llGoBack) {
            onBackPressed()
        } else if (view.id == R.id.btnSendMoney) {

            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_MONEY_TRANSFER_ATTEMPT,
                BaaSConstantsUI.CL_MONEY_TRANSFER_ATTEMPT_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(),
                "",
                "",
                "",
                mViewModel.addedBeneficiary?.ifsc ?: "",
                ""
            )

            //Hide Keyboard
            hideKeyboard(this)
            mViewModel.preValidateTransaction()
        }
    }


    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()

                when (apiName) {
                    ApiName.GET_TRANSACTION_CHARGES -> {
                        it.data?.let {
                            if (it is GetTransactionChargesResponse) {
                                if (it.charges == 0.0) {
                                    mBinding.clBankTransferFree.visibility = View.VISIBLE
                                } else {
                                    mBinding.tvBankTransferPaid.visibility = View.VISIBLE
                                    mBinding.tvBankTransferPaid.text =
                                        String.format(
                                            getString(R.string.bank_transfer_charges),
                                            getString(R.string.currency_symbol),
                                            it.charges.toString()
                                        )
                                }
                            }
                        }
                    }
                    ApiName.PREVALIDATE_TRANSACTION -> {
                        it.data?.let {
                            if (it is PrevalidateTransactionResponse) {
                                prevalidateTransactionResponse = it

                                if (mViewModel.strEnteredAmount.get().toString().toDouble() >
                                    it.maxAmountTransfer
                                ) {
                                    mBinding.tvAmountExceedsErrorMessage.visibility =
                                        View.VISIBLE
                                    mBinding.tvAmountExceedsErrorMessage.text =
                                        String.format(
                                            getString(R.string.transfer_amount_exceeds_limit),
                                            getString(R.string.currency_symbol),
                                            it.maxAmountTransfer.toString()
                                        )
                                } else {
                                    if (it.chargedAmount == 0.0
                                        && it.advanceChargeAmount == 0.0
                                    ) {
                                        goToPassCodeVerificationActivity()
                                    } else {
                                        showConfirmationDialog(it)
                                    }
                                }

                            }
                        }
                    }
                    ApiName.BENEFICIARY_BANK_TRANSFER -> {
                        it.data?.let {
                            if (it is BeneficiaryBankTransferResponse) {
                                if (it.transactionLogsList.isNullOrEmpty()) {
                                    //
                                } else {
                                    it.transactionLogsList?.let { list ->
                                        list[0].txnLogId?.let { transactionID ->

                                            val bundle = Bundle()
                                            bundle.putString(
                                                BaaSConstantsUI.ARGUMENTS_TRANSACTION_ID,
                                                transactionID
                                            )

                                            prevalidateTransactionResponse?.let { prevalidateTransactionModel ->
                                                if (mViewModel.strEnteredAmount.get().toString()
                                                        .toDouble() <=
                                                    prevalidateTransactionModel.maxAmountTransfer
                                                    && (prevalidateTransactionModel.chargedAmount != 0.0
                                                            || prevalidateTransactionModel.advanceChargeAmount != 0.0)

                                                ) {
                                                    bundle.putString(
                                                        BaaSConstantsUI.ARGUMENTS_AMOUNT_CHARGED,
                                                        UtilsUI.convertAmountFormat(
                                                            prevalidateTransactionModel.chargedAmount + prevalidateTransactionModel.advanceChargeAmount
                                                        )
                                                    )
                                                }
                                            }

                                            val intent =
                                                Intent(this, TransactionSuccessActivity::class.java)
                                            intent.putExtras(bundle)
                                            startActivity(intent)

                                            setResult(RESULT_OK)

                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
            Status.LOADING -> {
                showProgress("")
                if (apiName == ApiName.PREVALIDATE_TRANSACTION) {
                    if (mBinding.tvAmountExceedsErrorMessage.visibility == View.VISIBLE) {
                        mBinding.tvAmountExceedsErrorMessage.visibility =
                            View.GONE
                        mBinding.tvAmountExceedsErrorMessage.text = ""
                    }
                }
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()

                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                        )
                    ) {
                        mViewModel.reGenerateAccessToken(false)
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
                                UtilsUI.showSnackbarOnSwitchAction(
                                    mBinding.root,
                                    msg,
                                    false
                                )
                            }
                        } catch (e: Exception) {
                            UtilsUI.showSnackbarOnSwitchAction(
                                mBinding.root,
                                it.message,
                                false
                            )
                        }
                    }
            }
            Status.RETRY -> {
                hideProgress()
                if (apiName == ApiName.GET_TRANSACTION_CHARGES) {

                    var msg: String? = null
                    try {
                        val errorResponse = it.data as ErrorResponse
                        if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                            && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                        ) {
                            showTechinicalError()
                            return
                        } else {
                            try {
                                val errorUiRes = JsonUtil.toObject(
                                    it.message,
                                    ErrorResponseUI::class.java
                                ) as ErrorResponseUI
                                msg = errorUiRes.userMessage!!
                            } catch (e: Exception) {
                                msg = it.message
                            }
                        }
                    } catch (e: Exception) {
                        msg = it.message
                    }

                    msg?.let { message ->
                        val timer = Timer()

                        SimpleCustomSnackbar.retry.make(
                            mBinding.root,
                            message,
                            BaaSConstantsUI.SNACKBARD_DURATION,
                            object : SnackBarListener {
                                override fun undo() {
                                    timer.cancel()

                                    mViewModel.getTransactionCharges()
                                }
                            }
                        )?.show()

                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                finish()
                            }
                        }, BaaSConstantsUI.SNACKBARD_TIMER_DURATION)
                    }


                }
            }
        }
    }


    private fun showConfirmationDialog(model: PrevalidateTransactionResponse) {

        val fragment = TransferAmountConfirmationBottomDialogFragment.newInstance(model)
        fragment.show(supportFragmentManager, "Transfer_Amount_Confirmation")
        fragment.isCancelable = false
    }


    fun goToPassCodeVerificationActivity() {
        val bundle = Bundle()
        bundle.putString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE, "MoneyTransfer")
        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtras(bundle)
        verifyPassCode.launch(intent)
    }

    fun acceptOrCancelTransferCharges(acceptTransferChanges: Boolean) {
        if (acceptTransferChanges) {
            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_ACCEPT_CHARGES,
                BaaSConstantsUI.CL_ACCEPT_CHARGES_EVENT_ID,
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
        } else {
            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_CANCEL_TRANSFER_CHARGES,
                BaaSConstantsUI.CL_CANCEL_TRANSFER_CHARGES_EVENT_ID,
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
        }

    }

    override fun onBackPressed() {
        cleverTapUserMoneyTransferEvent(
            BaaSConstantsUI.CL_GO_BACK_PAISE_BHEJO,
            BaaSConstantsUI.CL_GO_BACK_PAISE_BHEJO_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            SessionManagerUI.getInstance(applicationContext).userMobileNumber,
            Date(),
            "",
            "",
            "",
            "",
            ""
        )

        super.onBackPressed()
    }
}