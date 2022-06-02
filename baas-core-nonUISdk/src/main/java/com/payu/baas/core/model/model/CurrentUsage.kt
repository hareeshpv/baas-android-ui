package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class CurrentUsage: ApiResponse() {

    @SerializedName("advanceUsed")
    var advanceUsed: String? = null

    @SerializedName("feesCharged")
    var feesCharged: String? = null

    var displayStartDate: String? = null
    var displayEndDate: String? = null

    var cycleStartDate: String? = null
    var cycleEndDate: String? = null
    @SerializedName("paidBack")
    var paidBack: String? = null

    @SerializedName("dueDate")
    var dueDate: String? = null

    @SerializedName("latePaymentFees")
    var latePaymentFees: String? = null
    var overdueBalance: String? = null
    var earnings: String? = null
    var providerId: Int? = null
    var creditEarningRatio: Int? = null
}
