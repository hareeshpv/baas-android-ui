package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class VerifyEmployeeResponse() : ApiResponse() {
    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("user")
    var userName: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("countryCode")
    var countryCode: String? = null

    @SerializedName("mobileNumber")
    var mobileNumber: String? = null

    @SerializedName("addressLine1")
    var addressLine1: String? = null

    @SerializedName("addressLine2")
    var addressLine2: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("stateId")
    var stateId: String? = null

    @SerializedName("employeeId")
    var employeeId: String? = null

    @SerializedName("pan")
    var pan: String? = null

    @SerializedName("brandId")
    var brandId: String? = null

    @SerializedName("verification_status")
    var verificationStatus: String? = null


}