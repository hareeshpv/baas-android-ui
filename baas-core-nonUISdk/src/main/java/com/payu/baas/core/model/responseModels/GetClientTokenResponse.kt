package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

data class GetClientTokenResponse(
    @SerializedName("access_token")
    var accessToken: String? = null) :
    ApiResponse() {
    @SerializedName("token_type")
    var tokenType: String? = null

    @SerializedName("expires_in")
    var expiresIn: String? = null

    @SerializedName("scope")
    var scope: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("error")
    var error: String? = null

}