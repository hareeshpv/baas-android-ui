package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import org.json.JSONArray

class KarzaSelfieResponse  : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("image")
    var image: Array<String>? = null

    @SerializedName("requestId")
    var requestId: String? = null

    @SerializedName("score")
    var statusCode: Double? = null

}