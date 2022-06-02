package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetUserDetailsResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants

class GetUserDetailsApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(context, requestMap, ApiName.GET_USER_DETAILS, sdkCallback) {
    override fun getRelativeUrl(): String = "user/details"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.DEVICE_BINDING_ID
    override fun getRequestData(): String = ""
    override fun getResponseModel(): ApiResponse = GetUserDetailsResponse()

    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).accessToken?.let {
                put(BaaSConstants.BS_KEY_ACCESS_TOKEN, it)
            }
        }
    }
}