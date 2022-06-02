package com.payu.baas.coreUI.view.ui.activity.advance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityAdvanceBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.model.AdvanceUsageHistory
import com.payu.baas.core.model.responseModels.GetAccountDetailsResponse
import com.payu.baas.core.model.responseModels.GetSalaryAdvanceInfoResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.ClickHandler
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.moneyTransfer.MoneyTransferActivity
import com.payu.baas.coreUI.view.ui.common.AddMoneyBottomSheetDialogFragment
import java.util.*
import kotlin.collections.ArrayList


class AdvanceActivity : BaseActivity(), ClickHandler {

    private var mAdapter: AdvanceTransactionsAdapter? = null
    lateinit var binding: ActivityAdvanceBinding

    lateinit var viewModel: AdvanceViewModel
    var mobileNumber: String? = null
    var sessionId: String? = null
    var overdueBalance: String? = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_advance)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI() // Don't change the position
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llGoBack,
            R.id.llGoBack1 -> {
                onBackPressed()
            }
            R.id.llUnlockAccount -> {
                showProfileUnlockBottomSheetDialog()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
            BaaSConstantsUI.CL_GO_BACK_TO_HOME_FROM_ADVANCE,
            BaaSConstantsUI.CL_GO_BACK_TO_HOME_FROM_ADVANCE_EVENT_ID,
            sessionId,
            imei,
            mobileNumber,
            Date(), overdueBalance!!, "", "", ""
        )
        finish()
    }

    /**
     * Here we show User Profile UnBlock dialog when user click on
     * 'Add 360 to Unlock' button
     */
    private fun showProfileUnlockBottomSheetDialog() {
        if(overdueBalance!!.isNullOrEmpty())
            overdueBalance="0"

        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
            BaaSConstantsUI.CL_ADVANCE_ADD_AMOUNT_TO_UNLOCK,
            BaaSConstantsUI.CL_ADVANCE_ADD_AMOUNT_TO_UNLOCK_EVENT_ID,
            sessionId,
            imei,
            mobileNumber,
            Date(), overdueBalance!!, "", "", ""
        )
        var bundle = Bundle()
        var shareText = "Share Bank Details To " + binding.btnShareOnWhatsapp.text
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_TITLE,
            shareText
//            getString(R.string.label_share_bank_details_to_unlock_profile)
        )
        if (accountDetails != null) {
            bundle.putString(BaaSConstantsUI.ARGUMENTS_ACCOUNT_DETAILS, Gson().toJson(accountDetails))
        }
        val fragment = AddMoneyBottomSheetDialogFragment.newInstance(bundle)
        fragment.show(supportFragmentManager, "Unlock_Profile")
        fragment.isCancelable = true
    }

    /**
     * Here we show weekly transaction details when user click on
     * 'See Transaction Detail'
     */
