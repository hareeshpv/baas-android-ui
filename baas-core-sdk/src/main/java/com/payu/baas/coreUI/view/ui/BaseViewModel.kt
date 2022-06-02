package com.payu.baas.coreUI.view.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.KYCLocationResponse
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.callback.ClickHandler
import com.payu.baas.coreUI.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.coreUI.view.ui.activity.mobileverification.MobileVerificationActivity
import kotlinx.coroutines.launch

 open class BaseViewModel(
    internal val baseCallBack: BaseCallback?,
    internal val clickHandler :ClickHandler?,
    internal val context: Context
) : ViewModel() {
    private val accessTokenResponseObs = MutableLiveData<Resource<Any>>()

    init {

    }

    fun getAccessTokenResponseObs(): LiveData<Resource<Any>> {
        return accessTokenResponseObs
    }

    open fun reGenerateAccessToken(isOnBoarding: Boolean) {
        if (isOnBoarding)
            sendOTP()
//            getAccessToken()
        else
            verifyPasscode()
    }
     open fun sendOTP() {
         baseCallBack?.callNextScreen(Intent(context, MobileVerificationActivity::class.java), null,true)
     }
    fun getAccessToken() {
        viewModelScope.launch {
            accessTokenResponseObs.postValue(Resource.loading(null))
            try {
                val apiParams = ApiParams()
                ApiCall.callAPI(

                    ApiName.GET_CLIENT_TOKEN,
                    apiParams,
                    object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is KYCLocationResponse) {
                                accessTokenResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            accessTokenResponseObs.postValue(
                                Resource.error(
                                    errorResponse.toString(),
                                    null
                                )
                            )
                        }
                    })
            } catch (e: Exception) {
                accessTokenResponseObs.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun verifyPasscode() {
        baseCallBack!!.callNextScreen(Intent(context, PasscodeActivity::class.java), null)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class BaseViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val clickHandler: ClickHandler?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(BaseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BaseViewModel(baseCallBack, clickHandler,context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}