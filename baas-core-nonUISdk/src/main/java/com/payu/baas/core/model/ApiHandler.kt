package com.payu.baas.core.model

import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.util.BaaSConstants

class ApiHandler(val apiModel: ApiModel) {

    fun handleResponse(responseCode: Int, response: String?) { if (responseCode != BaaSConstants.NO_INTERNET_ERROR_CODE && responseCode < 400) {
            response?.let { apiModel.getResponseHandler().onSuccess(response) }
        } else {
            //TODO send error response in onError callback method of Response Handler
            val errorResponse = ErrorResponse().apply {
                errorMessage = response?.let { it }
                errorCode = responseCode
            }
            response?.let { apiModel.getResponseHandler().onError(errorResponse) }
        }
    }

    fun handleErrorResponse(error: String?) {
        val errorResponse = ErrorResponse().apply {
//            errorMessage = apiModel.context.getString(R.string.baas_something_went_wrong)
            errorMessage = BaaSConstants.SOMETHING_WENT_WRONG_ERROR_MESSAGE
            error?.let { errorMessage = error }
        }
        apiModel.getResponseHandler().onError(errorResponse)
    }

}