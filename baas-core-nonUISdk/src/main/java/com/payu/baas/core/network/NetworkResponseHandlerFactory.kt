package com.payu.baas.core.network

import com.google.gson.Gson
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.model.responseModels.ApiResponse

class NetworkResponseHandlerFactory(val apiModel: ApiModel) {
    fun getResponseHandler(): NetworkResponseHandler {
        return defaultResponseHandler()
    }

    private fun defaultResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val gson = Gson()
                val responseModel = gson.fromJson(response, apiModel.getResponseModel()::class.java)
                if (responseModel != null) {
                    responseModel.rawResponse = response
                    apiModel.sdkCallback.onSuccess(responseModel)
                }else{
                    apiModel.sdkCallback.onSuccess(ApiResponse())
                }
            }

            override fun onError(errorResponse: ErrorResponse) {
                apiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}