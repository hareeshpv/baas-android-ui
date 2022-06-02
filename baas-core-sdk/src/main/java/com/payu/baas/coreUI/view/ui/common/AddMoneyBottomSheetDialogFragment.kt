package com.payu.baas.coreUI.view.ui.common

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.BottomSheetDialogAddMoneyBinding
import com.payu.baas.core.model.responseModels.GetAccountDetailsResponse
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.BaaSConstantsUI.ARGUMENTS_IMEI
import com.payu.baas.coreUI.util.BaaSConstantsUI.ARGUMENTS_MOBILE_NUMBER
import com.payu.baas.coreUI.view.ui.activity.advance.AdvanceActivity
import com.payu.baas.coreUI.view.ui.activity.dashboard.DashboardActivity
import com.payu.baas.coreUI.view.ui.activity.profile.UserProfileActivity
import java.util.*

class AddMoneyBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(bundle: Bundle): AddMoneyBottomSheetDialogFragment =
            AddMoneyBottomSheetDialogFragment().apply {
                arguments = bundle
            }
    }

    private var binding: BottomSheetDialogAddMoneyBinding? = null
    var cleverTapDefaultInstance: CleverTapAPI? = null
    var imei: String? = null
    var mobileNumber: String? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = binding!!

    //Transparent Background
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
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
            R.layout.bottom_sheet_dialog_add_money,
            container,
            false
        )
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var accountName = ""
        var accountNumber = ""
        var ifsc = ""
        arguments?.let { bundle ->
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_TITLE)) {
                mBinding.tvTitle.text = bundle.getString(BaaSConstantsUI.ARGUMENTS_TITLE)
            }
            imei = bundle.getString(ARGUMENTS_IMEI)
            mobileNumber = bundle.getString(ARGUMENTS_MOBILE_NUMBER)
            if (bundle.containsKey(BaaSConstantsUI.ARGUMENTS_ACCOUNT_DETAILS)) {
                val strAccountDetails = bundle.getString(BaaSConstantsUI.ARGUMENTS_ACCOUNT_DETAILS)
                var accountDetails: GetAccountDetailsResponse? =
                    Gson().fromJson(strAccountDetails, GetAccountDetailsResponse::class.java)

                if (accountDetails != null) {
                    accountDetails.accountName?.let {
                        accountName = it
                    }
                    accountDetails.accountNumber?.let {
                        accountNumber = it
                    }
                    accountDetails.ifscCode?.let {
                        ifsc = it
                    }

                    mBinding.tvAccountHolderName.text = accountName
                    mBinding.tvVirtualAccountNumber.text = accountNumber
                    mBinding.tvIFSCCode.text = ifsc
                }
            }
        }
        mBinding.llCancel.setOnClickListener {
            dismiss()
        }
        mBinding.llShareOnWhatsapp.setOnClickListener {
            var eventName = ""
            var eventId = ""

            if (activity is AdvanceActivity) {
                eventName = BaaSConstantsUI.CL_ADVANCE_SHARE_BANK_DETAILS
                eventId = BaaSConstantsUI.CL_ADVANCE_SHARE_BANK_DETAILS_EVENT_ID
            }else if (activity is UserProfileActivity){
                eventName = BaaSConstantsUI.CL_USER_SHARE_ACCOUNT_DETAIL_PROFILE
                eventId = BaaSConstantsUI.CL_USER_SHARE_ACCOUNT_DETAIL_PROFILE_EVENT_ID
            } else if (activity is DashboardActivity){
                eventName = BaaSConstantsUI.CL_USER_SHARE_ACCOUNT_DETAILS_HOME
                eventId = BaaSConstantsUI.CL_USER_SHARE_ACCOUNT_DETAILS_HOME_EVENT_ID
            }
            cleverTapUserProfileEvent(
                eventName,
                eventId,
                SessionManager.getInstance(
                    requireActivity().applicationContext
                ).accessToken,
                imei,
                mobileNumber,
                Date(),
                ""
            )
            if (accountName.isNotEmpty() && accountNumber.isNotEmpty() && ifsc.isNotEmpty()) {

                var shareText =
                    "MY ACCOUNT DETAILS:\n\nAccount Holder Name:\n$accountName\nVirtual Account Number:\n$accountNumber\nIFSC Code:\n$ifsc"

                val whatsappIntent = Intent(Intent.ACTION_SEND)
                whatsappIntent.type = "text/plain"
                whatsappIntent.setPackage("com.whatsapp")
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                try {
                    requireActivity().startActivity(whatsappIntent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        requireActivity(),
                        "Whatsapp have not been installed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun cleverTapUserProfileEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        item: String,
    ) {
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_ITEM to item
        )
        cleverTapDefaultInstance?.pushEvent(BaaSConstantsUI.CL_USER_EVENT_PROFILE, prodViewedAction)
    }


}