package com.payu.baas.core.view.ui.activity.kyc

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.karza.okycmaster.ActionType
import com.karza.okycmaster.KDataListener
import com.karza.okycmaster.KManager
import com.karza.okycmaster.SDKEnv
import com.payu.baas.core.R
import com.payu.baas.core.model.responseModels.KarzaAadharResponse
import com.payu.baas.core.model.responseModels.KarzaSelfieResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.JsonUtil
import com.payu.baas.core.util.enums.UserState
import org.json.JSONObject

class KarzaActivity : AppCompatActivity(), KDataListener {
    lateinit var kManager: KManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_karza_sdkactivity)
        kManager = KManager.Builder(supportFragmentManager)
            .setContext(applicationContext)
            .setUserToken(SessionManager.getInstance(this).karzaUserToken)
            .setDataListener(this)
            .setEnv(SDKEnv.PROD)
            .build()
        kManager.init(R.id.frameLayout);
    }

    override fun onStarted() {
    }

    override fun onExit() {
    }

    override fun permissionMissing(permissions: Array<out String>?) {
        ActivityCompat.requestPermissions(this, permissions!!, 204)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0].equals(PackageManager.PERMISSION_GRANTED)) {
            kManager = KManager.Builder(supportFragmentManager)
                .setContext(applicationContext)
                .setUserToken(com.payu.baas.core.storage.SessionManager.getInstance(this).karzaUserToken)
                .setDataListener(this)
                .setEnv(SDKEnv.TEST)
                .build()
            kManager.init(R.id.frameLayout);
        }
    }

    override fun onEvent(
        httpStatus: Int, apiStatus: Int, docType: String?,
        actionType: ActionType?, data: JSONObject?
    ) {
        val builder = if ("HttpStatus : " + httpStatus + " , " +
            "apiStatus : " + apiStatus + " , " +
            "docType : " + docType + " , " +
            "ActionType : " + actionType!!.type + " , " +
            "data : " + data != null
        ) data.toString() else "data is null"
        if (docType.equals("from_aadhaar_xml")) {
            var response: KarzaAadharResponse =
                JsonUtil.toObject(builder, KarzaAadharResponse::class.java) as KarzaAadharResponse
            SessionManagerUI.getInstance(this).karzaAadhaarFileContent = response.zipFileBase64
            SessionManagerUI.getInstance(this).karzaAadhaarFileCode = response.sharecode
            SessionManagerUI.getInstance(this).userStatusCode =
                UserState.AADHARXML_SAVED_LOCAL.getValue()
        } else if (docType.equals("silent_liveness")) {
            var response: KarzaSelfieResponse =
                JsonUtil.toObject(builder, KarzaSelfieResponse::class.java) as KarzaSelfieResponse
            SessionManagerUI.getInstance(this).karzaUserSelfie =
                response.image?.get(0)
            if (!(response.image?.get(0).isNullOrEmpty()))
                SessionManagerUI.getInstance(this).userStatusCode =
                    UserState.SELFIE_SAVED_LOCAL.getValue()
        } else if (docType.equals("from_pan")) {

        } else if (actionType == ActionType.REPORT_DATA) {
            SessionManagerUI.getInstance(this).karzaVerificationResponse = builder
            SessionManagerUI.getInstance(this).userStatusCode =
                UserState.AADHARXML_SAVED_LOCAL.getValue()
        } else if (actionType == ActionType.END) { //docType contains value for which we working on like it will be liveness if working on selfie,
            if (SessionManagerUI.getInstance(this).userStatusCode!!.equals(UserState.AADHARXML_SAVED_LOCAL))
                SessionManager.getInstance(this).karzaTransactionId = null
            finish()
        } else if (actionType == ActionType.ERROR) {
            finish()
            Toast.makeText(this, "Authorization error", Toast.LENGTH_LONG)
        }
        //adhar_xml for adhar verification
    }

}