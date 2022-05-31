package com.payu.baas.core.view.ui.activity.addressAddNew

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class AddNewAddressViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val addressResponseObs = MutableLiveData<Resource<Any>>()
    val strPinCode: ObservableField<String> = ObservableField("")
    val strAddressLine1: ObservableField<String> = ObservableField("")
    val strAddressLine2: ObservableField<String> = ObservableField("")
//    val strLandmark: ObservableField<String> = ObservableField("")
    val strCity: ObservableField<String> = ObservableField("")
    val strState: ObservableField<String> = ObservableField("")
    var mobileNumber: String? = null
    var imeiNumber: String? = null
    init {
        mobileNumber= SessionManagerUI.getInstance(context).userMobileNumber

    }

    fun addNewAddress() {
        baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_SAVE_NEW_ADDRESS,
            BaaSConstantsUI.CL_USER_SAVE_NEW_ADDRESS_EVENT_ID,
            SessionManager.getInstance(context).accessToken,
            imeiNumber,
            mobileNumber,
            Date(),
            "",
            ""
        )
        viewModelScope.launch {
            addressResponseObs.postValue(Resource.loading(null))
            try {
                val apiParams = ApiParams().apply {
                    addressLine1 = strAddressLine1.get()
                    addressLine2 = strAddressLine2.get()
                    city = strCity.get()
                    stateId = strState.get()
                    pinCode = strPinCode.get()
                }

                ApiCall.callAPI(ApiName.UPDATE_USER_ADDRESS, apiParams, object : ApiHelperUI {
                    override fun onSuccess(apiResponse: ApiResponse) {
                        if (apiResponse is UpdateAddressResponse) {
                            addressResponseObs.postValue(Resource.success(apiResponse))
                        }
                    }

                    override fun onError(errorResponse: ErrorResponse) {
                        addressResponseObs.postValue(Resource.error(errorResponse.errorMessage!!, errorResponse))
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
    internal class AddNewAddressViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddNewAddressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddNewAddressViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}