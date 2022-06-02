package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetAddressResponse : ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null

    var addressLine1: String? = null
    var addressLine2: String? = null
    var city: String? = null
    var pinCode: String? = null
    var stateId: String? = null
}