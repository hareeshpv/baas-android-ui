package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class KarzaSessionResponse  : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("result")
    var result: KarzaResult? = null

    @SerializedName("request_id")
    var requestId: String? = null

    @SerializedName("status-code")
    var statusCode: String? = null
    var error: String? = null
    var status: Int? = null


}