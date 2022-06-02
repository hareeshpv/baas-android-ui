package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("access_token") var accessToken: String? = null) :
    ApiResponse() {
    @SerializedName("token_type")
    var tokenType: String? = null

    @SerializedName("expires_in")
    var expiresIn: String? = null

    @SerializedName("scope")
    var scope: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("user_uuid")
    var userUUID: String? = null
}