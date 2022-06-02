package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class ZenDeskDetailsResponse : ApiResponse() {
    @SerializedName("zendeskUrl")
    var zendeskUrl: String? = null

    @SerializedName("appId")
    var appId: String? = null

    @SerializedName("clientId")
    var clientId: String? = null

}