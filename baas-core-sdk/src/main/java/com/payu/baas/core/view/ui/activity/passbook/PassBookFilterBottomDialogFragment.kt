package com.payu.baas.core.view.ui.activity.passbook

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.baas.core.R
import com.payu.baas.core.databinding.LayoutPassbookFiltersBinding
import com.payu.baas.core.model.datasource.PassBookFilterDataSource
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.UtilsUI
import java.util.*

class PassBookFilterBottomDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(): PassBookFilterBottomDialogFragment =
            PassBookFilterBottomDialogFragment().apply {
                /*
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
                 */
            }
    }

    private var binding: LayoutPassbookFiltersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = binding!!

    private lateinit var viewModel: PassBookViewModel

    //Transparent Background
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    //Handle BackPress Event
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireActivity(), theme) {
            override fun onBackPressed() {
                //do your stuff
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_passbook_filters,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PassBookViewModel::class.java]


        applyThemeToUI()
        setupUI()
    }

    private fun applyThemeToUI() {

        UtilsUI.applyColorToIcon(
            mBinding.ivCancel,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTitle,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTransactions,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvAccountType,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )
    }

    private fun setupUI() {
        //Set previously selected type
        val transactionTypeList = PassBookFilterDataSource.transactionTypes
        transactionTypeList.forEach { typeModel ->
            if (viewModel.selectedTransactionType?.code == typeModel.code) {
                typeModel.selected = true
                return@forEach
            }
        }
        mBinding.rvTransactionType.layoutManager = GridLayoutManager(requireActivity(), 2)
        val transactionTypeAdapter =
            PassBookFilterTypeSelectionAdapter(transactionTypeList)
        mBinding.rvTransactionType.adapter = transactionTypeAdapter


        //Set previously selected type
        val accountTypeList = PassBookFilterDataSource.accountTypes
        accountTypeList.forEach { typeModel ->
            if (viewModel.selectedAccountType?.code == typeModel.code) {
                typeModel.selected = true
                return@forEach
            }
        }
        mBinding.rvAccountType.layoutManager = GridLayoutManager(requireActivity(), 2)
        val accountTypeAdapter =
            PassBookFilterTypeSelectionAdapter(accountTypeList)
        mBinding.rvAccountType.adapter = accountTypeAdapter


        mBinding.llCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnApplyFilter.setOnClickListener {
            //Here we save currently selected type
            transactionTypeAdapter.getList().let {
                it.forEach { typeModel ->
                    if (typeModel.selected) {
                        viewModel.selectedTransactionType = typeModel
                        return@forEach
                    }
                }
            }
            //Here we save currently selected type
            accountTypeAdapter.getList().let {
                it.forEach { typeModel ->
                    if (typeModel.selected) {
                        viewModel.selectedAccountType = typeModel
                        return@forEach
                    }
                }
            }

            activity?.let {act->
                if(act is PassBookActivity) {
                    viewModel.baseCallBack!!.cleverTapUserPassbookEvent(
                        BaaSConstantsUI.CL_PASSBOOK_CUSTOM_FILTER_TXNS,
                        BaaSConstantsUI.CL_PASSBOOK_CUSTOM_FILTER_TXNS_EVENT_ID,
                        SessionManager.getInstance(requireActivity()).accessToken,
                        (act as PassBookActivity).imei,
                        (act as PassBookActivity).mobileNumber,
                        Date(),
                        "",
                        viewModel.selectedTransactionType?.label?:"",
                        viewModel.selectedAccountType?.label?:"",
                        "",
                        "",
                        ""
                    )
                }
            }

            viewModel.applyFilter()
            dismiss()
        }
    }

}