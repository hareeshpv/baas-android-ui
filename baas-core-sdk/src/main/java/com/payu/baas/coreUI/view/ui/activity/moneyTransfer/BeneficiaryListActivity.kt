package com.payu.baas.coreUI.view.ui.activity.moneyTransfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityBeneficiaryListBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.model.BeneficiaryModel
import com.payu.baas.core.model.model.IFSCDetailsModel
import com.payu.baas.core.model.responseModels.DeleteBeneficiaryResponse
import com.payu.baas.core.model.responseModels.GetBeneficiaryResponse
import com.payu.baas.core.model.responseModels.UndoDeletedBeneficiaryResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.ClickHandler
import com.payu.baas.coreUI.view.callback.SnackBarListener
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import org.json.JSONArray
import java.util.*


class BeneficiaryListActivity : BaseActivity(),
    ClickHandler {

    private lateinit var mBinding: ActivityBeneficiaryListBinding
    private lateinit var mViewModel: MoneyTransferViewModel
    private var mAdapter: BeneficiaryListAdapter? = null
    private var mAllowToUndoDeletedBeneficiary: Boolean = false
    private var HasDeleteTaskPerformed = false
    private var mobileNumber: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_beneficiary_list)
        activity = this

        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_ALLOW_UNDO_DELETED_BENEFICIARY)) {
                mAllowToUndoDeletedBeneficiary =
                    it.getBoolean(BaaSConstantsUI.ARGUMENTS_ALLOW_UNDO_DELETED_BENEFICIARY)
            }
        }



        applyThemeToUI()
        setupViewModel()
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
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MoneyTransferViewModel.MoneyTransferViewModelFactory(this, this, this)
        )[MoneyTransferViewModel::class.java]
        mBinding.viewModel = mViewModel
        mViewModel.pageNumber = 0
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        mBinding.tvEdit.visibility = View.INVISIBLE

        //Note : I don't have API and don't know response of API
        //so you have to pass your required response model class after getting API response successfully
        //and do needful changes into adapter class
        val layoutManager = LinearLayoutManager(this)
        mBinding.rv.layoutManager = layoutManager
        mBinding.rv.setHasFixedSize(true)
        mAdapter =
            BeneficiaryListAdapter(object : BeneficiaryListAdapter.ItemClickListener {
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
            }, true)
        mBinding.rv.adapter = mAdapter
        mBinding.rv.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                mViewModel.loadingBeneficiaryList = true

                Handler(Looper.getMainLooper()).postDelayed({
                    mViewModel.getAllBeneficiaryList()
                }, 500)
            }

            override val isLoading: Boolean
                get() = mViewModel.loadingBeneficiaryList
            override val isLastPage: Boolean
                get() = mViewModel.pageNumber == -1

        })
    }

    private fun setupObserver() {
        mViewModel.getAllBeneficiaryListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_BENEFICIARY)
        }

        mViewModel.getDeleteBeneficiariesResponseObs().observe(this) {
            parseResponse(it, ApiName.DELETE_BENEFICIARY)
        }

        mViewModel.getUndoDeletedBeneficiariesResponseObs().observe(this) {
            parseResponse(it, ApiName.UNDO_DELETED_BENEFICIARY)
        }

        mViewModel.getAllBeneficiaryList()
    }

    override fun click(view: View) {
        when (view.id) {
            R.id.tvEdit -> {
                if (SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity == 1) {
                    cleverTapUserProfileEvent(
                        BaaSConstantsUI.CL_USER_PROFILE_EDIT_PAYEE_LIST,
                        BaaSConstantsUI.CL_USER_EDIT_PAYEES_EVENT_ID,
                        SessionManager.getInstance(applicationContext).accessToken,
                        imei,
                        mobileNumber,
                        Date(),
                        ""
                    )
                } else {
                    cleverTapUserMoneyTransferEvent(
                        BaaSConstantsUI.CL_EDIT_PAYEE_LIST_MONEY_TRANSFER,
                        BaaSConstantsUI.CL_EDIT_PAYEE_LIST_MONEY_TRANSFER_EVENT_ID,
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

                if (mAdapter?.itemCount!! > 0) {
                    mAdapter?.enableMultipleSelection()
                    mBinding.tvEdit.visibility = View.INVISIBLE
                    mBinding.llDelete.visibility = View.VISIBLE
                } else {
                    com.payu.baas.coreUI.app.BaasUIApp.ctx?.let { context ->
                        UtilsUI.showSnackbarOnSwitchAction(
                            mBinding.root,
                            context.getString(R.string.msg_no_data_found_for_payee_list),
                            false
                        )
                    }
                }
            }
            R.id.llGoBack -> {
                onBackPressed()
            }
            R.id.btnDelete -> {
                if (SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity == 1) {
                    cleverTapUserProfileEvent(
                        BaaSConstantsUI.CL_USER_PROFILE_delete_PAYEE_LIST,
                        BaaSConstantsUI.CL_USER_DELETE_PAYEES_EVENT_ID,
                        SessionManager.getInstance(applicationContext).accessToken,
                        imei,
                        mobileNumber,
                        Date(),
                        ""
                    )
                } else {
                    cleverTapUserMoneyTransferEvent(
                        BaaSConstantsUI.CL_DELETE_PAYEES_MONEY_TRANSFER,
                        BaaSConstantsUI.CL_DELETE_PAYEES_MONEY_TRANSFER_EVENT_ID,
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
                if (mAdapter?.allowSelection == true) {
                    mViewModel.selectedBeneficiariesForDelete.clear()

                    //Here we get all beneficiaries list
                    val list = mAdapter?.getAllItems()
                    //Here we save selected beneficiaries
                    val selectedList = arrayListOf<BeneficiaryModel>()
                    //Here we extract selected beneficiaries
                    list?.forEach {
                        if (it.selected) {
                            selectedList.add(it)
                        }
                    }
                    if (selectedList.isNotEmpty()) {
                        //Here we save selected list into ViewModel for UNDO process
                        mViewModel.selectedBeneficiariesForDelete = selectedList
                        //Here we extract selected beneficiaries id's and save into list
                        val selectedBeneficiariesIds = arrayListOf<String>()
                        selectedList.forEach { beneficiaryModel ->
                            beneficiaryModel.beneficiaryId?.let { id ->
                                selectedBeneficiariesIds.add(
                                    id
                                )
                            }
                        }
                        mViewModel.deleteBeneficiaries(JSONArray(selectedBeneficiariesIds))
                    } else {
                        com.payu.baas.coreUI.app.BaasUIApp.ctx?.let { context ->
                            UtilsUI.showSnackbarOnSwitchAction(
                                mBinding.root,
                                context.getString(R.string.msg_no_beneficiaries_selected_to_delete),
                                false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_BENEFICIARY -> {
                        mViewModel.loadingBeneficiaryList = false
                        it.data?.let { output ->
                            if (output is GetBeneficiaryResponse) {
                                if (output.userBeneficiaryList.isNullOrEmpty()) {
                                    mViewModel.pageNumber = -1
                                } else {
                                    output.userBeneficiaryList?.let { list ->
                                        mBinding.tvEdit.visibility = View.VISIBLE
                                        //From API response we got data into wrong key. For size we get value into Page and for Page we get value into Size
                                        if (list.size < output.page!!) {
                                            mViewModel.pageNumber = -1
                                        } else {
                                            mViewModel.pageNumber++
                                        }
                                        //mViewModel.pageNumber = -1
                                        mAdapter?.addList(list)

                                        /**
                                         * Here we store list data into ViewModel for the purpose of manage
                                         * undo beneficiary data
                                         */
                                        if (mAllowToUndoDeletedBeneficiary) {
                                            mViewModel.beneficiaryList.addAll(list)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ApiName.DELETE_BENEFICIARY -> {
                        it.data?.let {
                            if (it is DeleteBeneficiaryResponse) {
                                HasDeleteTaskPerformed = true

                                if (!mAllowToUndoDeletedBeneficiary) {
                                    SimpleCustomSnackbar.showSwitchMsg.make(
                                        mBinding.root,
                                        getString(R.string.msg_selected_payees_deleted_sucessfully),
                                        Snackbar.LENGTH_LONG, true
                                    )?.show()
                                } else {

                                    val timer = Timer()

                                    SimpleCustomSnackbar.undoItems.make(
                                        mBinding.root,
                                        getString(R.string.msg_selected_payees_deleted_sucessfully),
                                        BaaSConstantsUI.SNACKBARD_DURATION,
                                        object : SnackBarListener {
                                            override fun undo() {
                                                timer.cancel()

                                                //Here we get previously deleted beneficiaries
                                                val deletedList =
                                                    mViewModel.selectedBeneficiariesForDelete
                                                if (deletedList.isNotEmpty()) {
                                                    //Here we extract selected beneficiaries id's and save into list
                                                    val selectedBeneficiariesIds =
                                                        arrayListOf<String>()
                                                    deletedList.forEach { beneficiaryModel ->
                                                        beneficiaryModel.beneficiaryId?.let { id ->
                                                            selectedBeneficiariesIds.add(
                                                                id
                                                            )
                                                        }
                                                    }
                                                    //Here we pass beneficiaries ids for undo process
                                                    mViewModel.undoDeletedBeneficiaries(
                                                        JSONArray(
                                                            selectedBeneficiariesIds
                                                        )
                                                    )
                                                } else {
                                                    mViewModel.loadingBeneficiaryList = false
                                                }
                                            }
                                        }
                                    )?.show()


                                    timer.schedule(object : TimerTask() {
                                        override fun run() {
                                            //Snack bar dismiss automatically means auto cancel
                                            //and so we conclude that user has not go with UNDO option
                                            //Hence we have to remove deleted items from ViewModel list
                                            val allBeneficiaryList = mViewModel.beneficiaryList
                                            if (allBeneficiaryList.isNotEmpty()) {
                                                val deletedBeneficiaryList =
                                                    mViewModel.selectedBeneficiariesForDelete
                                                var i = 0
                                                while (i < allBeneficiaryList.size) {
                                                    var j = 0
                                                    while (j < deletedBeneficiaryList.size) {
                                                        if (allBeneficiaryList[i].beneficiaryId == deletedBeneficiaryList[j].beneficiaryId) {
                                                            allBeneficiaryList.removeAt(i)
                                                            i--
                                                            break
                                                        }
                                                        j++
                                                    }
                                                    i++
                                                }
                                            }

                                            /**
                                             * Here we reactivate pagination as SnackBar is dismissed and
                                             * user has not selected UNDO option
                                             */
                                            mViewModel.loadingBeneficiaryList = false
                                        }
                                    }, BaaSConstantsUI.SNACKBARD_TIMER_DURATION)
                                }

                                /**
                                 * Due to pagination concept if we remove any data from adapter then
                                 * API gets called automatically to fetch data for next page.
                                 * But because we have to manage UNDO task we stop pagination call by
                                 * making loadingBeneficiaryList=true
                                 */
                                if (mAllowToUndoDeletedBeneficiary) {
                                    mViewModel.loadingBeneficiaryList = true
                                }
                                //Here have to remove deleted beneficiaries from adapter/list
                                mAdapter?.deleteItems()

                                //Don't change position
                                mBinding.llGoBack.performClick()
                            }
                        }
                    }
                    ApiName.UNDO_DELETED_BENEFICIARY -> {
                        it.data?.let {
                            if (it is UndoDeletedBeneficiaryResponse) {
                                mBinding.tvEdit.visibility = View.VISIBLE
                                /**
                                 * Here we reactivate pagination as refilled previously deleted items
                                 */
                                mAdapter?.clearAdapter()

                                /**
                                 * Here we unselect those items which are selected previously
                                 */
                                mViewModel.beneficiaryList.forEach {
                                    if (it.selected)
                                        it.selected = false
                                }

                                mAdapter?.addList(mViewModel.beneficiaryList)
                                mViewModel.loadingBeneficiaryList = false
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
                hideProgress()
                mViewModel.loadingBeneficiaryList = false
                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                        )
                    ) {
                        mViewModel.reGenerateAccessToken(false)
                    } else {
                        UtilsUI.showSnackbarOnSwitchAction(
                            mBinding.root,
                            it.message,
                            false
                        )
                    }
            }
            Status.RETRY -> {
                hideProgress()
                mViewModel.loadingBeneficiaryList = false
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
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity == 1) {
            if (mAdapter?.allowSelection == true) {
                cleverTapUserProfileEvent(
                    BaaSConstantsUI.CL_USER_DELETE_PAYEES_GO_BACK_MANAGE_PAYEE,
                    BaaSConstantsUI.CL_USER_DELETE_PAYEES_GO_BACK_MANAGE_PAYEE_EVENT_ID,
                    SessionManager.getInstance(applicationContext).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    ""
                )
            }else{
                cleverTapUserProfileEvent(
                    BaaSConstantsUI.CL_USER_MANAGE_PAYEES_GO_BACK_PROFILE,
                    BaaSConstantsUI.CL_USER_MANAGE_PAYEES_GO_BACK_PROFILE_EVENT_ID,
                    SessionManager.getInstance(applicationContext).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    ""
                )
            }
        } else {
            if (mAdapter?.allowSelection == true) {
                cleverTapUserMoneyTransferEvent(
                    BaaSConstantsUI.CL_GO_BACK_EDIT_PAYEES_PAGE_MONEY_TRANSFER,
                    BaaSConstantsUI.CL_GO_BACK_EDIT_PAYEES_PAGE_MONEY_TRANSFER_EVENT_ID,
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
            } else {
                cleverTapUserMoneyTransferEvent(
                    BaaSConstantsUI.CL_GO_BACK_PAYEE_LIST_MONEY_TRANSFER,
                    BaaSConstantsUI.CL_GO_BACK_PAYEE_LIST_MONEY_TRANSFER_EVENT_ID,
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
//                if (HasDeleteTaskPerformed)
//                    setResult(Activity.RESULT_OK)
//                finish()
            }
            SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity = 0
            if (mAdapter?.allowSelection == true) {
//            cleverTapUserMoneyTransferEvent(
//                BaaSConstantsUI.CL_GO_BACK_EDIT_PAYEES_PAGE_MONEY_TRANSFER,
//                BaaSConstantsUI.CL_GO_BACK_EDIT_PAYEES_PAGE_MONEY_TRANSFER_EVENT_ID,
//                SessionManager.getInstance(this).accessToken,
//                imei,
//                SessionManagerUI.getInstance(applicationContext).userMobileNumber,
//                Date(),
//                "",
//                "",
//                "",
//                ""
//            )


                mAdapter?.disableMultipleSelection()
                if (mAdapter?.itemCount!! > 0)
                    mBinding.tvEdit.visibility = View.VISIBLE
                else
                    mBinding.tvEdit.visibility = View.INVISIBLE
                mBinding.llDelete.visibility = View.GONE
            } else {

//            cleverTapUserMoneyTransferEvent(
//                BaaSConstantsUI.CL_GO_BACK_PAYEE_LIST_MONEY_TRANSFER,
//                BaaSConstantsUI.CL_GO_BACK_PAYEE_LIST_MONEY_TRANSFER_EVENT_ID,
//                SessionManager.getInstance(this).accessToken,
//                imei,
//                SessionManagerUI.getInstance(applicationContext).userMobileNumber,
//                Date(),
//                "",
//                "",
//                "",
//                ""
//            )

//                /**
//                 * Here we recall recent payee list( in previous activity) to get updated payee list after deleting some payees
//                 */
//                if (HasDeleteTaskPerformed)
//                    setResult(Activity.RESULT_OK)
//                finish()
            }

        }
        /**
         * Here we recall recent payee list( in previous activity) to get updated payee list after deleting some payees
         */
        if (HasDeleteTaskPerformed)
            setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        mViewModel.pageNumber = 0
        mViewModel.loadingBeneficiaryList = false
        mViewModel.beneficiaryList.clear()
        mViewModel.selectedBeneficiariesForDelete.clear()
        mAdapter?.clearAdapter()
        super.onDestroy()
    }
}