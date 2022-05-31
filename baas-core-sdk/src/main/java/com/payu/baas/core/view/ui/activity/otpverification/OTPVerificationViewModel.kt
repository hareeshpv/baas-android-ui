package com.payu.baas.core.view.ui.activity.otpverification

import android.annotation.SuppressLint
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
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch
import java.util.*

class OTPVerificationViewModel(baseCallBack: BaseCallback?, context: Context
) : BaseViewModel(baseCallBack, null,context){
    private val verifyOTPResponseObs = MutableLiveData<Resource<Any>>()
    private val resendOTPResponseObs = MutableLiveData<Resource<Any>>()
    val strotp1: ObservableField<String> = ObservableField("")
    val strotp2: ObservableField<String> = ObservableField("")
    val strotp3: ObservableField<String> = ObservableField("")
    val strotp4: ObservableField<String> = ObservableField("")
    @SuppressLint("StaticFieldLeak")
    var mobileNo_: String? = null
    var imeiNumber: String? = null


    init {
        mobileNo_ = SessionManagerUI.getInstance(context).userMobileNumber
    }
    fun verify() {
        baseCallBack!!.cleverTapUserOnBoardingEvent(BaaSConstantsUI.CL_USER_VERIFY_OTP,BaaSConstantsUI.CL_USER_OTP_VERIFICATION_EVENT_ID, SessionManager.getInstance(context).accessToken,imeiNumber,mobileNo_, Date())
        if(!baseCallBack!!.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack!!.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else {
            viewModelScope.launch {
                verifyOTPResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobile = mobileNo_ ?: ""
                        code = """${strotp1.get().toString().trim()}${
                            strotp2.get().toString().trim()
                        }${strotp3.get().toString().trim()}${strotp4.get().toString().trim()}"""
                    }
                    ApiCall.callAPI(ApiName.VERIFY_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is VerifyOtpResponse) {
                                verifyOTPResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
//                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
//                                baseCallBack.showTechinicalError()
//                            }else
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
    fun resendOtp() {
        baseCallBack!!.cleverTapUserOnBoardingEvent(BaaSConstantsUI.CL_USER_RESEND_OTP,BaaSConstantsUI.CL_USER_RESEND_OTP_VERIFICATION_EVENT_ID,SessionManager.getInstance(context).accessToken,imeiNumber,mobileNo_, Date())
        if(! baseCallBack!!.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack!!.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else{
            viewModelScope.launch {
                resendOTPResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber = mobileNo_?:""
                    }
                    ApiCall.callAPI(ApiName.SEND_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SendOtpResponse) {
                                resendOTPResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
                                baseCallBack.showTechinicalError()
                            }else
                            resendOTPResponseObs.postValue(Resource.error(errorResponse.errorMessage!!, errorResponse))
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




    internal class OTPVerificationViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(OTPVerificationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OTPVerificationViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}



