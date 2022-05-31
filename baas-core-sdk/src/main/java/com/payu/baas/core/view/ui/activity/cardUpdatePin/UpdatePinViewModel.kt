package com.payu.baas.core.view.ui.activity.cardUpdatePin

import android.content.Context
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class UpdatePinViewModel(baseCallBack: BaseCallback?,
                         context: Context
) : BaseViewModel(baseCallBack, null,context) {
    private val upadtePinResponseObs = MutableLiveData<Resource<Any>>()
    private val upadtePinStatusResponseObs = MutableLiveData<Resource<Any>>()

    init {
        updatePin()
    }

    fun updatePin() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                upadtePinResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        redirect_link = BaaSConstantsUI.CARD_SET_PIN_REDIRECT_URL
                    }

                    ApiCall.callAPI(ApiName.CARD_SET_PIN, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is CardSetPinResponse) {
                                upadtePinResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            upadtePinResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    upadtePinResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getUpadtePinResponseObs(): LiveData<Resource<Any>> {
        return upadtePinResponseObs
    }

    fun updatePinStatus(status: Boolean) {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                upadtePinStatusResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        pin_status = status
                    }

                    ApiCall.callAPI(ApiName.UPDATE_PIN, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is UpdateCardPinSetStatusResponse) {
                                upadtePinStatusResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            upadtePinStatusResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    upadtePinStatusResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getUpadtePinStatusResponseObs(): LiveData<Resource<Any>> {
        return upadtePinStatusResponseObs
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class UpdatePinViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(UpdatePinViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UpdatePinViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}