package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class CardDetailResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    var code: String? = null

    var status: String? = "INACTIVE"

    @SerializedName("lastFourDigits")
    var lastFourDigits: String? = null

    @SerializedName("allowAtmTransaction")
    var allowAtmTransaction: Boolean? = false

    @SerializedName("allowPOSTransaction")
    var allowCardSwipe: Boolean? = false

    @SerializedName("allowECOMTransaction")
    var allowOnlineTransaction: Boolean? = false
    var cardReceived: Boolean? = false
}