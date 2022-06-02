package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.VerifyIFSCResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.storage.SessionManager


class IFSCApiModel (
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.IFSC_CODE, sdkCallback
) {
    override fun getRelativeUrl(): String = "user/beneficiary/ifsc"
    override fun getRequestMethod(): RequestMethod = RequestMethod.POST
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    /*override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).karzaKey?.let { put("x-karza-key", it) }
        }
    }*/

    override fun getResponseModel(): ApiResponse = VerifyIFSCResponse()

}