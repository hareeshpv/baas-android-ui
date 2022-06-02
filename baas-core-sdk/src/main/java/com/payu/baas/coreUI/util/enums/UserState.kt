package com.payu.baas.coreUI.util.enums

enum class UserState {
    PERMISSION_ASSIGNED {
        override fun getValue(): String = "00"
    },
    MOBILE_NOT_SUBMITTED { // added if user reinstalls the app in same or another phone or user registers for the 1st time
        override fun getValue(): String = "0"
    },
    MOBILE_SUBMITTED {
        override fun getValue(): String = "0"
    },
    MOBILE_VERIFIED {
        override fun getValue(): String = "1"
    },
    KARZA_APPLICATION_GENERATED {
        override fun getValue(): String = "2"
    },
    LAT_LONG_IP_SAVED {
        override fun getValue(): String = "3"
    },
    SELFIE_SAVED {
        override fun getValue(): String = "4"
    },
    AADHARXML_SAVED {
        override fun getValue(): String = "5"
    },
    KYC_RESULT_SAVED{
        override fun getValue(): String = "6"
    },
    KYC_CHECKS_PASSED {
        override fun getValue(): String = "7"
    },
    KYC_CHECKS_FAILED {
        override fun getValue(): String = "8"
    },
    ONBOARDING_IN_PROGRESS_1 {
        override fun getValue(): String = "9"
    },
    ONBOARDING_IN_PROGRESS_2 {
        override fun getValue(): String = "10"
    },
    ONBOARDING_FAILED_1 {
        override fun getValue(): String = "11"
    },
    ONBOARDING_IN_PROGRESS_3 {
        override fun getValue(): String = "12"
    },
    ONBOARDING_FAILED_2 {
        override fun getValue(): String = "13"
    },
    ONBOARDING_IN_PROGRESS_4 {
        override fun getValue(): String = "14"
    },
    ONBOARDING_IN_PROGRESS {
        override fun getValue(): String = "IN PROGRESS"
    },
    ONBOARDING_FAILED {
        override fun getValue(): String = "FAILURE"
    },
    ONBOARDING_SUCCESS {
        override fun getValue(): String = "SUCCESS"
    },
    ONBOARDED {
        override fun getValue(): String = "15"
    },
    PASSCODE_SET {
        override fun getValue(): String = "16"
    },
    LOGIN_DONE {
        override fun getValue(): String = "17"
    },
    PAN_SAVED_LOCAL {
        override fun getValue(): String = "18"
    },
    CARD_DELIVERY_ADDRESS_SAVED_LOCAL {
        override fun getValue(): String = "19"
    },
    SELFIE_SAVED_LOCAL {
        override fun getValue(): String = "20"
    },
    AADHARXML_SAVED_LOCAL {
        override fun getValue(): String = "21"
    },
    KYC_SCREEN_PASSED {
        override fun getValue(): String = "22"
    },
    WELCOM_SCREEN_REACHED {
        override fun getValue(): String = "23"
    },
    LOGGED_OUT {
        override fun getValue(): String = "24"
    }
    ;

    abstract fun getValue(): String
}