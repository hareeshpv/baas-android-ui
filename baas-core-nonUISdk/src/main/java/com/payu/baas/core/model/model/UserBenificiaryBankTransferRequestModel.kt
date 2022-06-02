package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class UserBenificiaryBankTransferRequestModel {
    @SerializedName("currency")
    var currency: String? = null
    @SerializedName("payment_type")
    var payment_type: String? = null
    @SerializedName("remarks")
    var remarks: String? = null
    @SerializedName("account_id")
    var account_id: String? = null
    @SerializedName("amount")
    var amount: String? = null
    @SerializedName("creditor_details")
    var creditorDetails: CreditorDetails? = null

}