package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse


class CardLimitsChannel: ApiResponse()  {
    @SerializedName("perTransaction")
    var perTransaction: Long? = null

    @SerializedName("daily")
    var daily: Long? = null

}
