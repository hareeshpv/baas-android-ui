package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class CreateAndUpdateBenificiaryRequestModel {
    @SerializedName("beneficiaryName")
    var beneficiaryName: String? = null
    @SerializedName("accountNumber")
    var accountNumber: String? = null
    @SerializedName("ifsc")
    var ifsc: String? = null
    @SerializedName("verified")
    var verified: String? = null
    @SerializedName("status")
    var status: String? = null
}