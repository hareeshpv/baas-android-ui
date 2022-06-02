package com.payu.baas.coreUI.view.callback

import com.payu.baas.core.model.ErrorResponse

interface NetworkResponseHandler {
    fun onSuccess(response: String)
    fun onError(errorResponse: ErrorResponse)
}