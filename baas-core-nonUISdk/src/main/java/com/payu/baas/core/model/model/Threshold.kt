package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse


class Threshold : ApiResponse() {
    @SerializedName("amount_limit")
    var amountLimit: Int? = null

    @SerializedName("target_duration")
    var targetDuration: String? = null

    @SerializedName("usage_limit")
    var usageLimit: Int? = null
}
