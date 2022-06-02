package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class GenderAadharXMLModel() {
    var formData: Int? = null

    @SerializedName("aadhaarXMLData")
    var gender: String? = null
    var passportOCRData: String? = null
    var panOCRData: String? = null
    var voterOCRData: String? = null
    var match: String? = null
    var matchMeta: String? = null
}