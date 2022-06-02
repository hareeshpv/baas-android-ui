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
import com.payu.baas.core.model.responseModels.LoginResponse
import com.payu.baas.core.model.responseModels.TipResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants

class GetTipsApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.GET_TIPS, sdkCallback
) {
    override fun getRelativeUrl(): String = "benefits/tips"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.DEVICE_BINDING_ID
    override fun getResponseModel(): ApiResponse = TipResponse()

    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).accessToken?.let {
                put(BaaSConstants.BS_KEY_ACCESS_TOKEN, it)
            }
        }
    }


}