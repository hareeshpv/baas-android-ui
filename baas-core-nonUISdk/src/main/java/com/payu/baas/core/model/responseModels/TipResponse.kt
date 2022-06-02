package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.TipDetails

class TipResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("tipsList")
    var tipsList: ArrayList<TipDetails> = arrayListOf()
}