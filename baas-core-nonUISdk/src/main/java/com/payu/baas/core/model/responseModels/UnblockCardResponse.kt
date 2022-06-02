package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class UnblockCardResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("msg")
    var message: String? = null

}