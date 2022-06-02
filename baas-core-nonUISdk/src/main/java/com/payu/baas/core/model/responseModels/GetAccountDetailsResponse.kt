package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

 class GetAccountDetailsResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("accountName")
    var accountName: String? = null

    @SerializedName("accountNumber")
    var accountNumber: String? = null

    @SerializedName("ifscCode")
    var ifscCode: String? = null

    @SerializedName("error")
    var error: String? = null

}