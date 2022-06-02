package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ResponseModelCreator
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager


class KarzaSessionApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.KARZA_TOKEN, sdkCallback
) {
    override fun getRelativeUrl(): String = "https://testapi.karza.in/v2/get-jwt"
    override fun getRequestMethod(): RequestMethod = RequestMethod.POST
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.KARZA_TOKEN
    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).karzaKey?.let { put("x-karza-key", it) }
        }
    }

    override fun getResponseModel(): ApiResponse = KarzaSessionResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@KarzaSessionApiModel, response).getResponse()
                if (apiResponse != null && (apiResponse as KarzaSessionResponse).result!=null) {
                    SessionManager.getInstance(context).karzaToken =
                        (apiResponse as KarzaSessionResponse)!!.result!!.token
                }
                this@KarzaSessionApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@KarzaSessionApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}