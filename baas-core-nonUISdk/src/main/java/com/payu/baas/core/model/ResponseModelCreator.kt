package com.payu.baas.core.model

import com.google.gson.Gson
import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.model.responseModels.ApiResponse

class ResponseModelCreator(val apiModel: ApiModel, val response: String) {
    internal fun getResponse(): ApiResponse {
        val gson= Gson()
        val responseModel = gson.fromJson(response, apiModel.getResponseModel()::class.java)
        responseModel.rawResponse = response
        return responseModel
    }
}