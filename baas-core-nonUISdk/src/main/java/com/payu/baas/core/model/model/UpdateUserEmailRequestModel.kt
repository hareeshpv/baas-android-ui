package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class UpdateUserEmailRequestModel {
    @SerializedName("emailId")
    var emailId: String? = null
}