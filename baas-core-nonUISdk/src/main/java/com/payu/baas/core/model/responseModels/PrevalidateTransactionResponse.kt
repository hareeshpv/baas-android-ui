package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class PrevalidateTransactionResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("maxAmountTransfer")
    var maxAmountTransfer: Double = 0.0

    @SerializedName("chargedAmount")
    var chargedAmount: Double = 0.0

    @SerializedName("advanceChargeAmount")
    var advanceChargeAmount: Double = 0.0
}