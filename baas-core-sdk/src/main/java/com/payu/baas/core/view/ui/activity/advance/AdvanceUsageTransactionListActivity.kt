package com.payu.baas.core.view.ui.activity.advance

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityAdvanceUsageTransactionListBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.model.AdvanceUsageHistory
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails
import com.payu.baas.core.model.responseModels.GetPassBookTransactionsResponse
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.ui.BaseActivity
import java.util.*

class AdvanceUsageTransactionListActivity : BaseActivity(), ClickHandler {

    lateinit var binding: ActivityAdvanceUsageTransactionListBinding
    lateinit var viewModel: AdvanceViewModel
    private var mAdapter: AdvanceWeeklyTransactionDetailsAdapter? = null
    var mobileNumber: String? = null
    var sessionId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_advance_usage_transaction_list)
        activity = this
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AdvanceViewModel.AdvanceViewModelFactory(this, this, this)
        )[AdvanceViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getAdvanceWeeklyTransactionsDetailListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_LIST)
        }
        viewModel.getTransactionStatusDetailsResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_DETAIL)
        }
    }

    //    var startDate = " "
//    var endDate = ""
    private fun setupUI() {
//        val bundle = intent.extras.getBundle()
        val historyString =
            intent.extras!!.getString(BaaSConstantsUI.ARGUMENTS_ADVANCE_USAGE_HISTORY_MODEL)
        var historyUsageHistory = JsonUtil.toObject(
            historyString,
            AdvanceUsageHistory::class.java
        ) as AdvanceUsageHistory
        sessionId = SessionManager.getInstance(this).accessToken
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
            BaaSConstantsUI.CL_ADVANCE_TXN_DETAILS,
            BaaSConstantsUI.CL_ADVANCE_TXN_DETAILS_EVENT_ID,
            sessionId,
            imei,
            mobileNumber,
            Date(), "", "", "", ""
        )
        binding.tvToolbarSubTitle.setText(
            historyUsageHistory!!.displayStartDate +
                    " - " + historyUsageHistory!!.displayEndDate
        )
        binding.rv.setHasFixedSize(true)
        mAdapter =
            AdvanceWeeklyTransactionDetailsAdapter(object :
                AdvanceWeeklyTransactionDetailsAdapter.ItemClickListener {
                override fun onItemClicked(model: GetPassBookTransactionDetails) {
//                    viewModel.transactionID = model.txnLogId
//                    viewModel.getTransactionStatusDetails(false)
                }

            })
        binding.rv.adapter = mAdapter
//        startDate = historyUsageHistory!!.cycleStartDate!!
//        endDate = historyUsageHistory!!.cycleEndDate!!
        viewModel.getAdvanceWeeklyTransactionsDetailList(
            historyUsageHistory!!.cycleStartDate!!,
            historyUsageHistory!!.cycleEndDate!!
        )

        /**
         * Handle pagination
         */
        binding.rv.isNestedScrollingEnabled = false
        binding.nestedscrollview.viewTreeObserver.addOnScrollChangedListener {
            val view =
                binding.nestedscrollview.getChildAt(binding.nestedscrollview.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedscrollview.height + binding.nestedscrollview.scrollY)
            if (diff == 0) {
                if (viewModel.pageNumber != -1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.getAdvanceWeeklyTransactionsDetailList(
                            historyUsageHistory!!.cycleStartDate!!,
                            historyUsageHistory!!.cycleEndDate!!
                        )
                    }, 200)
                }
            }
        }
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_TRANSACTION_LIST -> {
                        if (viewModel.pageNumber != -1)
                            it.data?.let {
//                            mAdapter?.clearAdapter()
                                if (it is GetPassBookTransactionsResponse) {
                                    if (it.transactionList.isNotEmpty()) {

                                        val finalTransactionList =
                                            arrayListOf<GetPassBookTransactionDetails>()

                                        //Here we first add already displayed data from adapter into above list
                                        //to manage comparison
                                        mAdapter?.let { adapter ->
                                            val list = adapter.getList()
                                            if (list.isNotEmpty()) {
                                                finalTransactionList.addAll(list)
                                            }
                                        }
                                        val serverTransactionList = it.transactionList
                                        if (finalTransactionList.isNullOrEmpty()) {
                                            finalTransactionList.addAll(serverTransactionList)
                                        } else {
                                            serverTransactionList.forEach { serverTransactionDetailModel ->
                                                finalTransactionList.add(
                                                    serverTransactionDetailModel
                                                )
                                            }
                                        }
                                        mAdapter?.addList(finalTransactionList)
                                    }

                                    if (it.page?.uppercase().equals("LAST PAGE")) {
                                        viewModel.pageNumber = -1
                                    } else {
                                        viewModel.pageNumber++
                                    }
                                }
                            }
                    }
                    ApiName.GET_TRANSACTION_DETAIL -> {
                        it.data?.let {
                            if (it is TransactionDetailsResponse) {
//                                showTransactionDetails(it)
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
                        var errorUiRes = JsonUtil.toObject(
                            it.message,
                            ErrorResponseUI::class.java
                        ) as ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.clParent,
                        msg,
                        false
                    )
                }
//                if (!(it.message.isNullOrEmpty()))
//                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
//                            BaaSConstantsUI.DEVICE_BINDING_FAILED
//                        )
//                    ) {
//                        viewModel.reGenerateAccessToken(false)
//                    } else {
//                        var msg = "Error"
//                        var errorRes: Any
//                        try {
//                            when (apiName) {
//                                ApiName.GET_TRANSACTION_LIST -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        TransactionDetailsResponse::class.java
//                                    ) as TransactionDetailsResponse
//                                    if (!errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                }
//                                ApiName.GET_TRANSACTION_DETAIL -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        GetPassBookTransactionsResponse::class.java
//                                    ) as GetPassBookTransactionsResponse
//                                    if (!errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                }
//                            }
//                            viewModel.baseCallBack?.showToastMessage(msg)
//                        } catch (e: Exception) {
//                            viewModel.baseCallBack?.showToastMessage(it.message)
//                        }
//                    }
            }
            Status.RETRY -> {
                hideProgress()

            }
        }
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llGoBack -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
            BaaSConstantsUI.CL_GO_BACK_TO_ADVANCE_FROM_TXN_DETAILS,
            BaaSConstantsUI.CL_GO_BACK_TO_ADVANCE_FROM_TXN_DETAILS_EVENT_ID,
            sessionId,
            imei,
            mobileNumber,
            Date(), "", "", "", ""
        )
        finish()
    }

}