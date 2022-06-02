package com.payu.baas.coreUI.view.ui.activity.editCurrentAddress

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetAddressResponse
import com.payu.baas.core.model.responseModels.UpdateAddressResponse
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class EditCurrentAddressViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val addressResponseObs = MutableLiveData<Resource<Any>>()

    //
    val strPinCode: ObservableField<String> = ObservableField("")
    val strAddressLine1: ObservableField<String> = ObservableField("")
    val strAddressLine2: ObservableField<String> = ObservableField("")
    val strCity: ObservableField<String> = ObservableField("")
    val strState: ObservableField<String> = ObservableField("")

    init {
        getAddress()
    }

    fun updateAddress() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                addressResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        addressLine1 = strAddressLine1.get()
                        addressLine2 = strAddressLine2.get()
                        city = strCity.get()
                        pinCode = strPinCode.get()
                        stateId = strState.get()
                    }

                    ApiCall.callAPI(ApiName.UPDATE_USER_ADDRESS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is UpdateAddressResponse) {
                                addressResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            addressResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
//                            baseCallBack.callNextScreen(
//                                Intent(context,
//                                    EditCurrentAddressActivity::class.java
//                                ), null
//                            )
                        }
                    })
                } catch (e: Exception) {
                    addressResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }

    fun getAddressResponseObs(): LiveData<Resource<Any>> {
        return addressResponseObs
    }


    fun getAddress() {
        viewModelScope.launch {
            addressResponseObs.postValue(Resource.loading(null))
            try {
                val apiParams = ApiParams()

                ApiCall.callAPI(ApiName.GET_ADDRESS, apiParams, object : ApiHelperUI {
                    override fun onSuccess(apiResponse: ApiResponse) {
                        if (apiResponse is GetAddressResponse) {
                            addressResponseObs.postValue(Resource.success(apiResponse))


                        }
                    }

                    override fun onError(errorResponse: ErrorResponse) {
                        addressResponseObs.postValue(
                            Resource.error(
                                errorResponse.errorMessage!!,
                                errorResponse
                            )
                        )
                    }
                })
            } catch (e: Exception) {
                addressResponseObs.postValue(Resource.error(e.toString(), null))
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class EditCurrentAddressViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(EditCurrentAddressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditCurrentAddressViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}