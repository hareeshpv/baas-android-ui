package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class EmploymentVerificationRequestModel {
    @SerializedName("pan")
    var pan: String? = null
    @SerializedName("brandEmpId")
    var brandEmpId: String? = null
    @SerializedName("mobile")
    var mobile: String? = null
}