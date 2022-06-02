package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class UserNameAadharXMLModel(){
    var formData: String? = null
    @SerializedName("aadhaarXMLData")
    var aadhaarXmlData: String? = null
    var panOCRData: String? = null
    @SerializedName("match")
    var matchScore: Double? = null
    var matchMeta: String? = null
//    var passportOCRData: String? = null
//    var voterOCRData: String? = null
//    var dlOCRData: String? = null
//    var chequeOCRData: String? = null
//    var electricityBillData: String? = null
//    var postpaidMobileBillData: String? = null
//    var pngBillData: String? = null
//    var telephoneBillData: String? = null
//    var leaveAndLicenseData: String? = null
//    var propertyTaxData: String? = null

}