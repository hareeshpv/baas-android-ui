package com.payu.baas.coreUI.model.entities.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails

class GetPassBookTransactionDetailHeader {

    @SerializedName("date")
    var date: String? = null

    @SerializedName("transaction_detail")
    var transactionDetails: ArrayList<GetPassBookTransactionDetails> = arrayListOf()

}