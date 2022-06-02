package com.payu.baas.coreUI.view.ui.activity.advance

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.LayoutAdvanceWeeklyTransactionDetailsBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.model.AdvanceUsageHistory
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.ClickHandler


//https://code.luasoftware.com/tutorials/android/android-bottom-sheet/
class AdvanceWeeklyTransactionDetailsBottomDialogFragment : BottomSheetDialogFragment(),
    ClickHandler {

    companion object {

        fun newInstance(): AdvanceWeeklyTransactionDetailsBottomDialogFragment =
            AdvanceWeeklyTransactionDetailsBottomDialogFragment().apply {
                /*
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
                 */
            }
    }

    private var mAdapter: AdvanceWeeklyTransactionDetailsAdapter? = null
    private var binding: LayoutAdvanceWeeklyTransactionDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = binding!!

    private lateinit var viewModel: AdvanceViewModel


    //Transparent Background
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    //Show Full Screen Fragment
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val  dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)

                val layoutParams = it.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                val screenHeight = (UtilsUI.getScreenHeight(requireActivity()) * .99).toInt()
                behaviour.peekHeight = screenHeight
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        // Add back button listener
        dialog.setOnKeyListener { dialogInterface, keyCode, keyEvent ->
            // getAction to make sure this doesn't double fire
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action === KeyEvent.ACTION_UP) {
                dialogInterface.dismiss()
                viewModel.pageNumber = 0
                 mAdapter!!.clearAdapter()
                mAdapter = null
                true
            } else false
            // Don't capture
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_advance_weekly_transaction_details,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AdvanceViewModel::class.java]
        viewModel.pageNumber = 0
        mBinding.viewModel = viewModel
        setupObserver()
        setupUI()

    }

    private fun setupUI() {
        val bundle = this.arguments
        val historyString = bundle!!.getString(BaaSConstantsUI.ARGUMENTS_ADVANCE_USAGE_HISTORY_MODEL)
        var historyUsageHistory = com.payu.baas.coreUI.util.JsonUtil.toObject(
            historyString,
            AdvanceUsageHistory::class.java
        ) as AdvanceUsageHistory
        mBinding.tvToolbarSubTitle.setText(
            historyUsageHistory!!.displayStartDate +
                    " - " + historyUsageHistory!!.displayEndDate
        )
        mBinding.rv.setHasFixedSize(true)
        mAdapter =
            AdvanceWeeklyTransactionDetailsAdapter(object :
                AdvanceWeeklyTransactionDetailsAdapter.ItemClickListener {
                override fun onItemClicked(model: GetPassBookTransactionDetails) {
//                    viewModel.transactionID = model.txnLogId
//                    viewModel.getTransactionStatusDetails(false)
                }

            })
//        mAdapter.addList(historyUsageHistory)
//        mAdapter!!.list.clear()
        mBinding.rv.adapter = mAdapter
        viewModel.getAdvanceWeeklyTransactionsDetailList(historyUsageHistory!!.cycleStartDate!!, historyUsageHistory!!.cycleEndDate!!)

        /**
         * Handle pagination
         */
        mBinding.rv.isNestedScrollingEnabled = false
        mBinding.nestedscrollview.viewTreeObserver.addOnScrollChangedListener {
            val view =
                mBinding.nestedscrollview.getChildAt(mBinding.nestedscrollview.childCount - 1) as View
            val diff: Int =
                view.bottom - (mBinding.nestedscrollview.height + mBinding.nestedscrollview.scrollY)
            if (diff == 0) {
                if (viewModel.pageNumber != -1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.getAdvanceWeeklyTransactionsDetailList(historyUsageHistory!!.cycleStartDate!!, historyUsageHistory!!.cycleEndDate!!)
                    }, 200)
                }
            }


            /* get the maximum height which we have scroll before performing any action */
//                val maxDistance: Int = mBinding.ll.height
//                /* how much we have scrolled */
//                val movement: Int = mBinding.nestedscrollview.scrollY
//                /*finally calculate the alpha factor and set on the view */
//                if (movement >= 0 && movement <= maxDistance) {
//                    mBinding.llFilterOptionsTemp.visibility=View.GONE
//                } else {
//                    mBinding.llFilterOptionsTemp.visibility=View.VISIBLE
//                }
//            }
        }
    }

    private fun setupObserver() {
        viewModel.getAdvanceWeeklyTransactionsDetailListResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_LIST)
        }
        viewModel.getTransactionStatusDetailsResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_TRANSACTION_DETAIL)
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
                                showTransactionDetails(it)
                            }
                        }
                    }
                }
            }
            Status.LOADING -> {
                showProgress()
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()
                if (!(it.message.isNullOrEmpty()))
                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                        )
                    ) {
                        viewModel.reGenerateAccessToken(true)
                    } else {
                        var msg = "Error"
                        var errorRes: Any
                        try {
                            errorRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                                it.message,
                                com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                            ) as com.payu.baas.coreUI.model.ErrorResponseUI
                            if (!errorRes.userMessage.isNullOrEmpty())
                                msg = errorRes.userMessage!!
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
                            viewModel.baseCallBack?.showToastMessage(msg)
                        } catch (e: Exception) {
                            viewModel.baseCallBack?.showToastMessage(it.message)
                        }
                    }
            }
            Status.RETRY -> {
                hideProgress()

            }
        }
    }

    private fun hideProgress() {
        activity?.let {
            if (it is AdvanceActivity) {
                (it as AdvanceActivity).hideProgress()
            }
        }
    }

    private fun showProgress() {
        activity?.let {
            if (it is AdvanceActivity) {
                (it as AdvanceActivity).showProgress("")
            }
        }
    }

    override fun click(view: View) {
        if (view.id == R.id.llGoBack) {
            dismiss()
        }
    }

    /**
     * Show Transaction Details
     */
    private fun showTransactionDetails(model: TransactionDetailsResponse) {

        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_TRANSACTION_DETAIL_MODEL,
            com.payu.baas.coreUI.util.JsonUtil.toString(model)
        )
        val intent = Intent(activity, AdvanceTransactionDetailActivity::class.java)
        intent.putExtras(bundle)
        activity?.startActivity(intent)
    }

//    override fun onDismiss(ddialog: DialogInterface) {
//        dialog!!.dismiss()
//        dialog = null
////        viewModel.pageNumber = 0
////        mAdapter!!.clearAdapter()
////        mAdapter = null
//        super.onDismiss(ddialog)
//        //Code here
////        dialog!!.dismiss()
////        dialog = null
////        mAdapter!!.clearAdapter()
//    }
}