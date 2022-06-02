package com.payu.baas.coreUI.view.callback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.util.*

interface BaseCallback {

    fun showDialog(message: String)

    fun callNextScreen(intent: Intent, bundle: Bundle?, finsh: Boolean = false)

    fun showProgress(strMessage: String)

    fun hideProgress()

    fun showToastMessage(strMessage: String?)

    fun isInternetAvailable(redirectToNoInternetActivity: Boolean = false): Boolean

    fun showTechinicalError()

    fun showAlertWithSingleButton(
        strTitle: String?,
        strMessage: String,
        strPositiveActionButton: String
    )

    fun showAlertWithSingleButtonCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        alertDialogSingleCallback: AlertDialogSingleCallback
    )

    fun showAlertWithMultipleCallBack(
        strTitle: String,
        strMessage: String,
        strPositiveActionButton: String,
        strNegativeActionButton: String,
        alertDialogMultipleCallback: AlertDialogMultipleCallback
    )

    fun showLogoutAlert()

    fun showKeyboard(editText: EditText)
    fun showKeyboard(activity: Activity, editText: EditText)
    fun showNumericKeyboardWithDecimal(activity: Activity, editText: EditText)
    fun showKeyboard(activity: Activity, editText: EditText, inputTpye: Int)

    fun hideKeyboard(editText: EditText)

    fun hideKeyboard(activity: Activity)

    fun cleverTapUserProfile(
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
    )

    fun cleverTapUserOnBoardingEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date
    )
    fun cleverTapUserOnBoardingEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        dob: String?,
        empId: String?,
        dAddress: String?,
        timeStamp: Date
    )
    fun cleverTapUserHomeEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        item: String,
        status: String
    )

    fun cleverTapUserCardManagementEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        status: String,
        reason: String
    )

    fun cleverTapUserPassbookEvent(
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
    )

    fun cleverTapUserMoneyTransferEvent(
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
    )

    fun cleverTapUserProfileEvent(
        eventName: String,
        eventID: String,
        session: String?,
        IMEI: String?,
        mobileNumber: String?,
        timeStamp: Date,
        item: String
    )

    fun cleverTapUserAdvanceEvent(
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
    )

    fun getLocationFromAddress(context: Context, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.getLatitude(), location.getLongitude())
        } catch (ex: IOException) {
        }
        return p1
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.getInetAddresses())
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        val sAddr: String = addr.getHostAddress()
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: java.lang.Exception) {
        } // for now eat exceptions
        return ""
    }

    // method to make background task
    suspend fun getPublicIP(): String {
        var publicIP = ""
        try {
            val data = Scanner(
                URL(
                    "https://api.ipify.org"
                )
                    .openStream(), "UTF-8"
            )
                .useDelimiter("\\A")
            publicIP = data.next()
        } catch (e: IOException) {
        }
        return publicIP
    }
}