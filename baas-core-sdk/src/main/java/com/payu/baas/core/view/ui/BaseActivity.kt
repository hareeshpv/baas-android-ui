package com.payu.baas.core.view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.android.sdk.CleverTapAPI
import com.payu.baas.core.R
import com.payu.baas.core.app.BaasUIApp
import com.payu.baas.core.databinding.DialogConfirmationBinding
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.AlertDialogMultipleCallback
import com.payu.baas.core.view.callback.AlertDialogSingleCallback
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import com.payu.baas.core.view.ui.activity.splash.SplashScreenActivity
import zendesk.core.Identity
import zendesk.core.JwtIdentity
import zendesk.core.Zendesk
import zendesk.support.Support
import zendesk.support.guide.HelpCenterActivity
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.concurrent.schedule

open class BaseActivity : AppCompatActivity(), BaseCallback {
    private var mDialog: Dialog? = null
    var activity: Activity? = null
    var cleverTapDefaultInstance: CleverTapAPI? = null
    val PAN_NUMBER_LENGTH = 10
    val POSITION_P_PAN_NUMBER = 3 // STRING STARTS COUNTING FROM 0
    var telephonyManager: TelephonyManager? = null
    var imei: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_base)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            imei = getIMEI()
        } catch (e: Exception) {
        }
    }

    override fun callNextScreen(intent: Intent, bundle: Bundle?, finish: Boolean) {
        if (bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if (finish)
            finish()
    }

    override fun showProgress(strMessage: String) {
        try {
            if (mDialog == null) {
                activity?.let {
                    mDialog = Dialog(activity!!)
                    mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog!!.setContentView(R.layout.dialog_progressbar)
                    mDialog!!.setCanceledOnTouchOutside(false)
                    mDialog!!.setCancelable(false)
                    Objects.requireNonNull(mDialog!!.getWindow())
                        ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    mDialog!!.show()
                }

            }
        } catch (e: Exception) {
        }
    }

    override fun hideProgress() {
        try {
            if (isProgressShowing()) {
                mDialog!!.dismiss()
                mDialog = null
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun showToastMessage(strMessage: String?) {
        strMessage?.let {
            Toast.makeText(
                activity,
                strMessage,
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun showErrorScreen(isAvailable: Boolean, redirectToNoInternetActivity: Boolean): Boolean {
        if (!isAvailable
            && redirectToNoInternetActivity
        ) {
            activity?.let { act ->
                //Start your targeted activity from here
                val intent = Intent(act, ErrorActivity::class.java)
                val mBundle = Bundle()
                mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
                intent.putExtras(mBundle)
                act.startActivity(intent)

            }
            return false
        }
        return true
    }

    override fun isInternetAvailable(redirectToNoInternetActivity: Boolean): Boolean {

//        try {
//            // register activity with the connectivity manager service
//            val connectivityManager =
//                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//            // if the android version is equal to M
//            // or greater we need to use the
//            // NetworkCapabilities to check what type of
//            // network has the internet connection
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                // Returns a Network object corresponding to
//                // the currently active default data network.
//                val network = connectivityManager.activeNetwork
//                // Representation of the capabilities of an active network.
//                val activeNetwork =
//                    connectivityManager.getNetworkCapabilities(network)
//
//                return when {
//                    // Indicates this network uses a Wi-Fi transport,
//                    // or WiFi has network connectivity
//                    activeNetwork!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//
//                    // Indicates this network uses a Cellular transport. or
//                    // Cellular has network connectivity
//                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                    // else return false
//                    else -> showErrorScreen(false, redirectToNoInternetActivity)
//                }
//            } else {
//                // if the android version is below M
//                @Suppress("DEPRECATION") val networkInfo =
//                    connectivityManager.activeNetworkInfo
//                @Suppress("DEPRECATION")
//                return networkInfo!!.isConnected
//            }
//        } catch (e: Exception) {
//        }

        try {
            val cm =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            val isAvailable = netInfo != null && netInfo.isConnected
            if (!isAvailable
                && redirectToNoInternetActivity
            ) {
                activity?.let { act ->
                    //Start your targeted activity from here
                    val intent = Intent(act, ErrorActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
                    intent.putExtras(mBundle)
                    act.startActivity(intent)
                }
            }
            return isAvailable
        } catch (e: Exception) {
        }
        return showErrorScreen(false, redirectToNoInternetActivity)
    }

    override fun showTechinicalError() {
        activity?.let { act ->
            //Start your targeted activity from here
            val intent = Intent(act, ErrorActivity::class.java)
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.TECHNICAL.ordinal)
            intent.putExtras(mBundle)
            act.startActivity(intent)
        }
    }

    override fun showAlertWithSingleButton(
        strTitle: String?,
        strMessage: String,
        strPositiveActionButton: String,
    ) {
        activity?.let {
            val alertDialogBuilder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(
                    it
                )
            alertDialogBuilder.setTitle(strTitle)
            alertDialogBuilder.setMessage(strMessage)
                .setPositiveButton(strPositiveActionButton,
                    DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
            val alertDialog: androidx.appcompat.app.AlertDialog = alertDialogBuilder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }
    }


    override fun showAlertWithSingleButtonCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        alertDialogSingleCallback: AlertDialogSingleCallback,
    ) {
        activity?.let {
            val alertDialogBuilder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(
                    it
                )
            alertDialogBuilder.setTitle(strTitle)
            alertDialogBuilder.setMessage(strMessage)
                .setPositiveButton(strPositiveActionButton,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                        alertDialogSingleCallback.onPositiveActionButtonClick()
                    })
            val alertDialog: androidx.appcompat.app.AlertDialog = alertDialogBuilder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }
    }

    override fun showAlertWithMultipleCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        strNegativeActionButton: String,
        alertDialogMultipleCallback: AlertDialogMultipleCallback,
    ) {
        activity?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = this.layoutInflater
            val dialogView = DataBindingUtil.inflate<DialogConfirmationBinding>(
                inflater,
                R.layout.dialog_confirmation,
                null,
                false
            )
            dialogBuilder.setView(dialogView.root)

            dialogView.tvTitle.text = strTitle
            dialogView.tvMessage.text = strMessage
            dialogView.tvYes.text = strPositiveActionButton
            dialogView.tvNo.text = strNegativeActionButton

            if (strMessage.isEmpty()) {
                dialogView.tvMessage.visibility = View.GONE
            }

            val alertDialog = dialogBuilder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
            // set the margin
            alertDialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT;
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.window?.setGravity(Gravity.CENTER)

            dialogView.tvYes.setOnClickListener {
                alertDialog.dismiss()
                alertDialogMultipleCallback.onPositiveActionButtonClick()
            }
            dialogView.tvNo.setOnClickListener {
                alertDialog.dismiss()
                alertDialogMultipleCallback.onNegativeActionButtonClick()
            }
        }
    }

    override fun showLogoutAlert() {
        activity?.let {
            val alertDialogBuilder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(
                    it
                )
            alertDialogBuilder.setTitle(getString(R.string.log_out))
            alertDialogBuilder.setMessage(getString(R.string.logout_message))
                .setPositiveButton(it.getString(R.string.ha),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
//                        PreferenceHelper.clearWhenLogout()
                        callNextScreen(Intent(it, SplashScreenActivity::class.java), null)
                        it.finish()
                    })
                .setNegativeButton(it.getString(R.string.nahi),
                    DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
            val alertDialog: androidx.appcompat.app.AlertDialog = alertDialogBuilder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.setOnShowListener {
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            }
            alertDialog.show()
        }
    }

    private fun isProgressShowing(): Boolean {
        try {
            if (mDialog != null
                && mDialog!!.isShowing
            ) return true
        } catch (e: Exception) {
        }
        return false
    }

    override fun showKeyboard(editText: EditText) {
        activity?.let { activity ->
            Timer("Timer", false)
                .schedule(50) {
                    activity.runOnUiThread(java.lang.Runnable {
                        editText.requestFocus()
                        editText.setSelection(editText.text?.length!!)
                        val manager =
                            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    })
                }
        }
    }

    override fun showKeyboard(activity: Activity, editText: EditText) {
        activity?.let { activity ->
            Timer("Timer", false)
                .schedule(50) {
                    activity.runOnUiThread(java.lang.Runnable {
                        val checkFocus: Boolean = editText.requestFocus()
                        if (checkFocus) {
                            editText.inputType = InputType.TYPE_CLASS_PHONE
                        }
                        val manager =
                            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    })
                }
        }
    }

    override fun showNumericKeyboardWithDecimal(activity: Activity, editText: EditText) {
        activity?.let {
            Timer("Timer", false)
                .schedule(50) {
                    activity.runOnUiThread(java.lang.Runnable {
                        val checkFocus: Boolean = editText.requestFocus()
                        if (checkFocus) {
                            editText.inputType =
                                InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
                        }
                        val manager =
                            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.toggleSoftInput(
                            InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY
                        )
                    })
                }
        }
    }

    override fun showKeyboard(activity: Activity, editText: EditText, inputTpye: Int) {
        activity?.let {
            Timer("Timer", false)
                .schedule(50) {
                    activity.runOnUiThread(java.lang.Runnable {
                        val checkFocus: Boolean = editText.requestFocus()
                        if (checkFocus) {
                            editText.inputType = inputTpye
                        }
                        val manager =
                            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                    })
                }
        }
    }

    override fun hideKeyboard(editText: EditText) {
        activity?.let { activity ->
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    override fun hideKeyboard(activity: Activity) {
        try {
            val inputManager: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusedView = activity.currentFocus
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    currentFocusedView.windowToken,
                    0
                )
            }
        } catch (e: java.lang.Exception) {
        }
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
        val profileUpdate = HashMap<String, Any>()
        profileUpdate[BaaSConstantsUI.CL_USER_NAME] = name
        profileUpdate[BaaSConstantsUI.CL_USER_IDENTITY] = identity.toString() // String or number
        profileUpdate[BaaSConstantsUI.CL_USER_EMAIL] = email// Email address of the user
        profileUpdate[BaaSConstantsUI.CL_USER_PHONE] =
            phone.toString() // Phone (with the country code, starting with +)
        profileUpdate[BaaSConstantsUI.CL_USER_GENDER] = gender// Can be either M or F
        profileUpdate[BaaSConstantsUI.CL_USER_EMPLOYED] = employed // Can be either Y or N
        profileUpdate[BaaSConstantsUI.CL_USER_EDUCATION] =
            education // Can be either Graduate, College or School
        profileUpdate[BaaSConstantsUI.CL_USER_DOB] = dob
        profileUpdate[BaaSConstantsUI.CL_USER_TIME_STAMP] = Date()
        profileUpdate[BaaSConstantsUI.CL_USER_ADDRESS] = address.toString()
        profileUpdate[BaaSConstantsUI.CL_USER_PHOTO] = photo
        cleverTapDefaultInstance?.pushProfile(profileUpdate)
    }

    override fun cleverTapUserOnBoardingEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date
    ) {
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_ONBOARDING,
            prodViewedAction
        )
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
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_DOB to dob,
            BaaSConstantsUI.CL_USER_EMPLOYEE_ID to empId,
            BaaSConstantsUI.CL_USER_ADDRESS to dAddress,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_ONBOARDING,
            prodViewedAction
        )
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
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_ITEM to item,
            BaaSConstantsUI.CL_USER_EVENT_STATUS to status
        )
        cleverTapDefaultInstance?.pushEvent(BaaSConstantsUI.CL_USER_EVENT_HOME, prodViewedAction)
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
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_STATUS to status,
            BaaSConstantsUI.CL_USER_EVENT_REASON to reason
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_CARD_MANAGEMENT,
            prodViewedAction
        )
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
        item: String,
    ) {
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_TYPE to type,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_FILTER to transactionFilter,
            BaaSConstantsUI.CL_USER_EVENT_ACCOUNT_TYPE_FILTER to accountTypeFilter,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_ID to transactionID,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_STATUS to transactionStatus,
            BaaSConstantsUI.CL_USER_EVENT_ITEM to item
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_PASSBOOK,
            prodViewedAction
        )
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
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_AMOUNT_PAID to amountPaid,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_CHARGES to transactionCharges,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_STATUS to transactionStatus,
            BaaSConstantsUI.CL_USER_EVENT_RECEIVER_BANK_IFSCODE to receiverBankIFSCode,
            BaaSConstantsUI.CL_USER_EVENT_ITEM to item
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_MONEY_TRANSFER,
            prodViewedAction
        )
    }

    override fun cleverTapUserProfileEvent(
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
        advanceStatus: String,
    ) {
        val prodViewedAction = mapOf(
            BaaSConstantsUI.CL_USER_EVENT_NAME to eventName,
            BaaSConstantsUI.CL_USER_EVENT_ID to eventID,
            BaaSConstantsUI.CL_SESSION to session,
            BaaSConstantsUI.CL_IMEI_NUMBER to IMEI,
            BaaSConstantsUI.CL_USER_MOBILE_NUMBER to mobileNumber,
            BaaSConstantsUI.CL_USER_TIME_STAMP to timeStamp,
            BaaSConstantsUI.CL_USER_EVENT_ADVANCE_STATUS to advanceStatus,
            BaaSConstantsUI.CL_USER_EVENT_TRANSACTION_AMOUNT to amount,
            BaaSConstantsUI.CL_USER_EVENT_ITEM to item,
            BaaSConstantsUI.CL_USER_EVENT_ACCOUNT_NUMBER to accountNumber
        )
        cleverTapDefaultInstance?.pushEvent(
            BaaSConstantsUI.CL_USER_EVENT_ADVANCE,
            prodViewedAction
        )
    }

    override fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { _, _ ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    fun validatePanNumber(panNum: String): Boolean {
        if (panNum.isNullOrEmpty() || panNum.length < PAN_NUMBER_LENGTH || panNum.length > PAN_NUMBER_LENGTH)
            return false
        else if (panNum.length == PAN_NUMBER_LENGTH)
            if (checkStringForAllLetterUsingRegex(panNum)
                || (panNum.indexOf(     // check 4th char is P
                    "P",
                    POSITION_P_PAN_NUMBER
                ) == -1)
                || !(isAllChars(panNum.substring(0, 4))) // to check are 1st 5 digits chars
                || !isAllChars(
                    panNum.substring(9) // to check last char is string
                )
                || !isInteger(
                    panNum.substring(
                        5,
                        8
                    )
                ) // to check char from 6th to 9th possition are numeric or not
            )
                return false
        return true
    }

    open fun isAllChars(name: String): Boolean {
        val chars = name.toCharArray()
        for (c in chars) {
            if (!Character.isLetter(c)) {
                return false
            }
        }
        return true
    }

    open fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false
    fun checkStringForAllLetterUsingRegex(input: String): Boolean {
        return input.lowercase(Locale.getDefault())
            .replace("[^a-z]".toRegex(), "")
            .replace("(.)(?=.*\\1)".toRegex(), "")
            .length == 26
    }

    open fun onlyDigits(str: String?): Boolean {
        // Regex to check string
        // contains only digits
        val regex = "[0-9]+"

        // Compile the ReGex
        val p = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (str == null) {
            return false
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        val m: Matcher = p.matcher(str)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }

    fun shakeError(editText: EditText): TranslateAnimation? {
        editText.selectAll();
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.setDuration(500)
        shake.setInterpolator(CycleInterpolator(7f))
        return shake
    }

    fun shakeView(): TranslateAnimation? {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.setDuration(500)
        shake.setInterpolator(CycleInterpolator(7f))
        return shake
    }


    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    fun getIMEI(): String? {
        BaasUIApp.ctx?.let {
            var imei: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    imei = telephonyManager!!.imei
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //this change is for Android 10 as per security concern it will not provide the imei number.
                if (imei == null) {
                    imei = Settings.Secure.getString(
                        it.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
            } else {
                imei = if (telephonyManager!!.deviceId != null && telephonyManager!!.deviceId != "000000000000000") {
                    telephonyManager!!.deviceId
                } else {
                    Settings.Secure.getString(it.contentResolver, Settings.Secure.ANDROID_ID)
                }
            }
            return imei
        }
        return ""

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (telephonyManager!!.phoneCount == 2) {
                    telephonyManager!!.getImei(0);
                } else {
                    telephonyManager!!.imei;
                }
            } else {
                if (telephonyManager!!.phoneCount == 2) {
                    telephonyManager!!.getDeviceId(0);
                } else {
                    telephonyManager!!.getDeviceId();
                }
            }
        } else {
            imei = telephonyManager!!.getDeviceId();
        }

        return imei*/


    }

    fun launchZendeskSDK() {
        if (SessionManager.getInstance(this).accessToken.isNullOrEmpty()
            || SessionManager.getInstance(this).zdUrl.isNullOrEmpty()
        ) {
            return
        }
        Zendesk.INSTANCE.init(
            applicationContext,
            SessionManager.getInstance(this).zdUrl!!,
            SessionManager.getInstance(this).zdAppId!!,
            SessionManager.getInstance(this).zdClientId!!
        )
        Support.INSTANCE.init(Zendesk.INSTANCE)

        val identity: Identity = JwtIdentity(SessionManager.getInstance(this).accessToken)
        Zendesk.INSTANCE.setIdentity(identity)
        Support.INSTANCE.init(Zendesk.INSTANCE)
        HelpCenterActivity.builder().show(this)
    }
}