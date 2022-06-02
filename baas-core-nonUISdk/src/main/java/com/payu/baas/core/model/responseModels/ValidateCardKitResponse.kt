package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class ValidateCardKitResponse : ApiResponse() {
    @SerializedName("validated")
    var validated: Boolean? = null
}