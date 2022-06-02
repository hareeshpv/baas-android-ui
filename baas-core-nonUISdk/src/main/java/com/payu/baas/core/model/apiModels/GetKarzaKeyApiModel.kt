package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ResponseModelCreator
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetApplicationIdResultsResponse
import com.payu.baas.core.model.responseModels.GetKarzaKeyResponse
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager

class GetKarzaKeyApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.KARZA_KEY, sdkCallback
) {
    override fun getRelativeUrl(): String = "faqs/karza/api-key"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.PRE_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getResponseModel(): ApiResponse = GetKarzaKeyResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@GetKarzaKeyApiModel, response).getResponse()
                SessionManager.getInstance(context).karzaKey =
                    (apiResponse as GetKarzaKeyResponse).key
                this@GetKarzaKeyApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@GetKarzaKeyApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}