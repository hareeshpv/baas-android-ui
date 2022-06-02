package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.TransactionMode


class GetTransactionModeResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("transactionModes")
    var transactionModes: List<TransactionMode>? = null

}