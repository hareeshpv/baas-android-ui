package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class TargetAccountBalance : ApiResponse(){
    @SerializedName("balance")
    var balance: Int? = null

    @SerializedName("currency")
    var currency: String? = null

}
