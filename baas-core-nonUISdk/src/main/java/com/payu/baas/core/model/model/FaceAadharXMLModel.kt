package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class FaceAadharXMLModel() {

    @SerializedName("matchScore")
    var matchScore: Double? = 0.0
//    var agentStatus: String? = null

    @SerializedName("matchMeta")
    var matchMeta: String? = null
//    var aadhaarBase64: String? = null
}