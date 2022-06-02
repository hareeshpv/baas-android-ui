package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.NotificationDetails
import com.payu.baas.core.model.model.OfferDetails


class OffersResponse : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("offerResponsesList")
    var offerList: ArrayList<OfferDetails> = arrayListOf()
}