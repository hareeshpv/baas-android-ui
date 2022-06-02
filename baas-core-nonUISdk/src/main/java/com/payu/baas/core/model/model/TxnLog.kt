package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class TxnLog :  ApiResponse() {
    @SerializedName("txn_currency")
    var txnCurrency: TxnCurrency? = null

    @SerializedName("txn_date")
    var txnDate: String? = null

    @SerializedName("txn_indicator")
    var txnIndicator: String? = null

    @SerializedName("txn_type")
    var txnType: String? = null

    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("remarks")
    var remarks: String? = null

    @SerializedName("txn_amount")
    var txnAmount: Int? = null

    @SerializedName("txn_log_id")
    var txnLogId: String? = null

    @SerializedName("entry_id")
    var entryId: String? = null

    @SerializedName("rrn")
    var rrn: String? = null

    @SerializedName("txn_code")
    var txnCode: String? = null

}
