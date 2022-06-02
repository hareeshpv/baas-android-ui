package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class KarzaNewCustomerResponse : ApiResponse(){
    var statusCode = 0
    var requestId: String? = null

    @SerializedName("result")
    var results: KarzaCustomerResults? = null
    var error: String? = null
    var status: Int? = null
}