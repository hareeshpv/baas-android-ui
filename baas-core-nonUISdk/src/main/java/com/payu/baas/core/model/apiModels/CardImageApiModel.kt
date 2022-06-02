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
import com.payu.baas.core.model.responseModels.CardImageResponse
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager

class CardImageApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.CARD_IMAGE, sdkCallback
) {
    override fun getRelativeUrl(): String = "card/image"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getRequestData(): String = ""
    override fun getResponseModel(): ApiResponse = CardImageResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@CardImageApiModel, response).getResponse()
                SessionManager.getInstance(context).cardImage =
                    (apiResponse as CardImageResponse).cardImageUrl.toString()
                this@CardImageApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@CardImageApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}