package com.payu.baas.coreUI.util

import com.payu.baas.core.BaaSSDK
import com.payu.baas.coreUI.app.BaasUIApp
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.model.ApiDetails
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse

class ApiCall {

     companion object{
         fun callAPI(apiName: ApiName, apiParams: ApiParams, apiHelper: ApiHelperUI) {
             com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                 BaaSSDK.callApi(
                     it,
                     ApiDetails(apiName, apiParams),
                     object : SdkCallback {
                         override fun onSuccess(apiResponse: ApiResponse) {
                             apiHelper.onSuccess(apiResponse)
                         }


                         override fun onError(errorResponse: ErrorResponse) {
                             apiHelper.onError(errorResponse)
                         }
                     })
             }

         }
     }



}