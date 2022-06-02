package com.payu.baas.core.enums

enum class ContentType {
    FORM_DATA {
        override fun getValue(): String {
            return "multipart/form-data"
        }
    },
    APPLICATION_JSON {
        override fun getValue(): String = "application/json"
    };
    abstract fun getValue(): String
}