//    private fun showWeeklyTransactionDetailsBottomSheetDialog(advanceUsageHistory: AdvanceUsageHistory) {
//        val fragment = AdvanceWeeklyTransactionDetailsBottomDialogFragment.newInstance()
//        val bundle = Bundle()
//        var historyString = JsonUtil.toString(advanceUsageHistory)
//        bundle.putString(BaaSConstantsUI.ARGUMENTS_ADVANCE_USAGE_HISTORY_MODEL, historyString)
//        fragment.arguments = bundle
//        fragment.show(supportFragmentManager, "Advance_Weekly_Transaction_Detail")
//        fragment.isCancelable = false
//    }
    private fun showUsageTxnList(advanceUsageHistory: AdvanceUsageHistory) {
//        val fragment = AdvanceWeeklyTransactionDetailsBottomDialogFragment.newInstance()
        val bundle = Bundle()
        var historyString = com.payu.baas.coreUI.util.JsonUtil.toString(advanceUsageHistory)
        bundle.putString(BaaSConstantsUI.ARGUMENTS_ADVANCE_USAGE_HISTORY_MODEL, historyString)
        callNextScreen(
            Intent(this, AdvanceUsageTransactionListActivity::class.java),
            bundle, false
        )
//        fragment.arguments = bundle
//        fragment.show(supportFragmentManager, "Advance_Weekly_Transaction_Detail")
//        fragment.isCancelable = false
    }

    private fun setupUI() {
        sessionId =  SessionManager.getInstance(this).accessToken
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        binding.appbarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == -binding.collapsingToolbar.height + binding.toolbar.height) {
                //toolbar is collapsed here
                //write your code here
                binding.tvPastUsagelabel.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE

                // set text view layout margin programmatically
                (binding.llTransactions.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = 0.dpToPixels(this@AdvanceActivity)
                }

            } else {
                binding.tvPastUsagelabel.visibility = View.VISIBLE
                binding.toolbar.visibility = View.GONE

                // set text view layout margin programmatically
                (binding.llTransactions.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = 20.dpToPixels(this@AdvanceActivity)
                }
            }
        })

        binding.rvTransactions.setHasFixedSize(true)
        mAdapter =
            AdvanceTransactionsAdapter(object : AdvanceTransactionsAdapter.ItemClickListener {
                override fun onItemClicked(model: AdvanceUsageHistory) {
                    showUsageTxnList(model)
//                    showWeeklyTransactionDetailsBottomSheetDialog(model)
                }
            })
        binding.rvTransactions.adapter = mAdapter
        binding.llTransactions.visibility = View.GONE
        binding.clNoTransactionsFound.visibility = View.GONE
        binding.clTechnicalIssue.visibility = View.GONE
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AdvanceViewModel.AdvanceViewModelFactory(this, this, this)
        )[AdvanceViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getAdvanceTransactionListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_SALARY_ADVANCE_INFO)
        }
        viewModel.getAccountDetailsObs().observe(this, {
            parseResponse(it, ApiName.GET_ACCOUNT_DETAILS)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_ACCOUNT_DETAILS -> {
                        it.data?.let {
                            if (it is GetAccountDetailsResponse) {
                                invalidateAccountDetailsUI(it)
                            }
                        }
                    }
                    ApiName.GET_SALARY_ADVANCE_INFO -> {
                        it.data?.let {
                            if (it is GetSalaryAdvanceInfoResponse) {
                                viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
                                    BaaSConstantsUI.CL_ADVANCE_PAGE_LOAD,
                                    BaaSConstantsUI.CL_ADVANCE_PAGE_LOAD_EVENT_ID,
                                    sessionId,
                                    imei,
                                    mobileNumber,
                                    Date(),"","","",""
                                )
                                binding.accountLocked = it.blocked
                                /*if (it.active == true) {
                                     binding.tvSwitchStatus.text = getString(R.string.label_active)
                                     binding.switchStatusTemp.isChecked = true
                                     binding.switchStatus.isChecked = true
                                 } else {
                                    binding.tvSwitchStatus.text =
                                        getString(R.string.label_inactive)
                                    binding.switchStatusTemp.isChecked = false
                                    binding.switchStatus.isChecked = false
                                }*/
                                binding.tvToolbarSubTitle.setText(
                                    "Is Haftey (" + it.currentUsage!!.displayStartDate +
                                            " - " + it.currentUsage!!.displayEndDate + ")"
                                )
                                binding.tvNextDueDate.setText(it.currentUsage!!.dueDate)
                                binding.tvTotalAmountLimit.text = it.advanceAvailable.toString()
                                binding.tvAdvanceUsed.text =
                                    getString(R.string.currency_symbol).plus(it.currentUsage?.advanceUsed)
                                binding.tvFeesCharged.text =
                                    getString(R.string.currency_symbol).plus(it.currentUsage?.feesCharged)
//
                                if (it.blocked == true)
                                    overdueBalance = it.currentUsage!!.overdueBalance
                                if (!it.displayMsgForLockedAccount.isNullOrEmpty()) {
                                    binding.accountUsed = true
//                                    binding.tvLockedAccountMessage.visibility = View.VISIBLE
                                    binding.tvLockedAccountMessage.setText(it.displayMsgForLockedAccount)

                                } else {
                                    binding.accountUsed = false
//                                    binding.tvLockedAccountMessage.visibility = View.GONE
                                }
                                if (!it.buttonTextForLockedAccount.isNullOrEmpty()) {
                                    viewModel.getAccountDetails()
                                    binding.btnShareOnWhatsapp.visibility = View.VISIBLE
                                    binding.btnShareOnWhatsapp.setText(it.buttonTextForLockedAccount)
                                } else {
                                    binding.btnShareOnWhatsapp.visibility = View.GONE
                                }
//                                }
                                if (!it.usageHistory.isNullOrEmpty()) {
                                    startScroll()
                                    binding.llTransactions.visibility = View.VISIBLE
                                    mAdapter?.addList(it.usageHistory as ArrayList<AdvanceUsageHistory>?)
                                    /*
                                    if (it.blocked == true) {
                                    var overDueBal = it.currentUsage!!.overdueBalance
                                    if(overDueBal.isNullOrEmpty() || overDueBal.toInt()==0)
                                        overDueBal = it.usageHistory!!.get(0).overdueBalance
                                    binding.btnShareOnWhatsapp.setText(
                                        "Add " + overDueBal + " to Unlock"
                                    )
                                    }
                                     */
                                } else {
                                    stopScroll()
                                    binding.clNoTransactionsFound.visibility = View.VISIBLE
                                }
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
                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                        || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED)
                    ) {
                        viewModel.reGenerateAccessToken(false)
//                        when (apiName) {
//                            ApiName.GET_SALARY_ADVANCE_INFO -> {
//                                lastCallFrom = LastCall.GET_SALARY_ADVANCE_INFO
//                            }
//                        }
                    } else if (apiName == ApiName.GET_SALARY_ADVANCE_INFO && errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                        && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                    ) {
                        binding.clTechnicalIssue.visibility = View.VISIBLE
                        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
                            BaaSConstantsUI.CL_ADVANCE_PAGE_ERROR,
                            BaaSConstantsUI.CL_ADVANCE_PAGE_ERROR_EVENT_ID,
                            sessionId,
                            imei,
                            mobileNumber,
                            Date(),"","","",""
                        )
                    } else {
                        viewModel.baseCallBack!!.cleverTapUserAdvanceEvent(
                            BaaSConstantsUI.CL_ADVANCE_PAGE_ERROR,
                            BaaSConstantsUI.CL_ADVANCE_PAGE_ERROR_EVENT_ID,
                            sessionId,
                            imei,
                            mobileNumber,
                            Date(),"","","",""
                        )
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
                            binding.llParent,
                            msg,
                            false
                        )
                    }
            }
            Status.RETRY -> {
                hideProgress()
                if (apiName == ApiName.GET_SALARY_ADVANCE_INFO)
                    binding.clTechnicalIssue.visibility = View.VISIBLE
                when (apiName) {
                    ApiName.GET_SALARY_ADVANCE_INFO -> {
                        viewModel.getAdvanceTransactions()
                    }
                }
            }
        }
    }

    private var accountDetails: GetAccountDetailsResponse? = null
    private fun invalidateAccountDetailsUI(accountDetails: GetAccountDetailsResponse?) {
        this.accountDetails = accountDetails
    }

    private fun stopScroll() {
        val toolbarLayoutParams =
            binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags = 0
        binding.collapsingToolbar.layoutParams = toolbarLayoutParams
        val appBarLayoutParams = binding.appbarLayout.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutParams.behavior = null
        binding.appbarLayout.layoutParams = appBarLayoutParams
    }

    private fun startScroll() {
        val toolbarLayoutParams =
            binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        binding.collapsingToolbar.layoutParams = toolbarLayoutParams
        val appBarLayoutParams = binding.appbarLayout.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutParams.behavior = AppBarLayout.Behavior()
        binding.appbarLayout.layoutParams = appBarLayoutParams
    }

    // extension function to convert dp to equivalent pixels
    fun Int.dpToPixels(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    fun AbhiPaiseBhejo(view: View) {
        callNextScreen(Intent(this, MoneyTransferActivity::class.java), null, true)
    }

    override fun onDestroy() {
        mAdapter?.clearAdapter()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAdvanceTransactions()
//        viewModel.getAccountDetails()
    }
}
