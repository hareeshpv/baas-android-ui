package com.payu.baas.core.view.ui.activity.verifyNewEmail

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.SendOtpResponse
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch

class VerifyNewEmailViewModel (application: Application) : AndroidViewModel(application){
    private val verifyOTPResponseObs = MutableLiveData<Resource<Any>>()
    private val resendOTPResponseObs = MutableLiveData<Resource<Any>>()
    val emailotp1: ObservableField<String> = ObservableField("")
    val emailotp2: ObservableField<String> = ObservableField("")
    val emailotp3: ObservableField<String> = ObservableField("")
    val emailotp4: ObservableField<String> = ObservableField("")
    lateinit var emailOtpVerificationBaseCallback: BaseCallback
    @SuppressLint("StaticFieldLeak")
    val context: Context = getApplication<Application>().applicationContext
    var strVerifyEmail: String? = null


    fun verifyNewEmail() {

        if(!emailOtpVerificationBaseCallback.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            emailOtpVerificationBaseCallback.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else {
            viewModelScope.launch {
                verifyOTPResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobile = strVerifyEmail?: ""
                        code = """${emailotp1.get().toString().trim()}${
                            emailotp2.get().toString().trim()
                        }${emailotp3.get().toString().trim()}${emailotp4.get().toString().trim()}"""
                    }
                    ApiCall.callAPI(ApiName.VERIFY_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is VerifyOtpResponse) {
                                verifyOTPResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            verifyOTPResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    verifyOTPResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }
    fun resendEmailOTP() {

        if(!emailOtpVerificationBaseCallback.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            emailOtpVerificationBaseCallback.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else{
            viewModelScope.launch {
                resendOTPResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber = strVerifyEmail?:""
                    }
                    ApiCall.callAPI(ApiName.SEND_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SendOtpResponse) {
                                resendOTPResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            resendOTPResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    resendOTPResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getResendOtpResponseObs(): LiveData<Resource<Any>> {
        return resendOTPResponseObs
    }
    fun verifyOtpResponseObs(): LiveData<Resource<Any>> {
        return verifyOTPResponseObs
    }








}



