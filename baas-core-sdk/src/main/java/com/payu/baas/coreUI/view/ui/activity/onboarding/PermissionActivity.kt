package com.payu.baas.coreUI.view.ui.activity.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityPermissionBinding
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.GPSTracker
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.coreUI.view.ui.activity.webView.WebViewActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*


class PermissionActivity : BaseActivity() {
    lateinit var binding: ActivityPermissionBinding
    private val REQUEST_CODE = 1
    private lateinit var PERMISSIONS: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
        activity = this

        setupUI()
    }

    private fun setupUI() {
        SessionManagerUI.getInstance(applicationContext).permissionResult = "0"
        PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE
        )
    }

    fun Thik_Hai(view: View) {
        allowPermission()
    }


    private fun allowPermission() {
        if (requestPermission(this, REQUEST_CODE, *PERMISSIONS)) {
            val gpsTracker = GPSTracker(this)
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                if (gpsTracker.location == null) {
                } else {
                }

                SessionManagerUI.getInstance(applicationContext).permissionResult = "1"
                SessionManagerUI.getInstance(applicationContext).userStatusCode =
                    UserState.PERMISSION_ASSIGNED.getValue()
                callNextScreen(Intent(this, MobileVerificationActivity::class.java), null)
                finish()
            } else {
                gpsTracker.showSettingsAlert()
            }
        }
//        if (!hasPermission(*PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_CODE);
//        }
    }

    private fun showSnackBar(message: String) {
        SimpleCustomSnackbar.make(
            binding.relativeContainer, message,
            Snackbar.LENGTH_LONG, R.drawable.pending_status
        )?.show()
    }

    fun permission_backarrow(view: View) {
        finish()
    }

    fun hasPermission(vararg permissions: String): Boolean = permissions.all {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.toString()
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
//        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults)
        if (permsRequestCode == 1) {
            var size = grantResults.size
            size = size - 1
//            var isDenied = false
//            for (i in 0..size) {
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    isDenied = true
//                    break
//                }
//            }
            for (i in 0..size) {
                var permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    val showRationale: Boolean =
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    if (showRationale) {
                        showSnackBar(BaaSConstantsUI.WITHOUT_PERMISSION)
                    } else {
                        showSnackBar(BaaSConstantsUI.MANUAL_ALLOW_PERMISSIONS)
                    }
                    return
                }
            }
            val gpsTracker = GPSTracker(this)
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                if (gpsTracker.location == null) {
                } else {
                }

                SessionManagerUI.getInstance(applicationContext).permissionResult = "1"
                SessionManagerUI.getInstance(applicationContext).userStatusCode =
                    UserState.PERMISSION_ASSIGNED.getValue()
                callNextScreen(Intent(this, MobileVerificationActivity::class.java), null)
                finish()
            } else {
                gpsTracker.showSettingsAlert()
            }

//            if (isDenied) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.SEND_SMS
//                    )
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.READ_PHONE_STATE
//                    )
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//                ) {
//                    showSnackBar(BaaSConstantsUI.WITHOUT_PERMISSION)
////                    requestPermission(this,REQUEST_CODE,*PERMISSIONS)
//                } else {
//                    showSnackBar(BaaSConstantsUI.MANUAL_ALLOW_PERMISSIONS)
//                }
//            } else {
//                SessionManagerUI.getInstance(applicationContext).permissionResult = "1"
//                SessionManagerUI.getInstance(applicationContext).userStatusCode =
//                    UserState.PERMISSION_ASSIGNED.getValue()
//                callNextScreen(Intent(this, MobileVerificationActivity::class.java), null)
//                finish()
//            }
        }

    }

    fun privacyPolicy(view: View) {
        val mBundle = Bundle()
        mBundle.putString(BaaSConstantsUI.USER_URL, getString(R.string.privacy_policy_link))
        callNextScreen(
            Intent(
                applicationContext,
                WebViewActivity::class.java
            ), mBundle
        )
    }
//    private fun hasPermissions(context: Context?, vararg PERMISSIONS: Array<String>): Boolean {
//        if (context != null && PERMISSIONS != null) {
//            for (permission in PERMISSIONS) {
//                if (ActivityCompat.checkSelfPermission(context,
//                        permission.toString()) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return false
//                }
//            }
//        }
//        return true
//    }

    fun termsCondition(view: View) {
        val mBundle = Bundle()
        mBundle.putString(BaaSConstantsUI.USER_URL, getString(R.string.terms_condition_link))
        callNextScreen(
            Intent(
                applicationContext,
                WebViewActivity::class.java
            ), mBundle
        )
    }


    fun requestPermission(
        activity: Activity?, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionsNeeded: ArrayList<String> = ArrayList()
        for (s in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(activity!!, s)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted && hasPermission
            if (!hasPermission) {
                permissionsNeeded.add(s)
            }
        }
//        if (!granted)
//            ActivityCompat.requestPermissions(
//                activity!!,
//                permissionsNeeded.toArray(arrayOfNulls(permissionsNeeded.size)),
//                requestCode
//            )
        return if (granted) {
            true
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                permissionsNeeded.toArray(arrayOfNulls(permissionsNeeded.size)),
                requestCode
            )
            false
        }
    }
}



