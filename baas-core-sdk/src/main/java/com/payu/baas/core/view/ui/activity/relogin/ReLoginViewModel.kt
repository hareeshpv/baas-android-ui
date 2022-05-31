package com.payu.baas.core.view.ui.activity.relogin

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class ReLoginViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val otpResponseObs = MutableLiveData<Resource<Any>>()

    val strMobileNo: ObservableField<String> = ObservableField("")

    init {
    }

    fun sendOtp() {
        if (baseCallBack!!.isInternetAvailable(true)) {
//            SessionManager.getInstance(context).deviceBindingId = UUID.randomUUID().toString()
            viewModelScope.launch {
                otpResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber = strMobileNo.get().toString().trim()
                    }

                    ApiCall.callAPI(ApiName.SEND_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SendOtpResponse) {
                                otpResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
//                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
//                                baseCallBack.showTechinicalError()
//                            } else
                                otpResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                        }
                    })
                } catch (e: Exception) {
                    otpResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }

    fun getOtpResponseObs(): LiveData<Resource<Any>> {
        return otpResponseObs
    }

    internal class ReLoginViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ReLoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReLoginViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}