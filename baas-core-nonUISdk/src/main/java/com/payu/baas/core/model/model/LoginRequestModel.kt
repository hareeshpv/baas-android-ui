package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class LoginRequestModel {
    @SerializedName("username")
    var username: String? = null
    @SerializedName("password")
    var passcode: String? = null
}