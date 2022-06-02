package com.payu.baas.coreUI.view.ui.activity.passbook

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityTransactionDetailBinding
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.view.callback.ClickHandler
import com.payu.baas.coreUI.view.callback.ImageLoadingListener
import com.payu.baas.coreUI.view.ui.BaseActivity
import java.util.*


class TransactionDetailActivity : BaseActivity(), ClickHandler {

    private lateinit var mBinding: ActivityTransactionDetailBinding
    private lateinit var mViewModel: PassBookViewModel
    private var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_detail)
        activity = this

        applyThemeToUI()
        setupUI()
        setupViewModel()
    }


    private fun applyThemeToUI() {

        UtilsUI.applyColorToIcon(
            mBinding.ivCancel,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvNarration,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvCurrencySymbol,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTransactionAmount,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTopTitle,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTopShortName,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvBottomTitle,
            SessionManagerUI.getInstance(this).accentColor
        )
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        intent.extras?.let { bundle ->
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL)) {
                val str =
                    bundle.getString(BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL) as String
                if (str.isNotEmpty()) {
                    val transactionDetailModel =
                        Gson().fromJson(str, TransactionDetailsResponse::class.java)
                    mBinding.let {

                        if (userFromMoneyTransferModule()) {

                            var transactionChagres = ""
                            intent.extras?.let { bundle ->
                                if (bundle.containsKey(
                                        BaaSConstantsUI
                                            .ARGUMENTS_AMOUNT_CHARGED
                                    )
                                ) {
                                    transactionChagres = bundle.getString(
                                        BaaSConstantsUI
                                            .ARGUMENTS_AMOUNT_CHARGED,
                                        ""
                                    )
                                }
                            }
                            cleverTapUserMoneyTransferEvent(
                                BaaSConstantsUI.CL_MONEY_TRANSFER_EXECUTED,
                                BaaSConstantsUI.CL_MONEY_TRANSFER_EXECUTED_EVENT_ID,
                                SessionManager.getInstance(this).accessToken,
                                imei,
                                mobileNumber,
                                Date(),
                                transactionDetailModel.amount.toString(),
                                transactionChagres,
                                transactionDetailModel.narration ?: "",
                                "",
                                ""
                            )
                        } else if (userFromPassBookModule()) {
                            cleverTapUserPassbookEvent(
                                BaaSConstantsUI.CL_PASSBOOK_VIEW_TXN_STATUS_AND_INFO,
                                BaaSConstantsUI.CL_PASSBOOK_VIEW_TXN_STATUS_AND_INFO_EVENT_ID,
                                SessionManager.getInstance(this).accessToken,
                                imei,
                                mobileNumber,
                                Date(),
                                "",
                                "",
                                "",
                                transactionDetailModel.transactionId ?: "",
                                transactionDetailModel.narration ?: "",
                                ""
                            )
                        }


                        UtilsUI.loadUrl(
                            it.ivTransactionStatusIcon,
                            transactionDetailModel.statusIcon,
                            null,
                            object : ImageLoadingListener {
                                override fun loadingFailed() {

                                }

                            }
                        )
                        it.tvNarration.text = transactionDetailModel.narration
                        transactionDetailModel.extraNotes?.let { extraNotes ->
                            it.tvExtraNotes.text = extraNotes

                            var colorCode = SessionManagerUI.getInstance(this).accentColor
                            if (extraNotes.isNotEmpty()) {
                                when (transactionDetailModel.statusIcon) {
                                    "Pending.svg" -> {
                                        colorCode = ContextCompat.getColor(
                                            this,
                                            R.color.transaction_pending
                                        )
                                    }
                                    "Failed.svg" -> {
                                        colorCode = ContextCompat.getColor(
                                            this,
                                            R.color.transaction_failed
                                        )
                                    }
                                    "Success.svg" -> {
                                        //Same as default color
                                    }
                                    else -> {

                                    }
                                }
                            }
                            it.tvExtraNotes.setTextColor(colorCode)
                        }
                        it.tvTransactionAmount.text = transactionDetailModel.amount.toString()
                        it.tvTransactionId.text = String.format(
                            getString(R.string.label_transaction_reference_id),
                            transactionDetailModel.refId
                        )
                        it.tvTransactionTimeStamp.text = transactionDetailModel.dateTime
                        it.tvTopSalutation.text = transactionDetailModel.topSalutation
                        it.tvTopTitle.text = transactionDetailModel.topTitle

                        if (transactionDetailModel.notes.isNullOrEmpty().not()) {
                            it.rlTopNotes.visibility = View.VISIBLE
                            it.tvBankDetails.text = transactionDetailModel.notes
                        }

                        it.ivTop.visibility = View.GONE
                        it.tvTopShortName.visibility = View.GONE
                        if (transactionDetailModel.topIcon.isNullOrEmpty().not()) {
                            it.ivTop.visibility = View.VISIBLE
                            UtilsUI.loadUrl(it.ivTop, transactionDetailModel.topIcon, null,
                                object : ImageLoadingListener {
                                    override fun loadingFailed() {
                                        it.ivTop.visibility = View.GONE
                                        it.tvTopShortName.visibility = View.VISIBLE
                                        it.tvTopShortName.text =
                                            UtilsUI.getShortName(transactionDetailModel.topTitle)
                                    }
                                })
                        } else {
                            it.tvTopShortName.visibility = View.VISIBLE
                            it.tvTopShortName.text =
                                UtilsUI.getShortName(transactionDetailModel.topTitle)
                        }

                        it.tvBottomSalutation.text = transactionDetailModel.bottomSalutation
                        it.tvBottomTitle.text = transactionDetailModel.bottomTitle
                        if (transactionDetailModel.bottomIcon.isNullOrEmpty().not())
                            UtilsUI.loadUrl(it.ivBottom, transactionDetailModel.bottomIcon, null,
                                object : ImageLoadingListener {
                                    override fun loadingFailed() {

                                    }

                                })
                        else
                            it.viewBottom.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PassBookViewModel.PassBookViewModelFactory(this, this, this)
        )[PassBookViewModel::class.java]
        mBinding.viewModel = mViewModel
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llCancel -> {
                finish()
            }
            R.id.tvNeedHelp -> {

//                cleverTapUserMoneyTransferEvent(
//                    BaaSConstantsUI.CL_NEED_HELP,
//                    BaaSConstantsUI.CL_NEED_HELP_EVENT_ID,
//                    SessionManager.getInstance(this).accessToken,
//                    imei,
//                    mobileNumber,
//                    Date(),
//                    "",
//                    "",
//                    "",
//                    ""
//                )
//                mZendeskViewModel.getZendeskDetails()
                if (userFromMoneyTransferModule()) {
                    cleverTapUserMoneyTransferEvent(
                        BaaSConstantsUI.CL_NEED_HELP,
                        BaaSConstantsUI.CL_NEED_HELP_EVENT_ID,
                        SessionManager.getInstance(this).accessToken,
                        imei,
                        mobileNumber,
                        Date(),
                        "",
                        "",
                        "",
                        "",
                        "Help"
                    )
                } else if (userFromPassBookModule()) {
                    cleverTapUserPassbookEvent(
                        BaaSConstantsUI.CL_PASSBOOK_HELP_TXN_PASSBOOK,
                        BaaSConstantsUI.CL_PASSBOOK_HELP_TXN_PASSBOOK_EVENT_ID,
                        SessionManager.getInstance(this).accessToken,
                        imei,
                        mobileNumber,
                        Date(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "Help"
                    )
                }
                launchZendeskSDK()
            }
            R.id.llShareOnWhatsapp -> {
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

    private fun userFromPassBookModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == "PassBook"
                ) {
                    return true

                }
            }
        }
        return false
    }


}