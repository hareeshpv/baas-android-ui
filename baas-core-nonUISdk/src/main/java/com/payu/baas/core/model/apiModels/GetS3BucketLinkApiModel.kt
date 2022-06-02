package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ResponseModelCreator
import com.payu.baas.core.model.responseModels.RatesChargesResponse
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetClientTokenResponse
import com.payu.baas.core.model.responseModels.GetS3BucketLinkResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants


class GetS3BucketLinkApiModel(
    context: Context,
    requestMap: HashMap<String, Any>,
    sdkCallback: SdkCallback
) : ApiModel(
    context, requestMap, ApiName.GET_S3_BUCKET_LINK, sdkCallback
) {
    override fun getRelativeUrl(): String = "faqs/images/url"
    override fun getRequestMethod(): RequestMethod = RequestMethod.GET
    override fun getApiType(): ApiType = ApiType.PRE_LOGIN
    override fun getTokenType(): TokenType = TokenType.NONE
    override fun getRequestData(): String = ""
    override fun getResponseModel(): ApiResponse = GetS3BucketLinkResponse()
    override fun getResponseHandler(): NetworkResponseHandler {
        return object : NetworkResponseHandler {
            override fun onSuccess(response: String) {
                val apiResponse =
                    ResponseModelCreator(this@GetS3BucketLinkApiModel, response).getResponse()
                SessionManager.getInstance(context).s3BucketUrl =
                    (apiResponse as GetS3BucketLinkResponse).value
                this@GetS3BucketLinkApiModel.sdkCallback.onSuccess(apiResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                this@GetS3BucketLinkApiModel.sdkCallback.onError(errorResponse)
            }

        }
    }
}