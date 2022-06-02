package com.payu.baas.core.enums

enum class UserState {
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
    PASSCODE_SET {
        override fun getValue(): String = "16"
    };

    abstract fun getValue(): String
}