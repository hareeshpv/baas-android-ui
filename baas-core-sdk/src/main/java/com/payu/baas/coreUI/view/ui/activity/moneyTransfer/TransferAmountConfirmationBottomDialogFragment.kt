package com.payu.baas.coreUI.view.ui.activity.moneyTransfer

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.LayoutTransferAmountConfirmationPopupBinding
import com.payu.baas.core.model.responseModels.PrevalidateTransactionResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.UtilsUI


//https://code.luasoftware.com/tutorials/android/android-bottom-sheet/
class TransferAmountConfirmationBottomDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(model: PrevalidateTransactionResponse): TransferAmountConfirmationBottomDialogFragment =
            TransferAmountConfirmationBottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("MODEL", Gson().toJson(model))
                }
            }
    }

    private var binding: LayoutTransferAmountConfirmationPopupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = binding!!

    //Transparent Background
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    //Show Full Screen Fragment
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
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
            if (keyCode == KeyEvent.KEYCODE_BACK
                && keyEvent.action === KeyEvent.ACTION_UP
            ) {
                dialogInterface.dismiss()
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
            R.layout.layout_transfer_amount_confirmation_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {

        UtilsUI.applyColorToText(
            mBinding.tvTotalChargesLabel,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTotalCharges,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        arguments?.let { bundle ->
            if (bundle.containsKey("MODEL")) {

                val str = bundle.getString("MODEL")
                str?.let {
                    val model = Gson().fromJson(
                        it,
                        PrevalidateTransactionResponse::class.java
                    )

                    mBinding.tvBankTransferCharges.text =
                        getString(R.string.currency_symbol).plus(UtilsUI.convertAmountFormat(model.chargedAmount))
                    mBinding.tvAdvanceUseFees.text =
                        getString(R.string.currency_symbol).plus(UtilsUI.convertAmountFormat(model.advanceChargeAmount))
                    mBinding.tvTotalCharges.text =
                        getString(R.string.currency_symbol).plus(UtilsUI.convertAmountFormat(model.chargedAmount + model.advanceChargeAmount))
                }
            }
        }
        mBinding.tvMoveAhead.setOnClickListener {
            dismiss()

            activity?.let {act->
                if (act is TransferAmountActivity) {
                    (act as TransferAmountActivity).acceptOrCancelTransferCharges(true)
                    (act as TransferAmountActivity).goToPassCodeVerificationActivity()
                }
            }
        }
        mBinding.tvCancel.setOnClickListener {
            dismiss()

            activity?.let {act->
                if (act is TransferAmountActivity) {
                    (act as TransferAmountActivity).acceptOrCancelTransferCharges(false)
                }
            }

        }
    }
}


