package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.BeneficiaryModel

class CreateBeneficiaryResponse :
    ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("msg")
    var message: String? = null

    @SerializedName("userBeneficiary")
    var userBeneficiary: BeneficiaryModel? = null
}