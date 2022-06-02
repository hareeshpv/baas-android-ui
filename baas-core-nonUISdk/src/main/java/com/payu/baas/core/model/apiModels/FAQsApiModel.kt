package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.FAQsResponse
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants


class FAQsApiModel (
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.FAQS, sdkCallback
) {
    override fun getRelativeUrl(): String = "faqs/url"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.PRE_LOGIN
    override fun getTokenType(): TokenType = TokenType.DEVICE_BINDING_ID
    override fun getResponseModel(): ApiResponse = FAQsResponse()
    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).accessToken?.let {
                put(
                    BaaSConstants.BS_KEY_ACCESS_TOKEN,
                    it
                )
            }
        }
    }
}