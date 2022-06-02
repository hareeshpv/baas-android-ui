package com.payu.baas.coreUI.view.ui.activity.address

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class AddressViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val addressResponseObs = MutableLiveData<Resource<Any>>()
    val strAddress: ObservableField<String> = ObservableField("")
    val ivTick: ObservableField<Drawable> = ObservableField()
    val btEnabled: ObservableField<Boolean> = ObservableField()
    var isAddressSelected = true

    init {
        isAddressSelected = true
        ivTick.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.ic_svg_tick_green
            )
        )
        btEnabled.set(true)
    }

    fun onSelectingAddress() {
        if (isAddressSelected) {
            isAddressSelected = false
            ivTick.set(null)
            btEnabled.set(false)
        } else {
            isAddressSelected = true
            ivTick.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_svg_tick_green
                )
            )
            btEnabled.set(true)
        }
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

    fun getAddressResponseObs(): LiveData<Resource<Any>> {
        return addressResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class AddressViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddressViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}