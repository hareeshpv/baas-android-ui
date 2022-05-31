package com.payu.baas.core.view.ui.activity.advance

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityAdvanceTransactionDetailBinding
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.UtilsUI
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.callback.ImageLoadingListener
import com.payu.baas.core.view.ui.BaseActivity


class AdvanceTransactionDetailActivity : BaseActivity(), ClickHandler {

    private lateinit var mBinding: ActivityAdvanceTransactionDetailBinding
    private lateinit var mViewModel: AdvanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_advance_transaction_detail)
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
            mBinding.tvTopNote,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvBottomTitle,
            SessionManagerUI.getInstance(this).accentColor
        )
    }

    private fun setupUI() {
        mBinding.llShareOnWhatsapp.visibility = View.GONE
        intent.extras?.let { bundle ->
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL)) {
                val str =
                    bundle.getString(BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL) as String
                if (str.isNotEmpty()) {
                    val transactionDetailModel =
                        Gson().fromJson(str, TransactionDetailsResponse::class.java)
                    mBinding.let {
                        UtilsUI.loadUrl(
                            it.ivTransactionStatusIcon,
                            transactionDetailModel.statusIcon,
                            null,object : ImageLoadingListener {
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
                        it.tvTransactionAmount.text = transactionDetailModel.amount
                        it.tvTransactionId.text = String.format(
                            getString(R.string.label_transaction_reference_id),
                            transactionDetailModel.refId
                        )
                        it.tvTransactionTimeStamp.text = transactionDetailModel.dateTime
                        it.tvTopSalutation.text = transactionDetailModel.topSalutation
                        it.tvTopTitle.text = transactionDetailModel.topTitle
                        it.tvTopNote.text = transactionDetailModel.notes
                        UtilsUI.loadUrl(it.ivTop, transactionDetailModel.topIcon, null,object : ImageLoadingListener {
                            override fun loadingFailed() {

                            }
                        })
                        it.tvBottomSalutation.text = transactionDetailModel.bottomSalutation
                        it.tvBottomTitle.text = transactionDetailModel.bottomTitle
                        UtilsUI.loadUrl(it.ivBottom, transactionDetailModel.bottomIcon, null,object : ImageLoadingListener {
                            override fun loadingFailed() {

                            }
                        })
                        it.llShareOnWhatsapp.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            AdvanceViewModel.AdvanceViewModelFactory(this, this, this)
        )[AdvanceViewModel::class.java]

        mBinding.viewModel = mViewModel
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llCancel -> {
                finish()
            }
            R.id.tvNeedHelp -> {
            }
            R.id.llShareOnWhatsapp -> {
            }
        }
    }
}