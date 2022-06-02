package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.ServerTestingResponse
import com.payu.baas.core.util.BaaSConstants

class ServerTestingModel (context: Context, requestMap: HashMap<String, Any>, sdkCallback: SdkCallback
    ) : ApiModel(
    context, requestMap, ApiName.SERVER_CALL, sdkCallback
    ) {
        override fun getRelativeUrl(): String = """${BaaSConstants.BS_URL_INITIAL_SERVER_CALL}actuator/health"""
        override fun getRequestMethod(): RequestMethod = RequestMethod.GET
        override fun getApiType(): ApiType = ApiType.POST_LOGIN
        override fun getTokenType(): TokenType = TokenType.SERVER_TOKEN
        override fun getResponseModel(): ApiResponse = ServerTestingResponse()
    }
