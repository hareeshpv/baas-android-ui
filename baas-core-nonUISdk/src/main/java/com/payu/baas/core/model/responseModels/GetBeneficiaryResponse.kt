package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.BeneficiaryModel

class GetBeneficiaryResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("size")
    var size: Int? = null

    @SerializedName("page")
    var page: Int? = null

    @SerializedName("sort")
    var sort: String? = null


    @SerializedName("result")
    var userBeneficiaryList: ArrayList<BeneficiaryModel>? = null
}