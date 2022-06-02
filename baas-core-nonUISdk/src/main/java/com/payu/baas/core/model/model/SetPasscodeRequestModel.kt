package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class SetPasscodeRequestModel {
    var newPasscode: String? = null
    var oldPasscode: String? = null
}