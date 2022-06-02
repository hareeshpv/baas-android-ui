package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class AdvanceUsageHistory : ApiResponse() {

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


    /* older values
    @SerializedName("overdueBalance")
    var overdueBalance: String? = "0"

    @SerializedName("advanceUsed")
    var advanceUsed: String? = null

    @SerializedName("feesCharged")
    var feesCharged: String? = null

    @SerializedName("latePaymentFees")
    var latePaymentFees: String? = null

    @SerializedName("paidBack")
    var paidBack: String? = null

    @SerializedName("startDateTime")
    var startDateTime: String? = null

    @SerializedName("endDateTime")
    var endDateTime: String? = null

    @SerializedName("dueDate")
    var dueDate: String? = null
    var earnings: String? = null
    var providerId: Int? = null

     */
}
