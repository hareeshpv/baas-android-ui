package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetTransactionModeResponse

class GetTransactionModeApiModel(
    context: Context, requestMap: HashMap<String, Any>, sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.GET_TRANSACTION_MODE, sdkCallback
) {
    override fun getRelativeUrl(): String = "card/transaction/modes"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getRequestData(): String = ""
    override fun getResponseModel(): ApiResponse = GetTransactionModeResponse()
}
