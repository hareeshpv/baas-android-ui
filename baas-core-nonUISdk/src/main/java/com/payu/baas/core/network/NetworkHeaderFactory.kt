package com.payu.baas.core.network

import com.payu.baas.core.enums.ContentType
import com.payu.baas.core.enums.TokenType
import com.payu.baas.core.model.apiModels.ApiModel
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstants

class NetworkHeaderFactory(val apiModel: ApiModel) {
    fun getHeader(): NetworkHeader {
        return when (apiModel.getTokenType()) {
            TokenType.DEVICE_BINDING_ID -> getNetworkHeaderWithDeviceBindingId()
            TokenType.ACCESS_TOKEN -> getNetworkHeaderWithAccessToken()
            TokenType.KARZA_TOKEN -> getNetworkHeaderWithKarzaToken()
            else -> getDefaultNetworkHeader()
        }
    }

    private fun getDefaultNetworkHeader(): NetworkHeader {
        return NetworkHeader().apply {
            put(BaaSConstants.BS_KEY_CONTENT_TYPE, ContentType.APPLICATION_JSON.getValue())
//            put(BaaSConstants.BS_KEY_BRAND_TOKEN, BaaSConstants.BS_VALUE_BRAND_TOKEN)
            SessionManager.getInstance(apiModel.context).brandToken?.let {
//                Logger.getLogger("brand token: ", it)
                put(BaaSConstants.BS_KEY_BRAND_TOKEN, it)
            }

            if (!apiModel.getAdditionalHeader().isNullOrEmpty())
                apiModel.getAdditionalHeader().forEach { (key, value) -> put(key, value) }
        }
    }

    private fun getNetworkHeaderWithDeviceBindingId(): NetworkHeader {
        return NetworkHeader().apply {
            put(BaaSConstants.BS_KEY_CONTENT_TYPE, ContentType.APPLICATION_JSON.getValue())
//            var dbId = SessionManager.getInstance(apiModel.context).deviceBindingId
//            if (dbId.isNullOrEmpty())
//                SessionManager.getInstance(apiModel.context).deviceBindingId =
//                    UUID.randomUUID().toString()
            SessionManager.getInstance(apiModel.context).deviceBindingId?.let {
//                Logger.getLogger("device binding id: ", it)
                put(
                    BaaSConstants.BS_KEY_DEVICE_BINDING_ID,
                    it
                )
            }
            if (!apiModel.getAdditionalHeader().isNullOrEmpty())
                apiModel.getAdditionalHeader().forEach { (key, value) -> put(key, value) }
        }
    }


    private fun getNetworkHeaderWithAccessToken(): NetworkHeader {
        return NetworkHeader().apply {
            SessionManager.getInstance(apiModel.context).accessToken?.let {
                put(
                    BaaSConstants.BS_KEY_ACCESS_TOKEN,
                    it
                )
            }
//            var dbId = SessionManager.getInstance(apiModel.context).deviceBindingId
//            if (dbId.isNullOrEmpty())
//                SessionManager.getInstance(apiModel.context).deviceBindingId =
//                    UUID.randomUUID().toString()
            SessionManager.getInstance(apiModel.context).deviceBindingId?.let {
//                Logger.getLogger("device binding id: ", it)
                put(
                    BaaSConstants.BS_KEY_DEVICE_BINDING_ID,
                    it
                )
            }
            SessionManager.getInstance(apiModel.context).brandToken?.let {
//                Logger.getLogger("brand token: ", it)
                put(
                    BaaSConstants.BS_KEY_BRAND_TOKEN,
                    it
                )
            }
//            put(BaaSConstants.BS_KEY_BRAND_TOKEN, BaaSConstants.BS_VALUE_BRAND_TOKEN)

            put(BaaSConstants.BS_KEY_CONTENT_TYPE, apiModel.getContentType().getValue())

            if (!apiModel.getAdditionalHeader().isNullOrEmpty())
                apiModel.getAdditionalHeader().forEach { (key, value) -> put(key, value) }
        }
    }

    private fun getNetworkHeaderWithKarzaToken(): NetworkHeader {
        return NetworkHeader().apply {
//            SessionManager.getInstance(apiModel.context).karzaToken?.let {
//                put(
//                    BaaSConstants.BS_KEY_ACCESS_TOKEN,
//                    it
//                )
//            }

            put(BaaSConstants.BS_KEY_CONTENT_TYPE, apiModel.getContentType().getValue())

            if (!apiModel.getAdditionalHeader().isNullOrEmpty())
                apiModel.getAdditionalHeader().forEach { (key, value) -> put(key, value) }
        }
    }
}