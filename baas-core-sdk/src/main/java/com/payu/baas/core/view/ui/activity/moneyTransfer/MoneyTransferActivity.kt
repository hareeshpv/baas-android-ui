package com.payu.baas.core.view.ui.activity.moneyTransfer

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityMoneyTransferBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.model.BeneficiaryModel
import com.payu.baas.core.model.model.IFSCDetailsModel
import com.payu.baas.core.model.responseModels.CreateBeneficiaryResponse
import com.payu.baas.core.model.responseModels.GetRecentBeneficiaryResponse
import com.payu.baas.core.model.responseModels.VerifyIFSCResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.ui.BaseActivity
import java.util.*
import kotlin.collections.ArrayList


class MoneyTransferActivity : BaseActivity(), ClickHandler {

    private var mAdapter: BeneficiaryListAdapter? = null
    lateinit var mBinding: ActivityMoneyTransferBinding

    lateinit var mViewModel: MoneyTransferViewModel
    private var mFoundRecentBeneficiaryList: Boolean = false
    private var mHasNewBeneficiaryAddedSuccessfully: Boolean = false
    private var mobileNumber: String? = null


    private var amountTransferSuccessLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
            }
        }

    private var payeeListOpenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                refetchRecentPayeeList()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_money_transfer)
        activity = this
        cleverTapUserMoneyTransferEvent(
            BaaSConstantsUI.CL_MONEY_TRANSFER_PAGE_LOAD,
            BaaSConstantsUI.CL_MONEY_TRANSFER_PAGE_LOAD_EVENT_ID,
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


        applyThemeToUI()
        setupViewModel()
        setupUI()
        setupObserver()
        captureKeyboardShowHideEvent()
        applyFocusOnWidgets()
    }

    override fun onResume() {
        super.onResume()
        if (mHasNewBeneficiaryAddedSuccessfully) {
            refetchRecentPayeeList()
            mHasNewBeneficiaryAddedSuccessfully = false
        }
    }

    /**
     * Here we capture keyboard show / hide event
     *  as we have show / hide Footer Payee List based on keyboard event
     */
    private fun captureKeyboardShowHideEvent() {
        val parentView: View = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                )
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    return
                }
                alreadyOpen = isShown
                showHideFooter(!isShown)
            }
        })
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
            mBinding.tvAccountNumber,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToIcon(
            mBinding.ivEnterAccountNumberVerified,
            Color.parseColor("#21C188")
        )

        UtilsUI.applyColorToText(
            mBinding.tvReEnterAccountNumber,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToIcon(
            mBinding.ivReEnterAccountNumberVerified,
            Color.parseColor("#21C188")
        )

        UtilsUI.applyColorToText(
            mBinding.tvIFSCCode,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToIcon(
            mBinding.ivIFSCCodeVerified,
            Color.parseColor("#21C188")
        )

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mBinding.pbVerifyIFSCCode.indeterminateDrawable.colorFilter =
                BlendModeColorFilter(
                    SessionManagerUI.getInstance(this).primaryDarkColor,
                    BlendMode.SRC_IN
                )
        } else {
            mBinding.pbVerifyIFSCCode.indeterminateDrawable.setColorFilter(
                SessionManagerUI.getInstance(this).primaryDarkColor,
                PorterDuff.Mode.SRC_IN
            )
        }*/

        UtilsUI.applyColorToText(
            mBinding.tvBankDetails,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvAccountHolderName,
            SessionManagerUI.getInstance(this).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvRecentPayees,
            SessionManagerUI.getInstance(this).accentColor
        )
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        mBinding.rv.setHasFixedSize(true)
        mAdapter = BeneficiaryListAdapter(object : BeneficiaryListAdapter.ItemClickListener {
            override fun onItemClicked(beneficiaryModel: BeneficiaryModel) {

                cleverTapUserMoneyTransferEvent(
                    BaaSConstantsUI.CL_SELECT_PAYEE,
                    BaaSConstantsUI.CL_SELECT_PAYEE_EVENT_ID,
                    SessionManager.getInstance(applicationContext).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )

                val ifscDetails = IFSCDetailsModel()
                ifscDetails.bank = beneficiaryModel.bankName
                ifscDetails.icon = beneficiaryModel.icon

                openAmountTransferActivity(beneficiaryModel, ifscDetails)
            }
        })
        mBinding.rv.adapter = mAdapter

        showHideFooter(false)
    }

    private fun applyFocusOnWidgets() {
        mBinding.etReEnterAccountNumber.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    if (mViewModel.strAccountNumber.get().toString().trim().isNotEmpty()
                        && (mViewModel.strAccountNumber.get().toString().trim().length < 9
                                || mViewModel.strAccountNumber.get().toString().trim().length > 18)
                    ) {
                        mViewModel.errorMessageForEnterAccountNumber.set(getString(R.string.msg_enter_valid_account_number))
                    }
                }
            }

        mBinding.etIFSCCode.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    mViewModel.isAccountNumberVerified()
                } else {
                    mViewModel.errorMessageForIFSCCode.set("")
                    if (mViewModel.strIFSCCode.get().toString().isNotEmpty()
                        && mViewModel.strIFSCCode.get().toString().trim().length != 11
                    ) {
                        mViewModel.errorMessageForIFSCCode.set(getString(R.string.msg_enter_valid_ifsc_code))
                    } else if (mViewModel.strIFSCCode.get().toString().trim().length == 11
                        && mViewModel.ifscCodeVerifiedSuccessfully.get() == false
                    ) {
                        mViewModel.verifyIFSCCode()
                    }
                }
            }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MoneyTransferViewModel.MoneyTransferViewModelFactory(this, this, this)
        )[MoneyTransferViewModel::class.java]
        mBinding.viewModel = mViewModel
    }

    private fun setupObserver() {
        mViewModel.getCreateUserBeneficiaryResponseObs().observe(this) {
            parseResponse(it, ApiName.ADD_BENEFICIARY)
        }

        mViewModel.getRecentBeneficiaryListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_RECENT_BENEFICIARY)
        }

        mViewModel.getVerifyIFSCCodeResponseObs().observe(this) {
            parseResponse(it, ApiName.IFSC_CODE)
        }

        mViewModel.getRecentBeneficiaryList()
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.llGoBack -> {
                onBackPressed()
            }
            R.id.tvSeeAllPayees -> {
                openPayeeList()
            }
            R.id.btnGoAhead -> {
                hideKeyboard(this)
                mViewModel.createUserBeneficiary()
            }
        }
    }


    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()

                when (apiName) {
                    ApiName.IFSC_CODE -> {
                        it.data?.let {
                            if (it is VerifyIFSCResponse) {
                                if (it.statusCode == "101") {
                                    mViewModel.ifscCodeVerifiedSuccessfully.set(true)
                                    it.IFSCDetails?.let { details ->
                                        //use the first-4-chars of the IFSC code (eg: SBIN) to retrieve the image
                                        details.ifsc?.let { ifscCode ->
                                            details.icon = (ifscCode.substring(0, 4)) + ".png"
                                        }
                                        mViewModel.verifiedIFSCDetails = details
                                        mBinding.ifscDetails = details
                                    }
                                } else {
                                    mViewModel.errorMessageForIFSCCode.set(getString(R.string.msg_invalid_ifsc_code))
                                }
                            }
                        }
                    }
                    ApiName.ADD_BENEFICIARY -> {
                        it.data?.let {
                            if (it is CreateBeneficiaryResponse) {
                                mHasNewBeneficiaryAddedSuccessfully = true
                                it.userBeneficiary?.let { beneficiaryModel ->

                                    mViewModel.verifiedIFSCDetails?.let { ifscDetails ->

                                        openAmountTransferActivity(beneficiaryModel, ifscDetails)
                                    }
                                }

                                cleverTapUserMoneyTransferEvent(
                                    BaaSConstantsUI.CL_ADD_RECEIVER_ACCOUNT_SUCCESSFULLY,
                                    BaaSConstantsUI.CL_ADD_RECEIVER_ACCOUNT_SUCCESSFULLY_EVENT_ID,
                                    SessionManager.getInstance(this).accessToken,
                                    imei,
                                    mobileNumber,
                                    Date(),
                                    "",
                                    "",
                                    "",
                                    mViewModel.strIFSCCode.get().toString().trim(),
                                    ""
                                )

                                //Here we clear entered data on UI
                                mViewModel.strAccountNumber.set("")
                                mViewModel.strReEnteredAccountNumber.set("")
                                mViewModel.strIFSCCode.set("")
                                mViewModel.strAccountHolderName.set("")
                                mViewModel.ifscCodeVerifiedSuccessfully.set(false)
                                mViewModel.verifiedIFSCDetails = null
                                mBinding.etAccountHolderName.clearFocus()
                            }
                        }
                    }
                    ApiName.GET_RECENT_BENEFICIARY -> {
                        it.data?.let {
                            if (it is GetRecentBeneficiaryResponse) {
                                if (it.userBeneficiaryList.isNullOrEmpty()) {
                                    mFoundRecentBeneficiaryList = false
                                    showHideFooter(false)

                                } else {
                                    it.userBeneficiaryList?.let { list ->
                                        if (list.isNotEmpty()) {
                                            mFoundRecentBeneficiaryList = true
                                            mAdapter?.addList(list as ArrayList<BeneficiaryModel>)
                                            showHideFooter(true)
                                        } else {
                                            mFoundRecentBeneficiaryList = false
                                            showHideFooter(false)
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

                        /*cleverTapUserMoneyTransferEvent(
                            BaaSConstantsUI.CL_MONEY_TRANSFER_PAGE_ERROR,
                            BaaSConstantsUI.CL_MONEY_TRANSFER_PAGE_ERROR_EVENT_ID,
                            SessionManager.getInstance(this).accessToken,
                            imei,
                            SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                            Date(),
                            "",
                            "",
                            "",
                            ""
                        )*/

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
            }
        }
    }

    private fun openAmountTransferActivity(
        beneficiaryModel: BeneficiaryModel?,
        ifscDetails: IFSCDetailsModel?
    ) {
        val bundle = Bundle()
        beneficiaryModel?.let {
            bundle.putString(
                BaaSConstantsUI.ARGUMENTS_BENEFICIARY_MODEL,
                Gson().toJson(it)
            )
        }
        ifscDetails?.let {
            bundle.putString(
                BaaSConstantsUI.ARGUMENTS_IFSC_CODE_DETAIL_MODEL,
                Gson().toJson(it)
            )
        }
        val intent =
            Intent(this, TransferAmountActivity::class.java)
        intent.putExtras(bundle)
        amountTransferSuccessLauncher.launch(intent)
    }


    private fun openPayeeList() {

        cleverTapUserMoneyTransferEvent(
            BaaSConstantsUI.CL_PAYEE_LIST_MONEY_TRANSFER,
            BaaSConstantsUI.CL_PAYEE_LIST_MONEY_TRANSFER_EVENT_ID,
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

        val bundle = Bundle()
        bundle.putBoolean(
            BaaSConstantsUI.ARGUMENTS_ALLOW_UNDO_DELETED_BENEFICIARY,
            true
        )
        val intent = Intent(this, BeneficiaryListActivity::class.java)
        intent.putExtras(bundle)
        SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity = 0
        payeeListOpenLauncher.launch(intent)
    }

    private fun refetchRecentPayeeList() {
        mFoundRecentBeneficiaryList = false
        mAdapter?.clearAdapter()
        showHideFooter(false)

        mViewModel.getRecentBeneficiaryList()
    }

    private fun showHideFooter(isShown: Boolean) {
        if (mFoundRecentBeneficiaryList) {
            if (isShown) {
                mBinding.clFooter.visibility = View.VISIBLE
            } else {
                mBinding.clFooter.visibility = View.GONE
            }
        } else {
            mBinding.clFooter.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        mAdapter?.clearAdapter()
        super.onDestroy()
    }

    override fun onBackPressed() {
        cleverTapUserMoneyTransferEvent(
            BaaSConstantsUI.CL_GO_BACK_HOME_MONEY_TRANSFER,
            BaaSConstantsUI.CL_GO_BACK_HOME_MONEY_TRANSFER_EVENT_ID,
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
