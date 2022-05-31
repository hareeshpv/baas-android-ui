package com.payu.baas.core.util

import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.responseModels.ApiResponse


interface ApiHelperUI {

    fun onSuccess(apiResponse: ApiResponse)

    fun onError(errorResponse: ErrorResponse)

}