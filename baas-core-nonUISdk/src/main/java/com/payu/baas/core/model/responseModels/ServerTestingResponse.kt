package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class ServerTestingResponse : ApiResponse() {
    @SerializedName("status")
    var status: String? = null



}