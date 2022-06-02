package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class VerifyOtp {
    @SerializedName("type")
    var type: String? = null
    @SerializedName("code")
    var code: String? = null
    @SerializedName("identity")
    var identity: String? = null
}