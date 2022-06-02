package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse


class LimitConfig: ApiResponse()  {
    @SerializedName("posChannel")
    var swipeChannel: CardLimitsChannel? = null

    @SerializedName("atmChannel")
    var atmChannel: CardLimitsChannel? = null

    @SerializedName("ecomChannel")
    var ecomChannel: CardLimitsChannel? = null

}
