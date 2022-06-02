package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetKarzaKeyResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("value")
    var key: String? = null
}