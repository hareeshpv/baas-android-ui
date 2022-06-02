package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetPassBookTransactionsResponse
import com.payu.baas.core.util.BaaSConstants

class GetTransactionListApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(context, requestMap, ApiName.GET_TRANSACTION_LIST, sdkCallback) {

    override fun getRelativeUrl(): String = "transactions?startDate=" +
            "${requestMap[BaaSConstants.BS_KEY_START_DATE]}"+"&endDate=" +
    "${requestMap[BaaSConstants.BS_KEY_END_DATE]}"+"&accountType=" +
            "${requestMap[BaaSConstants.BS_KEY_ACCOUNT_TYPE]}"+"&debitIndicator=" +
            "${requestMap[BaaSConstants.BS_KEY_DEBIT_INDICATOR]}"+"&page=" +
            "${requestMap[BaaSConstants.BS_KEY_PAGE]}"

//    override fun getRelativeUrl(): String =
//        "transactions?startDate=${requestMap[BaaSConstants.BS_KEY_START_DATE]}&endDate=${requestMap[BaaSConstants.BS_KEY_END_DATE]}&accountType=${requestMap[BaaSConstants.BS_KEY_ACCOUNT_TYPE]}&debitIndicator=${requestMap[BaaSConstants.BS_KEY_DEBIT_INDICATOR]}"


    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.POST_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getRequestData(): String = ""
    override fun getResponseModel(): ApiResponse = GetPassBookTransactionsResponse()
}