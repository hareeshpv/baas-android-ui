package com.payu.baas.core.view.callback

import com.payu.baas.core.model.ErrorResponse

interface NetworkResponseHandler {
    fun onSuccess(response: String)
    fun onError(errorResponse: ErrorResponse)
}