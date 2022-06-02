package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class UpdateUserMobileRequestModel {
    @SerializedName("mobileNumber")
    var mobileNumber: String? = null
}