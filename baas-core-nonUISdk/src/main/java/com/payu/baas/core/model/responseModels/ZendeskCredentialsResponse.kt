package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class ZendeskCredentialsResponse : ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null

    var appId: String? = null
    var clientId: String? = null
    var url: String? = null
}