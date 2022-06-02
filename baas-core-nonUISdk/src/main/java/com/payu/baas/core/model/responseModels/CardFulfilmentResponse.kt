package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.CardFulfilmentModel

class CardFulfilmentResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null
    @SerializedName("status")
    var status: String? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("cardFulfilment")
    var cardFulfilmentList: List<CardFulfilmentModel> = arrayListOf()


}
