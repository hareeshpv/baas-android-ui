package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class IFSCDetailsModel() {

    @SerializedName("city")
    var city: String? = null

    @SerializedName("office")
    var office: String? = null

    @SerializedName("ifsc")
    var ifsc: String? = null

    @SerializedName("district")
    var district: String? = null

    @SerializedName("micr")
    var micr: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("contact")
    var contact: String? = null

    @SerializedName("branch")
    var branch: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("bank")
    var bank: String? = null

    //No from API
    var icon: String? = null


}