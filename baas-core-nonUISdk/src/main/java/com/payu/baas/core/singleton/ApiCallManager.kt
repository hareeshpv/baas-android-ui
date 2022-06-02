package com.payu.baas.core.singleton

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.network.ApiExecutor
import com.payu.baas.core.model.ApiHandler
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.model.apiModels.ApiModelFactory
import com.payu.baas.core.model.apiModels.GetClientTokenApiModel
import com.payu.baas.core.model.apiModels.SendOtpApiModel
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.storage.SessionManager

class ApiCallManager(val context: Context) {
    internal fun executeApi(
        apiName: ApiName,
        requestData: HashMap<String, Any>,
        sdkCallback: SdkCallback
    ) {
        val apiModel = ApiModelFactory(context, apiName, requestData, sdkCallback).getApiModel()
        when (apiModel.getApiType()) {
            ApiType.PRE_LOGIN -> {
                if (SessionManager.getInstance(context).accessToken != null)
                    ApiExecutor(apiModel, ApiHandler(apiModel)).call()
                else
                    fetchClientToken(SendOtpApiModel(context, requestData, sdkCallback))
            }
            else -> ApiExecutor(apiModel, ApiHandler(apiModel)).call()
        }
    }

    private fun fetchClientToken(apiModel: ApiModel) {
        val clientTokenApiModel =
            GetClientTokenApiModel(context, apiModel.requestMap, object : SdkCallback {
                override fun onSuccess(apiResponse: ApiResponse) {
                    ApiExecutor(apiModel, ApiHandler(apiModel)).call()
                }

                override fun onError(errorResponse: ErrorResponse) {
                    apiModel.sdkCallback.onError(errorResponse)
                }
            })
        ApiExecutor(clientTokenApiModel, ApiHandler(clientTokenApiModel)).call()
    }
}