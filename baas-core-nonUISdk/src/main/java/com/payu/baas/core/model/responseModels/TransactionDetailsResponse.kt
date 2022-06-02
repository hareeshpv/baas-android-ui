package com.payu.baas.core.model.responseModels

import com.google.gson.annotations.SerializedName

class TransactionDetailsResponse : ApiResponse() {

    @SerializedName("userMessage")
    var userMessage: String? = null

    @SerializedName("systemMessage")
    var systemMessage: String? = null

    @SerializedName("code")
    var code: String? = null


    val amount: String? = null
    val bottomIcon: String? = null
    val bottomSalutation: String? = null
    val bottomTitle: String? = null
    val dateTime: String? = null
    val extraNotes: String? = null
    val narration: String? = null
    val notes: String? = null
    val refId: String? = null
    val statusIcon: String? = null
    val topIcon: String? = null
    val topSalutation: String? = null
    val topTitle: String? = null
    val transactionId: String? = null
}