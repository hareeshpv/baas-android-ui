package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetUserDetailsResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("mobileNumber")
    var mobileNumber: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("maskedPan")
    var maskedPan: String? = null

    @SerializedName("maskedAadhaar")
    var maskedAadhaar: String? = null

    @SerializedName("selfieLink")
    var selfieLink: String? = null
}