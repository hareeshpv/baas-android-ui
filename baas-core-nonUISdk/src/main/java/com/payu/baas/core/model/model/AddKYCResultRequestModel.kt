package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class AddKYCResultRequestModel {
    var requestId: String? = null
    var maskedAadhaar: String? = null
    var applicationId: String? = null
    var createTimeStamp: String? = null
    @SerializedName("panVerified")
    var panVerified: Boolean? = false
    @SerializedName("isAadhaarVerified")
    var isAadhaarVerified: Boolean? = false
    var livenessVerified: Boolean? = false
    var xmlDateVerified: Boolean? = false
    var currentAddress: String? = null
    var permanentAddress: String? = null
    var dob: String? = null
    var gender: String? = null
    @SerializedName("name")
    var userNameDate: UserNameAadharXMLModel? = null
    @SerializedName("faceAadhaarXml")
    var faceAadhaarXml: FaceAadharXMLModel? = null
}