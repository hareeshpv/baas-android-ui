package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class OfferDetails {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("cta_url")
    var ctaUrl: String? = null

}