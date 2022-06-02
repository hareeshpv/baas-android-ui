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
import com.payu.baas.core.model.responseModels.KarzaUserTokenResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants

class KarzaCustomerTokenApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.KARZA_GENERATE_CUSTOMER_TOKEN, sdkCallback
) {
    override fun getRelativeUrl(): String = "https://app.karza.in/test/videokyc/api/v2/generate-usertoken/" +
            "${requestMap[BaaSConstants.BS_KEY_KARZA_TRANSACTION_ID]}"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.KARZA_TOKEN
    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).karzaToken?.let {
                put(
                    BaaSConstants.BS_KEY_KARZA_TOKEN,
                    it
                )
            }
        }
    }
    override fun getResponseModel(): ApiResponse = KarzaUserTokenResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@KarzaCustomerTokenApiModel, response).getResponse()
            SessionManager.getInstance(context).karzaUserToken =
                    (apiResponse as KarzaUserTokenResponse).result!!.data!!.userToken
                this@KarzaCustomerTokenApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@KarzaCustomerTokenApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}