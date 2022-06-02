package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class VerifyOtpResponse : ApiResponse() {
    @SerializedName("userState")
    var userState: String? = null

    @SerializedName("msg")
    var message: String? = null

    @SerializedName("user_access_token")
    var userToken: String? = null

    @SerializedName("expires_in")
    var expiresIn: String? = null

    @SerializedName("scope")
    var scope: String? = null
}