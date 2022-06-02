package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class KycLocationRequestModel {
    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null
    @SerializedName("ipAddress")
    var ipAddress: String? = null
    var state: String? = null
    var countrys: String? = null
}