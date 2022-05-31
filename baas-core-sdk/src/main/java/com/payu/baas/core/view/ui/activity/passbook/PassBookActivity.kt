@file:JvmSynthetic

package com.payu.baas.core.view.ui.activity.passbook

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityPassbookBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.datasource.PassBookFilterDataSource
import com.payu.baas.core.model.entities.model.GetPassBookTransactionDetailHeader
import com.payu.baas.core.model.responseModels.GetAccountBalanceDetailsResponse
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails
import com.payu.baas.core.model.responseModels.GetPassBookTransactionsResponse
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.callback.SnackBarListener
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.moneyTransfer.MoneyTransferActivity
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.text.SimpleDateFormat
import java.util.*

internal class PassBookActivity : BaseActivity(), ClickHandler {

    private var mAdapter: PassBookTransactionsAdapter? = null
    lateinit var mBinding: ActivityPassbookBinding
    var mobileNumber: String? = null

    lateinit var viewModel: PassBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_passbook)
        activity = this

        setupViewModel()
        applyThemeToUI()
        setupUI()
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

        UtilsUI.gradientRound(
            mBinding.llHelp
        )
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack!!.cleverTapUserPassbookEvent(
            BaaSConstantsUI.CL_PASSBOOK_PAGE_LOAD,
            BaaSConstantsUI.CL_PASSBOOK_PAGE_LOAD_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            "",
            "",
            "",
            "",
            ""
        )

        val layoutManager = LinearLayoutManager(this)
        mBinding.rvTransactions.layoutManager = layoutManager
        mBinding.rvTransactions.setHasFixedSize(true)
        mAdapter =
            PassBookTransactionsAdapter(object : PassBookTransactionsAdapter.ItemClickListener {
                override fun onItemClicked(model: GetPassBookTransactionDetails) {
                    viewModel.transactionID = model.txnLogId
                    viewModel.getTransactionStatusDetails(false)

                }
            })
        mBinding.rvTransactions.adapter = mAdapter
        hideViews()


        /**
         * Handle pagination
         */
        mBinding.rvTransactions.isNestedScrollingEnabled = false


        /*mBinding.nestedscrollview.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->

            val nestedScrollView = checkNotNull(v){
                return@setOnScrollChangeListener
            }

            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

            if (lastChild != null) {

                if ((scrollY >= (lastChild.measuredHeight - nestedScrollView.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {

                    //get more items
                }
            }
        }*/
        mBinding.nestedscrollview.viewTreeObserver.addOnScrollChangedListener {
            if (mBinding.llTransactions.visibility == View.VISIBLE) {
                val view =
                    mBinding.nestedscrollview.getChildAt(mBinding.nestedscrollview.childCount - 1) as View
                val diff: Int =
                    view.bottom - (mBinding.nestedscrollview.height + mBinding.nestedscrollview.scrollY)
                if (diff == 0) {
                    if (viewModel.pageNumber != -1) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.getPassBookTransactions()
                        }, 500)
                    }
                }


                /* get the maximum height which we have scroll before performing any action */
                val maxDistance: Int = mBinding.ll.height
                /* how much we have scrolled */
                val movement: Int = mBinding.nestedscrollview.scrollY
                /*finally calculate the alpha factor and set on the view */
                if (movement >= 0 && movement <= maxDistance) {
                    mBinding.llFilterOptionsTemp.visibility = View.GONE
                } else {
                    mBinding.llFilterOptionsTemp.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            PassBookViewModel.PassBookViewModelFactory(this, this, this)
        )[PassBookViewModel::class.java]

        mBinding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getAccountBalanceDetailsResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_ACCOUNT_BALANCE_DETAILS)
        }
        viewModel.getPassBookTransactionListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_LIST)
        }
        viewModel.getTransactionStatusDetailsResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_DETAIL)
        }
        /**
         * We have to clear adapter when ever filter is applied.
         * So to manage filter apply event we manage this live data
         */
        viewModel.clearTransactionListO.observe(this) {
            if (it) {
                hideViews()
                var containsTransactions = false
                mAdapter?.let { adapter ->
                    if (adapter.itemCount > 0) {
                        containsTransactions = true
                        adapter.clearAdapter()
                    }
                }
                viewModel.pageNumber = 0
                //Here we need to call API to fetch transactions only when there are data into list previously
                //other wise we don't have to call API because we have already place API call code when scrolling happen in nestedscrollview.
                //Here we have placed code for clear adapter and when adapter gets clear, scrolling happen in nestedscrollview
                //and API gets called indirectly
                if (containsTransactions.not())
                    viewModel.getPassBookTransactions()
            }
        }
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {

        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_ACCOUNT_BALANCE_DETAILS -> {
                        it.data?.let {
                            if (it is GetAccountBalanceDetailsResponse) {

                                showAccountBalance(it)
                                viewModel.getPassBookTransactions()
                            }
                        }
                    }
                    ApiName.GET_TRANSACTION_LIST -> {
                        it.data?.let {
                            if (it is GetPassBookTransactionsResponse) {
                                if (it.transactionList.isNotEmpty()) {
                                    if (mBinding.llTransactions.visibility == View.GONE)
                                        mBinding.llTransactions.visibility = View.VISIBLE
                                    if (mBinding.clTechnicalIssueAfterAppliedFilter.visibility == View.VISIBLE)
                                        mBinding.clTechnicalIssueAfterAppliedFilter.visibility =
                                            View.GONE
                                    if (mBinding.clNoTransactionsFoundAfterAppliedFilter.visibility == View.VISIBLE)
                                        mBinding.clNoTransactionsFoundAfterAppliedFilter.visibility =
                                            View.GONE

                                    val finalTransactionList =
                                        arrayListOf<GetPassBookTransactionDetailHeader>()

                                    //Here we first add already displayed data from adapter into above list
                                    //to manage comparison
                                    mAdapter?.let { adapter ->
                                        val list = adapter.getList()
                                        if (list.isNotEmpty()) {
                                            finalTransactionList.addAll(list)
                                        }
                                    }

                                    val serverTransactionList = it.transactionList
                                    serverTransactionList.forEach { serverTransactionDetailModel ->
                                        serverTransactionDetailModel.date?.let { date_ ->

                                            val transactionDate =
                                                SimpleDateFormat(
                                                    BaaSConstantsUI.ddMMMyyyyhhmma,
                                                    Locale.getDefault()
                                                ).parse(date_)
                                            transactionDate?.let {
                                                val transactionMonthYear =
                                                    SimpleDateFormat(
                                                        BaaSConstantsUI.MMMyyyy,
                                                        Locale.getDefault()
                                                    ).format(transactionDate)

                                                if (finalTransactionList.isNotEmpty()) {

                                                    var foundMatch = false
                                                    finalTransactionList.forEach { header ->

                                                        //Here we first match transaction month year(Jan 21) fetched from server with header month year()
                                                        //If found matched then we extract amount value and based on transaction status we add
                                                        //amount into header Total Income or Expense

                                                        if (header.date == transactionMonthYear) {
                                                            foundMatch = true
                                                            header.transactionDetails.add(
                                                                header.transactionDetails.size,
                                                                serverTransactionDetailModel
                                                            )

                                                            return@forEach
                                                        }
                                                    }

                                                    if (!foundMatch) {

                                                        val header =
                                                            GetPassBookTransactionDetailHeader()
                                                        header.date = transactionMonthYear

                                                        // We have to add amount into Total Income/Expense only
                                                        // if transaction status is success
                                                        header.transactionDetails.add(
                                                            serverTransactionDetailModel
                                                        )
                                                        finalTransactionList.add(
                                                            finalTransactionList.size,
                                                            header
                                                        )
                                                    }
                                                } else {
                                                    val header =
                                                        GetPassBookTransactionDetailHeader()
                                                    header.date = transactionMonthYear

                                                    // We have to add amount into Total Income/Expense only
                                                    // if transaction status is success
                                                    header.transactionDetails.add(
                                                        serverTransactionDetailModel
                                                    )
                                                    finalTransactionList.add(header)
                                                }
                                            }
                                        }
                                    }

                                    mAdapter?.addList(finalTransactionList)
                                } else {
                                    if (viewModel.pageNumber == 0
                                    ) {
                                        if (IsDefaultFilterValueApplied()) {
                                            mBinding.clNoTransactionsFound.visibility =
                                                View.VISIBLE
                                        } else {
                                            mBinding.llTransactions.visibility = View.VISIBLE
                                            mBinding.clTechnicalIssueAfterAppliedFilter.visibility =
                                                View.GONE
                                            mBinding.clNoTransactionsFoundAfterAppliedFilter.visibility =
                                                View.VISIBLE
                                        }
                                    }
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

                                showTransactionDetails(it)
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
                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                        )
                    ) {
                        viewModel.reGenerateAccessToken(false)
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
                if (apiName == ApiName.GET_TRANSACTION_LIST) {
                    if (viewModel.pageNumber == 0
                    ) {
                        if (IsDefaultFilterValueApplied()) {
                            mBinding.clTechnicalIssue.visibility = View.VISIBLE
                            return
                        } else {
                            mBinding.llTransactions.visibility = View.VISIBLE
                            mBinding.clTechnicalIssueAfterAppliedFilter.visibility =
                                View.VISIBLE
                            mBinding.clNoTransactionsFoundAfterAppliedFilter.visibility =
                                View.GONE
                        }
                    }
                }


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

                msg?.let {message->
                    SimpleCustomSnackbar.retry.make(
                        mBinding.root,
                        message,
                        BaaSConstantsUI.SNACKBARD_DURATION,
                        object : SnackBarListener {
                            override fun undo() {
                                when (apiName) {
                                    ApiName.GET_ACCOUNT_BALANCE_DETAILS -> {
                                        viewModel.getAccountBalanceDetails()
                                    }
                                    ApiName.GET_TRANSACTION_LIST -> {
                                        viewModel.getPassBookTransactions()
                                    }
                                    else -> {

                                    }
                                }
                            }
                        }
                    )?.show()
                }
            }
        }
    }


    private fun IsDefaultFilterValueApplied(): Boolean {
        if (viewModel.selectedTimePeriodType?.code == PassBookFilterDataSource.ALL_TIME
            && viewModel.selectedAccountType?.code == PassBookFilterDataSource.ALL
            && viewModel.selectedTransactionType?.code == PassBookFilterDataSource.ALL
        ) {
            return true
        }
        return false
    }

    private fun showAccountBalance(accountBalanceDetails: GetAccountBalanceDetailsResponse) {
        accountBalanceDetails.totalAvailableBalance?.let {
            mBinding.tvTotalAmountKharchLimit.text = it
        }
        accountBalanceDetails.prepaidBalance?.let {
            mBinding.tvAmountForAccountBalance.text = it
        }
        accountBalanceDetails.advanceBalance?.let {
            mBinding.tvAmountForAdvanceAllowed.text = it
        }
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llGoBack,
            R.id.llGoBack1 -> {
                finish()
            }
            R.id.btnAbhiPaisaBhejo -> {
                viewModel.baseCallBack!!.cleverTapUserPassbookEvent(
                    BaaSConstantsUI.CL_PASSBOOK_ACCESS_MONEY_TRANSFER,
                    BaaSConstantsUI.CL_PASSBOOK_ACCESS_MONEY_TRANSFER_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )

                callNextScreen(Intent(this, MoneyTransferActivity::class.java), null)
                finish()
            }
            R.id.btnReloadPage -> {
                viewModel.getAccountBalanceDetails()
            }
            R.id.llHelpTooltip -> {
                /**
                 * Show Tooltip message when user click on Help (?) icon
                 */

                UtilsUI.showPopupWindow(
                    this@PassBookActivity,
                    mBinding.llHelpTooltip,
                    getString(R.string.tooltip_msg_for_passbook)
                )

                /*UtilsUI.showPopupWindow(
                    mBinding.llHelpTooltip
                )*/

                viewModel.baseCallBack!!.cleverTapUserPassbookEvent(
                    BaaSConstantsUI.CL_PASSBOOK_VIEW_ADVANCE_INFO,
                    BaaSConstantsUI.CL_PASSBOOK_VIEW_ADVANCE_INFO_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            }
            R.id.llSelectedTimePeriod,
            R.id.llSelectedTimePeriodTemp -> {
                /**
                 * Show Time Period Selection View
                 */
                val fragment = PassBookTimePeriodFilterBottomDialogFragment.newInstance()
                fragment.show(supportFragmentManager, "TimePeriod")
                fragment.isCancelable = false
            }
            R.id.llFilters,
            R.id.llFiltersTemp -> {
                /**
                 * Show Filter View
                 */
                val fragment = PassBookFilterBottomDialogFragment.newInstance()
                fragment.show(supportFragmentManager, "Filter")
                fragment.isCancelable = false
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
        bundle.putString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE, "PassBook")
        val intent = Intent(this, TransactionDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    /**
     * Here we hide required views
     */
    private fun hideViews() {
        if (IsDefaultFilterValueApplied()) {
        } else {
            //Have to show filter option as user has applied filter
            mBinding.llTransactions.visibility = View.VISIBLE
        }
        mBinding.clNoTransactionsFoundAfterAppliedFilter.visibility = View.GONE
        mBinding.clTechnicalIssueAfterAppliedFilter.visibility = View.GONE
        mBinding.clNoTransactionsFound.visibility = View.GONE
        mBinding.clTechnicalIssue.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cleverTapUserPassbookEvent(
            BaaSConstantsUI.CL_PASSBOOK_GO_BACT_HOME_PASSBOOK,
            BaaSConstantsUI.CL_PASSBOOK_GO_BACT_HOME_PASSBOOK_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            "",
            "",
            "",
            "",
            ""
        )

    }

    override fun onDestroy() {
        mAdapter?.clearAdapter()
        super.onDestroy()
    }


}
