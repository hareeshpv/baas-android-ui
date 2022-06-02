package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.model.*
import org.json.JSONArray

class KarzaVerificationResponse  : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("requestId")
    var requestId: String? = null
    @SerializedName("aadhaarNumber")
    var maskedAadhaar: String? = null
    @SerializedName("applicationId")
    var applicationId: String? = null
    @SerializedName("createdTimeStamp")
    var createdTimeStamp: String? = null
    @SerializedName("panVerified")
    var panVerified: Boolean? = false
    @SerializedName("isAadhaarVerified")
    var isAadhaarVerified: Boolean? = false
    var livenessVerified: Boolean? = false
    var xmlDateVerified: Boolean? = false
    var currentAddress: AddressAadharXMLModel? = null
    var permanentAddress: AddressAadharXMLModel? = null
    var faceAadhaarXML: FaceAadharXMLModel? = null
    @SerializedName("dob")
    var dobAadharXML: DOBAadharXMLModel? = null
    @SerializedName("gender")
    var genderAadharXML: GenderAadharXMLModel? = null
    @SerializedName("name")
    var userNameDate: UserNameAadharXMLModel? = null
}