package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class CardDeliveryAddressModel()  {
    var pinCode: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var landmark: String? = null
    var city: String? = null
    var state: String? = null

}