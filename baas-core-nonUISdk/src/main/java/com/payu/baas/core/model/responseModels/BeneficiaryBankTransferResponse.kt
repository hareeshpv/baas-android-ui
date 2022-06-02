package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.BeneficiaryBankTransferLogModel
import com.payu.baas.core.model.model.TargetAccountBalance
import com.payu.baas.core.model.model.TxnLog

class BeneficiaryBankTransferResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("txn_logs")
    var transactionLogsList: ArrayList<BeneficiaryBankTransferLogModel>? = null

}