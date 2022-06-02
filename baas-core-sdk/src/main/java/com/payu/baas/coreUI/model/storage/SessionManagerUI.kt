package com.payu.baas.coreUI.model.storage

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.BaaSConstantsUI.PREFS_FILE_NAME

class SessionManagerUI private constructor(val context: Context) {


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SessionManagerUI? = null
        fun getInstance(context: Context): SessionManagerUI {
            val checkInstance = instance
            if (checkInstance != null) {
                return checkInstance
            }

            return synchronized(this) {
                val checkInstanceAgain = instance
                if (checkInstanceAgain != null) {
                    checkInstanceAgain
                } else {
                    val created = SessionManagerUI(context)
                    instance = created

                    // assign constants
//                    if (getInstance(context).deviceBindingId.isNullOrEmpty())
//                    created.deviceBindingId = UUID.randomUUID().toString()
//                    created.brandToken = BaaSConstantsUI.BS_VALUE_BRAND_TOKEN

                    created

                }
            }
        }
    }

    var karzaUserSelfie: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_SP_KARZA_USER_SELFIE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_SP_KARZA_USER_SELFIE, value)
        }

    var karzaAadhaarFileContent: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_SP_KARZA_AADHAAR_CONTENT)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_SP_KARZA_AADHAAR_CONTENT, value)
        }
    var karzaAadhaarFileCode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_SP_KARZA_AADHAAR_CODE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_SP_KARZA_AADHAAR_CODE, value)
        }

    var karzaVerificationResponse: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_SP_KARZA_AADHAR_RESPONSE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_SP_KARZA_AADHAR_RESPONSE, value)
        }
    var userStatusCode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_SP_USER_STATE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_SP_USER_STATE, value)
        }
    var registeredMobileNumber: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.REGISTERED_MOBILE_NUMBER)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.REGISTERED_MOBILE_NUMBER, value)
        }
    var userMobileNumber: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.USER_MOBILE_NUMBER)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.USER_MOBILE_NUMBER, value)
        }
    var userEmpId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.USER_EMPLOYEE_ID)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.USER_EMPLOYEE_ID, value)
        }
    var oldPasscode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.OLD_PASSCODE)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.OLD_PASSCODE, value)
        }
    var permissionResult: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.PERMISSION_RESULT)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.PERMISSION_RESULT, value)
        }

    //    var beneficiaryId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//        .getString(BaaSConstantsUI.BS_SP_USER_BENIFICIARY_ID)
//        set(value) {
//            field = value
//            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//                .putString(BaaSConstantsUI.BS_SP_USER_BENIFICIARY_ID, value)
//        }
//    var beneficiaryIFSCECode: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//        .getString(BaaSConstantsUI.BS_SP_USER_BENIFICIARY_IFSC_CODE)
//        set(value) {
//            field = value
//            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//                .putString(BaaSConstantsUI.BS_SP_USER_BENIFICIARY_IFSC_CODE, value)
//        }
    var userPanDetails: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_USER_PAN_DETAILS)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_USER_PAN_DETAILS, value)
        }
    var cardDeliveryAddress: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_CARD_DELIVERY_ADDRESS)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_CARD_DELIVERY_ADDRESS, value)
        }

    //    var isDeliveryAddressUpdated: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//        .getString(BaaSConstantsUI.BS_KEY_IS_ADDRESS_UPDATED)
//        set(value) {
//            field = value
//            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//                .putString(BaaSConstantsUI.BS_KEY_IS_ADDRESS_UPDATED, value)
//        }
    var otherCardBlockReason: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_OTHER_CARD_BLOCK_REASON)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_OTHER_CARD_BLOCK_REASON, value)
        }
    var isFromProfileToBeneficiaryListActivity: Int? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getInt(BaaSConstantsUI.BS_KEY_IS_FROM_PFORILE_TO_BENEFICIARY)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_IS_FROM_PFORILE_TO_BENEFICIARY, value!!)
        }
    var transactionChangedLimit: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_CHANGE_TRANSACTION_LIMIT_SCREEN)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_CHANGE_TRANSACTION_LIMIT_SCREEN, value)
        }
    var isOnlineTransactionOn: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_IS_ONLINE_TRANSACTION_ON)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_IS_ONLINE_TRANSACTION_ON, value)
        }
    var isCardDelivered: Int? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getInt(BaaSConstantsUI.BS_KEY_IS_CARD_DELIVERED)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_IS_CARD_DELIVERED, value!!)
        }
//    var mockServerBaseUrl: String? = null
//        set(value) {
//            field = value
//            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
//                .putString(BaaSConstantsUI.BS_SP_ACCESS_TOKEN, value)
//        }

    var emailId: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_EMAIL)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_EMAIL, value)
        }
    var primaryColor: Int
        get() {
            val color = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .getInt(BaaSConstantsUI.BS_KEY_PRIMARY_COLOR)
            return if (color != -1) {
                color
            } else {
                ContextCompat.getColor(context, R.color.primary)
            }
        }
        set(value) {
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_PRIMARY_COLOR, value)
        }

    var primaryDarkColor: Int
        get() {
            val color = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .getInt(BaaSConstantsUI.BS_KEY_PRIMARY_DARK_COLOR)
            return if (color != -1) {
                color
            } else {
                ContextCompat.getColor(context, R.color.primary_dark)
            }
        }
        set(value) {
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_PRIMARY_DARK_COLOR, value)
        }

    var accentColor: Int
        get() {
            val color = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .getInt(BaaSConstantsUI.BS_KEY_ACCENT_COLOR)
            return if (color != -1) {
                color
            } else {
                ContextCompat.getColor(context, R.color.accent)
            }
        }
        set(value) {
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_ACCENT_COLOR, value)
        }
    var isCardBlocked: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_IS_CARD_BLOCK)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_IS_CARD_BLOCK, value)
        }
    var userName: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.USER_NAME)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.USER_NAME, value)
        }
    var cardActiveStatus: String? = PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
        .getString(BaaSConstantsUI.BS_KEY_CARD_ACTIVE_STATUS)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putString(BaaSConstantsUI.BS_KEY_CARD_ACTIVE_STATUS, value)
        }
    var isAddressConfirmedBeforeCardBlock: Int? =
        PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
            .getInt(BaaSConstantsUI.BS_KEY_IS_AADDRESS_CONFIRMED_BEFORE_CARD_BLOCK)
        set(value) {
            field = value
            PreferenceUtils.getInstance(context, PREFS_FILE_NAME)
                .putInt(BaaSConstantsUI.BS_KEY_IS_AADDRESS_CONFIRMED_BEFORE_CARD_BLOCK, value!!)
        }
}