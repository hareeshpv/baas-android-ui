package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class BeneficiaryModel() : ApiResponse() {

    @SerializedName("beneficiaryId")
    var beneficiaryId: String? = null

    @SerializedName("beneficiaryName")
    var beneficiaryName: String? = null

    @SerializedName("ifsc")
    var ifsc: String? = null

    @SerializedName("last4Digits")
    var last4Digits: String? = null

    @SerializedName("addedOn")
    var addedOn: String? = null

    @SerializedName("bankName")
    var bankName: String? = null

    @SerializedName("icon")
    var icon: String? = null

    var selected: Boolean = false

}