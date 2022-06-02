package com.payu.baas.core.network

import com.payu.baas.core.model.ErrorResponse

interface NetworkResponseHandler {
    fun onSuccess(response: String)
    fun onError(errorResponse: ErrorResponse)
}