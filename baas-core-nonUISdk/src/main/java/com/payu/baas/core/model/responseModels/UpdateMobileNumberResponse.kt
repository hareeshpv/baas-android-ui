package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class UpdateMobileNumberResponse() : ApiResponse() {

    @SerializedName("msg")
    var message: String? = null

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null


}