package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
 class GetTransactionChargesResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("mode")
    var transactionMode: String? = null

    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("DebitIndicator")
    var debitIndicator: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("error")
    var error: String? = null

    @SerializedName("charges")
    var charges: Double = 0.0

}