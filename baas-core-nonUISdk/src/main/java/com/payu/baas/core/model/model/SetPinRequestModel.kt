package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class SetPinRequestModel {
    @SerializedName("redirect_link")
    var redirectLink: String? = null
}