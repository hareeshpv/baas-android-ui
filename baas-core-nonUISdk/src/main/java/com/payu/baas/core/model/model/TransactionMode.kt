package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse


class TransactionMode : ApiResponse() {

    @SerializedName("allow")
    var allow: Boolean? = null

    @SerializedName("channel")
    var channel: String? = null

}
