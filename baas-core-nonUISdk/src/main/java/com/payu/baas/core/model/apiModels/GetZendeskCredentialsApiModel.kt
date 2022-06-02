package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ResponseModelCreator
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetClientTokenResponse
import com.payu.baas.core.model.responseModels.ZendeskCredentialsResponse
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager

class GetZendeskCredentialsApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.GET_ADDRESS, sdkCallback
) {
    override fun getRelativeUrl(): String = "zd/details"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getResponseModel(): ApiResponse = ZendeskCredentialsResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@GetZendeskCredentialsApiModel, response).getResponse()
                SessionManager.getInstance(context).zdUrl =
                    (apiResponse as ZendeskCredentialsResponse).url
                SessionManager.getInstance(context).zdAppId =
                    (apiResponse as ZendeskCredentialsResponse).appId
                SessionManager.getInstance(context).zdClientId =
                    (apiResponse as ZendeskCredentialsResponse).clientId
                this@GetZendeskCredentialsApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@GetZendeskCredentialsApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}