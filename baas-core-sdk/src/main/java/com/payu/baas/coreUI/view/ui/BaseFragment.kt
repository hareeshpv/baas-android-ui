package com.payu.baas.coreUI.view.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.view.callback.AlertDialogMultipleCallback
import com.payu.baas.coreUI.view.callback.AlertDialogSingleCallback
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.activity.onboarding.ErrorActivity
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BaseFragment : Fragment(), BaseCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BaseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BaseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun showDialog(message: String) {
        getBaseActivity().showDialog(message)
    }

    override fun callNextScreen(intent: Intent, bundle: Bundle?, finish: Boolean) {
        getBaseActivity().callNextScreen(intent, bundle)
    }

    override fun showTechinicalError() {

        //Start your targeted activity from here
        val intent = Intent(getBaseActivity(), ErrorActivity::class.java)
        val mBundle = Bundle()
        mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.TECHNICAL.ordinal)
        intent.putExtras(mBundle)
        getBaseActivity().startActivity(intent)
    }

    override fun showProgress(strMessage: String) {
        getBaseActivity().showProgress(strMessage)
    }

    override fun hideProgress() {
        getBaseActivity().hideProgress()
    }

    override fun showToastMessage(strMessage: String?) {
        getBaseActivity().showToastMessage(strMessage)
    }

    override fun isInternetAvailable(redirectToNoInternetActivity: Boolean): Boolean {
        return getBaseActivity().isInternetAvailable(redirectToNoInternetActivity)
    }

    override fun showAlertWithSingleButton(
        strTitle: String?,
        strMessage: String,
        strPositiveActionButton: String
    ) {
        getBaseActivity().showAlertWithSingleButton(strTitle, strMessage, strPositiveActionButton)
    }


    override fun showAlertWithSingleButtonCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        alertDialogSingleCallback: AlertDialogSingleCallback
    ) {
        getBaseActivity().showAlertWithSingleButtonCallBack(
            strTitle,
            strMessage,
            strPositiveActionButton,
            alertDialogSingleCallback
        )
    }

    override fun showAlertWithMultipleCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        strNegativeActionButton: String,
        alertDialogMultipleCallback: AlertDialogMultipleCallback
    ) {
        getBaseActivity().showAlertWithMultipleCallBack(
            strTitle,
            strMessage,
            strPositiveActionButton,
            strNegativeActionButton,
            alertDialogMultipleCallback
        )
    }

    override fun showLogoutAlert() {
        getBaseActivity().showLogoutAlert()
    }

    override fun showKeyboard(editText: EditText) {
        getBaseActivity().showKeyboard(editText)
    }

    override fun showKeyboard(activity: Activity, editText: EditText) {
        getBaseActivity().showKeyboard(activity, editText)
    }

    override fun showNumericKeyboardWithDecimal(activity: Activity, editText: EditText) {
        getBaseActivity().showNumericKeyboardWithDecimal(activity, editText)
    }

    override fun showKeyboard(activity: Activity, editText: EditText, inputTpye: Int) {
        getBaseActivity().showKeyboard(activity, editText, inputTpye)
    }


    override fun hideKeyboard(editText: EditText) {
        getBaseActivity().hideKeyboard(editText)
    }

    override fun hideKeyboard(activity: Activity) {
        getBaseActivity().hideKeyboard(activity)
    }


    override fun cleverTapUserProfile(
        name: String,
        identity: String?,
        email: String,
        phone: String?,
        gender: String,
        employed: String,
        education: String,
        dob: String,
        photo: String,
        address: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserOnBoardingEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserOnBoardingEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        dob: String?,
        empId: String?,
        dAddress: String?,
        timeStamp: Date
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserHomeEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        item: String,
        status: String
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserCardManagementEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        status: String,
        reason: String
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserPassbookEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        type: String,
        transactionFilter: String,
        accountTypeFilter: String,
        transactionID: String,
        transactionStatus: String,
        item: String
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserMoneyTransferEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        amountPaid: String,
        transactionCharges: String,
        transactionStatus: String,
        receiverBankIFSCode: String,
        item: String
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserProfileEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        item: String
    ) {
        TODO("Not yet implemented")
    }

    override fun cleverTapUserAdvanceEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        amount: String,
        item: String,
        accountNumber: String,
        advanceStatus: String
    ) {
        TODO("Not yet implemented")
    }


    open fun getBaseActivity(): BaseActivity {
        val activity = requireActivity()
        try {
            if (!activity.isDestroyed
                && activity is BaseActivity
            ) {
                return activity
            }
        } catch (e: Exception) {
        }
        return activity as BaseActivity
    }
}