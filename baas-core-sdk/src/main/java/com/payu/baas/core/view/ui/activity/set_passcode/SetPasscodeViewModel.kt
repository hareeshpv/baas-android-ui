package com.payu.baas.core.view.ui.activity.set_passcode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.SetPasswordResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch

class SetPasscodeViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val passcodeResponseObs = MutableLiveData<Resource<Any>>()
    val code1: ObservableField<String> = ObservableField("")
    val code2: ObservableField<String> = ObservableField("")
    val code3: ObservableField<String> = ObservableField("")
    val code4: ObservableField<String> = ObservableField("")

    fun setPasscode() {
        if (!baseCallBack!!.isInternetAvailable(false)) {
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack!!.callNextScreen(Intent(context, ErrorActivity::class.java), mBundle)
        } else {
            viewModelScope.launch {
                passcodeResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        password =
                            """${code1.get().toString().trim()}${code2.get().toString().trim()}${
                                code3.get().toString().trim()
                            }${code4.get().toString().trim()}"""
                    }
                    ApiCall.callAPI(ApiName.SET_PASSWORD, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SetPasswordResponse) {
                                passcodeResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
//                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
//                                baseCallBack.showTechinicalError()
//                            } else
                                passcodeResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                        }
                    })
                } catch (e: Exception) {
                    passcodeResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }

    fun getPasscodeResponseObs(): LiveData<Resource<Any>> {
        return passcodeResponseObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class SetPasscodeViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(SetPasscodeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SetPasscodeViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}