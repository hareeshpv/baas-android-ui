package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class HelpResponse : ApiResponse() {
    @SerializedName("value")
    var value: String? = null
}