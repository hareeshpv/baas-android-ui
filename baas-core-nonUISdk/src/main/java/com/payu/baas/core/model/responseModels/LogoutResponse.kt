package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class LogoutResponse : ApiResponse() {

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null
}