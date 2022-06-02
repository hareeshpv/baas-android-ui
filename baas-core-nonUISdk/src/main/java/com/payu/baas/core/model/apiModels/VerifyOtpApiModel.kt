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
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager

class VerifyOtpApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.VERIFY_OTP, sdkCallback
) {
    override fun getRelativeUrl(): String = "user/mobile/otp/verify"
    override fun getRequestMethod(): RequestMethod = RequestMethod.POST
    override fun getApiType(): ApiType = ApiType.PRE_LOGIN
    override fun getTokenType(): TokenType = TokenType.ACCESS_TOKEN
    override fun getResponseModel(): ApiResponse = VerifyOtpResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@VerifyOtpApiModel, response).getResponse()
                SessionManager.getInstance(context).accessToken =
                    (apiResponse as VerifyOtpResponse).userToken
                this@VerifyOtpApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@VerifyOtpApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}