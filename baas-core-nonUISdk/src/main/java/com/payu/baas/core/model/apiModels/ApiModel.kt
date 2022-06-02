package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.enums.ApiType
import com.payu.baas.core.enums.ContentType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.RequestCreator
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.network.NetworkHeader
import com.payu.baas.core.network.NetworkHeaderFactory
import com.payu.baas.core.network.NetworkResponseHandler
import com.payu.baas.core.network.NetworkResponseHandlerFactory

abstract class ApiModel(
    val context: Context,
    val requestMap: HashMap<String, Any>,
    val apiName: ApiName,
    val sdkCallback: SdkCallback
) {
    abstract fun getRelativeUrl(): String
    abstract fun getRequestMethod(): RequestMethod
    abstract fun getApiType(): ApiType
    abstract fun getTokenType(): TokenType
    abstract fun getResponseModel(): ApiResponse
    open fun getContentType(): ContentType =  ContentType.APPLICATION_JSON
    fun getHeader(): NetworkHeader = NetworkHeaderFactory(this).getHeader()

    open fun getAdditionalHeader(): NetworkHeader = NetworkHeader()
    open fun getRequestData(): String = RequestCreator(getContentType(), requestMap).createRequest()
    open fun getResponseHandler(): NetworkResponseHandler =
        NetworkResponseHandlerFactory(this).getResponseHandler()
}