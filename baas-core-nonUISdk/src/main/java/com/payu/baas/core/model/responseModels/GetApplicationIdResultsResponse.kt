package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetApplicationIdResultsResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("id")
    var applicationId: String? = null
}