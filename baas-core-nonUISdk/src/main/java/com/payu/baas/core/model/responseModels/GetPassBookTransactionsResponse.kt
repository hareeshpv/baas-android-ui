package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails

class GetPassBookTransactionsResponse : ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("transactionList")
    var transactionList: ArrayList<GetPassBookTransactionDetails> = arrayListOf()

    @SerializedName("page")
    var page: String? = null
}