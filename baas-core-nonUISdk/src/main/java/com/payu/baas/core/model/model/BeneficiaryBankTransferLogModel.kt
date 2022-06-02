package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.TargetAccountBalance
import com.payu.baas.core.model.model.TxnLog

class BeneficiaryBankTransferLogModel  {
    @SerializedName("txn_log_id")
    var txnLogId: String? = null

}