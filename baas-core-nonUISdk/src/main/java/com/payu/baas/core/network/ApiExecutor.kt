package com.payu.baas.core.network

import android.os.Handler
import android.os.Looper
import com.payu.baas.core.enums.ContentType
import com.payu.baas.core.enums.RequestMethod
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.model.ApiHandler
import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.util.BaaSConstants
import com.payu.baas.core.util.Utils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException


class ApiExecutor(val apiModel: ApiModel, val apiHandler: ApiHandler?) {

    private val certPinner = CertificatePinner.Builder()
        .add("baasdevtest.payu.in", "sha256/P0ofhtQPclFNzKB0I9pSAdfrMZtlIkHAg6NxQjDkd38=")
        .build()

    fun call() {

        if (Utils.isInternetAvailable(apiModel.context)) {

            val client = OkHttpClient().newBuilder().certificatePinner(certPinner)
                .connectTimeout(120000, TimeUnit.MILLISECONDS)
                .readTimeout(120000, TimeUnit.MILLISECONDS)
                .writeTimeout(120000, TimeUnit.MILLISECONDS).build()

            val requestBody: RequestBody
            if (apiModel.getContentType() == ContentType.APPLICATION_JSON) {
                requestBody = apiModel.getRequestData()
                    .toRequestBody(apiModel.getContentType().getValue().toMediaTypeOrNull())
            } else {
                var file = File(apiModel.requestMap[BaaSConstants.BS_KEY_LIVE_PHOTO].toString())
                requestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart(
                            BaaSConstants.BS_KEY_LIVE_PHOTO,
                            apiModel.requestMap[BaaSConstants.BS_KEY_KARZA_SELFIE_NAME].toString(),
                            file
                                .asRequestBody(
                                    "image/jpg".toMediaTypeOrNull()
                                )
                        ).build()
            }

            val requestBuilder: Request.Builder =
                when {
                    apiModel.getTokenType() == TokenType.KARZA_TOKEN -> {
                        Request.Builder()
                            .url(apiModel.getRelativeUrl())
                    }
                    apiModel.getTokenType() == TokenType.SERVER_TOKEN -> {
                        Request.Builder().url(apiModel.getRelativeUrl())
                    }
                    else -> {
                        Request.Builder().url(Utils.getAbsoluteUrl(apiModel.getRelativeUrl()))
                    }
                }


            if (apiModel.getRequestMethod() != RequestMethod.GET)
                requestBuilder.method(apiModel.getRequestMethod().name, requestBody)

            val header = apiModel.getHeader()
            if (!header.isNullOrEmpty())
                header.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val request = requestBuilder.build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Handler(Looper.getMainLooper()).post {
                        var message: String? = e.message
                        if (e is SSLException) {
                            message = BaaSConstants.SOMETHING_WENT_WRONG_ERROR_MESSAGE
                        }
                        apiHandler?.handleErrorResponse(message)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    Handler(Looper.getMainLooper()).post {
                        if (response != null && response.code != null && response.body != null)
                            apiHandler?.handleResponse(response.code, response.body?.string())
                    }
                }
            })

        } else {
            apiHandler?.handleResponse(
                BaaSConstants.NO_INTERNET_ERROR_CODE,
                BaaSConstants.NO_INTERNET_ERROR_MESSAGE
            )
        }


    }


}