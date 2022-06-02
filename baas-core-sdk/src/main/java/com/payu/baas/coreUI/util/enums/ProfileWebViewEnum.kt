package com.payu.baas.coreUI.util.enums

enum class ProfileWebViewEnum {
    HELP {
        override fun getValue(): String = "1"
    },
    RATES_CHARGES {
        override fun getValue(): String = "2"
    },
    FAQS {
        override fun getValue(): String = "3"
    },
    NOTIFICATIONS {
        override fun getValue(): String = "4"
    },
    OFFERS {
        override fun getValue(): String = "5"
    };

    abstract fun getValue(): String
}