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
import com.payu.baas.core.util.BaaSConstants

class KarzaNewCustomerApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.KARZA_ADD_NEW_CUSTOMER, sdkCallback
) {
    override fun getRelativeUrl(): String = "https://app.karza.in/test/videokyc/api/v2/okyc-customers"
    override fun getRequestMethod(): RequestMethod = RequestMethod.POST
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
            put("Referer","https://app.karza.in/test/okyc/add-customer")
        }
    }
    override fun getResponseModel(): ApiResponse = KarzaNewCustomerResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@KarzaNewCustomerApiModel, response).getResponse()
                var  transactionId =
                    SessionManager.getInstance(context).karzaTransactionId
//                if(transactionId.isNullOrEmpty()){
                    SessionManager.getInstance(context).karzaTransactionId =
                        (apiResponse as KarzaNewCustomerResponse).results!!.data!!.transactionId
//                }
              /*  Logger.getLogger(
                    "karza_transactionId",
                    (apiResponse as KarzaNewCustomerResponse).results!!.data!!.transactionId!!
                )*/
                this@KarzaNewCustomerApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@KarzaNewCustomerApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}