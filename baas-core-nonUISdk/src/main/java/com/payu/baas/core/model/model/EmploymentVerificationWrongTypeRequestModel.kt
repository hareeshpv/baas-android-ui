package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class EmploymentVerificationWrongTypeRequestModel {
    @SerializedName("pan")
    var pan: String? = null
    @SerializedName("brandEmpId")
    var brandEmpId: Int? = null
    @SerializedName("mobile")
    var mobile: String? = null
}