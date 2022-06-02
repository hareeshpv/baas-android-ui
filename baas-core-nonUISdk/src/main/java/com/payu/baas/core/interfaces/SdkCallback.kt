package com.payu.baas.core.interfaces

import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.responseModels.ApiResponse

interface SdkCallback {
    fun onSuccess(apiResponse: ApiResponse)
    fun onError(errorResponse: ErrorResponse)
}