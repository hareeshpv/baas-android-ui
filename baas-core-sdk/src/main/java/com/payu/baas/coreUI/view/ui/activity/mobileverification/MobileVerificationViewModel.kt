package com.payu.baas.coreUI.view.ui.activity.mobileverification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import com.payu.baas.coreUI.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch
import java.util.*

class MobileVerificationViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val otpResponseObs = MutableLiveData<Resource<Any>>()
    val strMobileNo: ObservableField<String> = ObservableField("")

    fun sendOtp() {
        if (!(baseCallBack!!.isInternetAvailable(false))) {
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack!!.callNextScreen(
                Intent(
                    context,
                    ErrorActivity::class.java
                ), mBundle
            )
        } else {
            var mobileNum = strMobileNo.get().toString().trim()
            SessionManager.getInstance(context).deviceBindingId = UUID.randomUUID().toString()
            viewModelScope.launch {
                otpResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber = mobileNum
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
//                            }else
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

    internal class MobileVerificationViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(MobileVerificationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MobileVerificationViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}