package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class KarzaResult {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("karzaToken")
    var token: String? = null
}