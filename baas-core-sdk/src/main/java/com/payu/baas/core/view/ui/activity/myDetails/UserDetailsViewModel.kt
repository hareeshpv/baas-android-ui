package com.payu.baas.core.view.ui.activity.myDetails

import android.content.Context
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetUserDetailsResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class UserDetailsViewModel (
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val userDetailsResponseObs = MutableLiveData<Resource<Any>>()
     val userTitle = MutableLiveData<String>()
     var userMobileNumber = MutableLiveData<String>()
     var userCurrentAddress = MutableLiveData<String>()
     var userPANCardNumber = MutableLiveData<String>()
     var userAdharCardNumber = MutableLiveData<String>()

    init {
        userDetailResponse()
    }

    fun userDetailResponse() {
        if (baseCallBack!!.isInternetAvailable(true)) {
            viewModelScope.launch {
                userDetailsResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {

                    }
                    ApiCall.callAPI(ApiName.GET_USER_DETAILS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetUserDetailsResponse) {
                                userTitle.value = (apiResponse as GetUserDetailsResponse).name
                                userMobileNumber.value = "+91"+" "+(apiResponse as GetUserDetailsResponse).mobileNumber
                                userCurrentAddress.value = (apiResponse as GetUserDetailsResponse).address
                                userPANCardNumber.value = (apiResponse as GetUserDetailsResponse).maskedPan
                                userAdharCardNumber.value = (apiResponse as GetUserDetailsResponse).maskedAadhaar
                                userDetailsResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            userDetailsResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    userDetailsResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }



    fun userDetailsResponseObs(): LiveData<Resource<Any>> {
        return userDetailsResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class UserDetailsViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserDetailsViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}