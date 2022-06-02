package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName
import com.payu.baas.core.model.responseModels.ApiResponse

class TransactionLimitModel()  {
    var swipePerTransaction: String? = null
    var atmPerTransaction: String? = null
    var onlinePerTransaction: String? = null
    var swipeDaily: String? = null
    var atmDaily: String? = null
    var onlineDaily: String? = null

}