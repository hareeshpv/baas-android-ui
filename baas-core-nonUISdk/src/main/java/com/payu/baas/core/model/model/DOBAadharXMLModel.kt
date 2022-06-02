package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class DOBAadharXMLModel() {
    var formData: Int? = null

    @SerializedName("aadhaarXMLData")
    var dob: String? = null
    var passportOCRData: String? = null
    var panOCRData: String? = null
    var voterOCRData: String? = null
    var dlOCRData: String? = null
    var postpaidMobileBillData: String? = null
    var match: String? = null
    var matchMeta: String? = null
}