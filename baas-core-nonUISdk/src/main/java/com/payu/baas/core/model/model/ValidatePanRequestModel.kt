package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class ValidatePanRequestModel {
    @SerializedName("panNumber")
    var panNumber: String? = null
}