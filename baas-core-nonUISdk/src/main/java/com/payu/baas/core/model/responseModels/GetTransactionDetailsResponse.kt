package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

 class GetTransactionDetailsResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("mode")
    var transactionMode: String? = null

    @SerializedName("amount")
    var amount: Double? = null

    @SerializedName("debitCreditIndicator")
    var debitCreditIndicator: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("error")
    var error: String? = null

}