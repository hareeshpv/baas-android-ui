package com.payu.baas.core.model

import com.payu.baas.core.enums.ContentType
import okhttp3.FormBody
import okhttp3.MultipartBody
import org.json.JSONObject

class RequestCreator(val contentType: ContentType, val requestMap: HashMap<String, Any>) {
    internal fun createRequest(): String {
//        return when (contentType) {
//            ContentType.APPLICATION_JSON -> {
               return if (!requestMap.isNullOrEmpty()) {
                    val jsonObject = JSONObject()
                    requestMap.forEach { (key, value) -> jsonObject.put(key, value) }
                    jsonObject.toString()
                } else ""
//            }
//        }
    }
}