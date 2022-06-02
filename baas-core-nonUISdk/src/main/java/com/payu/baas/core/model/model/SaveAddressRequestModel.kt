package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class SaveAddressRequestModel: ApiResponse() {
    @SerializedName("pan")
    var panNumber: String? = null

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

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("addressLine1")
    var addressLine1: String? = null

    @SerializedName("addressLine2")
    var addressLine2: String? = null

    @SerializedName("city")
    var city: String? = null
    @SerializedName("state")
    var state: String? = null

    @SerializedName("employeeId")
    var employeeId: String? = null
}
