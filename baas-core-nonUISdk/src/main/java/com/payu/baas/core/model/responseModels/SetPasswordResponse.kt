package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class SetPasswordResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("error")
    var error: String? = null

    @SerializedName("msg")
    var msg: String? = null
}