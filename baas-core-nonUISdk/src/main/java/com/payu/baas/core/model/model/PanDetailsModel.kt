package com.payu.baas.core.model.entities.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class PanDetailsModel()  {
    var fullName: String? = null
    var panNumber: String? = null
    var dob: String? = null
    var employeeId: String? = null

}