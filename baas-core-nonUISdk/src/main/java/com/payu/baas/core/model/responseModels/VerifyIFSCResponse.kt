package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.IFSCDetailsModel


class VerifyIFSCResponse: ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("status-code")
    var statusCode: String? = null

    @SerializedName("result")
    var IFSCDetails: IFSCDetailsModel? = null
}