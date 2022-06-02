package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetPassBookTransactionDetailHeader {

    @SerializedName("date")
    var date: String? = null

    @SerializedName("total_income")
    var totalIncome: String? = 0.0.toString()

    @SerializedName("total_expense")
    var totalExpense: String? = 0.0.toString()

    @SerializedName("transaction_detail")
    var transactionDetails: ArrayList<GetPassBookTransactionDetails> = arrayListOf()

}