package com.payu.baas.core.view.ui.activity.notemployee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.*
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch

class NotEmpViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val panEmpResponseObs = MutableLiveData<Resource<Any>>()
    val strPanNumber: ObservableField<String> = ObservableField("")
    val strEmpId: ObservableField<String> = ObservableField("")

    //    val strName: ObservableField<String> = ObservableField("")
    init {

    }

    fun verifyPanEmp() {
        if (!(baseCallBack!!.isInternetAvailable(false))) {
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack.callNextScreen(Intent(context, ErrorActivity::class.java), mBundle)
        } else {
            viewModelScope.launch {
                panEmpResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        panNumber = strPanNumber.get().toString().trim()
                        employeeId = strEmpId.get().toString().trim()
                        mobile =
                            SessionManagerUI.getInstance(context).registeredMobileNumber?.replace(
                                "+91 ",
                                ""
                            )
                    }

                    ApiCall.callAPI(ApiName.VERIFY_EMPLOYEE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is VerifyEmployeeResponse) {
                                panEmpResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
//                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
//                                baseCallBack.showTechinicalError()
//                            } else
                                panEmpResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                        }
                    })
                } catch (e: Exception) {
                    panEmpResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getPanEmpResponseObs(): LiveData<Resource<Any>> {
        return panEmpResponseObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class NotEmpViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(NotEmpViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotEmpViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}