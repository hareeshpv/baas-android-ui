package com.payu.baas.core.view.ui.activity.editEmailAddress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.UpdateEmailResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch

class EditEmailAddressViewModel  (
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val updateEmailResponseObs = MutableLiveData<Resource<Any>>()
    val strEmailAddress: ObservableField<String> = ObservableField("")

    fun sendEmailOTP() {
        if(!baseCallBack!!.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else{
            viewModelScope.launch {
                updateEmailResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        emailId = strEmailAddress.get().toString().trim()
                    }

                    ApiCall.callAPI(ApiName.UPDATE_USER_EMAIL, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is UpdateEmailResponse) {
                                updateEmailResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            updateEmailResponseObs.postValue(Resource.error(errorResponse.errorMessage!!, null))
                        }
                    })
                } catch (e: Exception) {
                    updateEmailResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }



    fun getUpdateEmailResponseObs(): LiveData<Resource<Any>> {
        return updateEmailResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class EditEmailtAddressViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(EditEmailAddressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditEmailAddressViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}