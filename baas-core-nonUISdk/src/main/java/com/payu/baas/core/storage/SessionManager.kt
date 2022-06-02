package com.payu.baas.core.storage

import android.annotation.SuppressLint
import android.content.Context
import com.payu.baas.core.util.BaaSConstants
import com.payu.baas.core.util.BaaSConstants.PREFS_FILE_NAME
import com.payu.baas.core.util.BaasEncryption
import java.util.*

class SessionManager private constructor(val context: Context) {


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SessionManager? = null
        fun getInstance(context: Context): SessionManager {
            val checkInstance = instance
            if (checkInstance != null) {
                return checkInstance
            }

            return synchronized(this) {
                val checkInstanceAgain = instance
                if (checkInstanceAgain != null) {
                    checkInstanceAgain
                } else {
                    val created = SessionManager(context)
                    instance = created

                    // assign constants
                    if (getInstance(context).deviceBindingId.isNullOrEmpty())
                        created.deviceBindingId = UUID.randomUUID().toString()
                    created.brandToken = BaaSConstants.BS_VALUE_BRAND_TOKEN

                    created

                }
            }
        }
    }

    var accessToken: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_ACCESS_TOKEN)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_ACCESS_TOKEN, field)
        }

    var deviceBindingId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_DEVICE_BINDING_ID)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {

            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_DEVICE_BINDING_ID, field)
        }

    var brandToken: String? = null
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_BRAND_TOKEN, field)
        }

    var cardImage: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_CARD_IMAGE)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_CARD_IMAGE, field)
        }
    var applicationId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_APPLICATION_ID)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_APPLICATION_ID, value)
        }
    var karzaToken: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_TOKEN)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_TOKEN, value)
        }
    var karzaTransactionId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_TRANSACTION_ID)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_TRANSACTION_ID, value)
        }
    var karzaUserToken: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_USER_TOKEN)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_USER_TOKEN, value)
        }
    var karzaUserSelfie: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_USER_SELFIE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_USER_SELFIE, value)
        }

    var karzaAadhaarFileContent: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_AADHAAR_CONTENT)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_AADHAAR_CONTENT, value)
        }
    var karzaAadhaarFileCode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_AADHAAR_CODE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_AADHAAR_CODE, value)
        }

    var karzaVerificationResponse: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_AADHAR_RESPONSE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_AADHAR_RESPONSE, value)
        }
    var userStatusCode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_USER_STATE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_USER_STATE, value)
        }
    var beneficiaryId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_USER_BENIFICIARY_ID)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_USER_BENIFICIARY_ID, value)
        }
    var beneficiaryIFSCECode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_USER_BENIFICIARY_IFSC)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_USER_BENIFICIARY_IFSC, value)
        }
    var karzaKey: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_KARZA_KEY)
        get() = if (field.isNullOrEmpty())
            null
        else
//            BaasEncryption.decrypt(field)
            BaasEncryption.decryptUrl(field!!, accessToken!!, deviceBindingId!!)
        set(value) {
            field = value
//            field = BaasEncryption.encrypt(value!!)
//            field = BaasEncryption.encryptUrl(value!!,accessToken!!,deviceBindingId!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_KARZA_KEY, value)
        }

    var s3BucketUrl: String? = null
        get() = if (PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .getString(BaaSConstants.BS_SP_S3_BUCKET_URL).isNullOrEmpty()
        )
            null
        else
            BaasEncryption.decrypt(
                PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                    .getString(BaaSConstants.BS_SP_S3_BUCKET_URL)
            )
        set(value) {
            field = BaasEncryption.encrypt("$value?key=")
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_S3_BUCKET_URL, field)
        }
    var zdUrl: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_ZD_URL)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_ZD_URL, field)
        }
    var zdAppId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_ZD_APP_ID)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_ZD_APP_ID, field)
        }
    var zdClientId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstants.BS_SP_ZD_CLIENT_ID)
        get() = if (field.isNullOrEmpty())
            null
        else
            BaasEncryption.decrypt(field)
        set(value) {
            field = BaasEncryption.encrypt(value!!)
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstants.BS_SP_ZD_CLIENT_ID, field)
        }
}