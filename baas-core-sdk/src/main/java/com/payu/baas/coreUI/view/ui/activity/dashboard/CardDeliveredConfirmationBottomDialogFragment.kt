package com.payu.baas.coreUI.view.ui.activity.dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.LayoutCardDeliveredConfirmationPopupBinding
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.UtilsUI


//https://code.luasoftware.com/tutorials/android/android-bottom-sheet/
class CardDeliveredConfirmationBottomDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(): CardDeliveredConfirmationBottomDialogFragment =
            CardDeliveredConfirmationBottomDialogFragment().apply {

            }
    }

    private var binding: LayoutCardDeliveredConfirmationPopupBinding? = null

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
            R.layout.layout_card_delivered_confirmation_popup,
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
            mBinding.tvMessage,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        mBinding.tvMoveAhead.setOnClickListener {
            dismiss()

            activity?.let {
                if (it is DashboardActivity) {
                    it.cardDeliveredConfirmationDoneByUserClicked()
                }
            }
        }
        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }
    }
}


