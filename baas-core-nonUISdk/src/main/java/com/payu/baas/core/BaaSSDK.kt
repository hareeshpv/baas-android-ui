package com.payu.baas.core

import android.content.Context
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.model.ApiDetails
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.RequestDataGenerator
import com.payu.baas.core.model.SdkConfig
import com.payu.baas.core.singleton.ApiCallManager

object BaaSSDK {

    private var sdkConfig: SdkConfig = SdkConfig()

    @JvmStatic
    fun init(sdkConfig: SdkConfig) {
        this.sdkConfig = sdkConfig
    }
    @JvmStatic
    fun callApi(context: Context, apiDetails: ApiDetails, sdkCallback: SdkCallback) {
        val requestData = RequestDataGenerator(apiDetails).getRequestData()
        if (requestData.isValid) {
            ApiCallManager(context).executeApi(
                apiDetails.apiName,
                requestData.result!!,
                sdkCallback
            )
        } else {
            sdkCallback.onError(ErrorResponse(requestData.errorMessage, requestData.errorCode))
        }
    }
}