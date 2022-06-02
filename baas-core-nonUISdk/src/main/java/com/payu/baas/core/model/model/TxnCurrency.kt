package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class TxnCurrency : ApiResponse() {
    @SerializedName("alpha_code")
    var alphaCode: String? = null

    @SerializedName("numeric_code")
    var numericCode: Int? = null

}
