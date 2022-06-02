package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class GetPassBookTransactionDetails {
    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("txnStatus")
    var txnStatus: String? = null

    @SerializedName("debitCreditIndicator")
    var debitCreditIndicator: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("icon_url")
    var iconUrl: String? = null

    @SerializedName("txnLogId")
    var txnLogId: String? = null

    @SerializedName("description")
    var label: String? = null

    @SerializedName("displayDate")
    var displayDate: String? = null

    @SerializedName("accountTypeIcon")
    var accountTypeIcon: String? = null

    @SerializedName("transactionTypeIcon")
    var transactionTypeIcon: String? = null
}