package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.PanValidateResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants

class PanValidateApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.PAN_VALIDATE, sdkCallback
) {
    override fun getRelativeUrl(): String = "user/pan/validate"
    override fun getRequestMethod(): RequestMethod = RequestMethod.POST
    override fun getApiType(): ApiType = ApiType.PRE_LOGIN
    override fun getTokenType(): TokenType = TokenType.NONE

    override fun getAdditionalHeader(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(context).deviceBindingId?.let {
                put(
                    BaaSConstants.BS_KEY_DEVICE_BINDING_ID,
                    it
                )
            }
        }
    }

    override fun getResponseModel(): ApiResponse = PanValidateResponse()

}