package com.payu.baas.coreUI.view.ui.activity.cardTransactionLimit

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.model.*
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetLimitsResponse
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class TransactionLimitViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val tLimitResponseObs = MutableLiveData<Resource<Any>>()

    val strTransLimitLayoutTitle: ObservableField<String> = ObservableField("Swipe Limit")
    val strSwipeLimitTitle: ObservableField<String> = ObservableField("")
    val strAtmWithdrawalLimitTitle: ObservableField<String> = ObservableField("")
    val strSwipePerTransactionValue: ObservableField<String> = ObservableField("")
    val strSwipeDailyValue: ObservableField<String> = ObservableField("")
    val strTransPerTransactionValue: ObservableField<String> = ObservableField("")
    val strTransDailyValue: ObservableField<String> = ObservableField("")
    val strAtmPerTransactionValue: ObservableField<String> = ObservableField("")
    val strAtmDailyValue: ObservableField<String> = ObservableField("")

    init {
        getTransactionLimits()
    }

    fun setTextForLimitTitles() {
        strSwipeLimitTitle.set(context.resources.getString(R.string.swipe_limit_text))
        strTransLimitLayoutTitle.set(context.resources.getString(R.string.transaction_limits_text))
        strAtmWithdrawalLimitTitle.set(context.resources.getString(R.string.atm_withdrawl_limit_text))
    }

    /* dummy values for limits before hitting the api*/
    fun setLimtValues() {
        strSwipePerTransactionValue.set("Rs. 15000")
        strSwipeDailyValue.set("Rs. 6000")
        strTransPerTransactionValue.set("Rs. 15000")
        strTransDailyValue.set("Rs. 5000")
        strAtmPerTransactionValue.set("Rs. 15000")
        strAtmDailyValue.set("Rs. 4000")
    }

    /* fetch values from apis and set here*/
    fun updateLimtValues(limitConfigs: TransactionLimitModel) {
        strSwipePerTransactionValue.set("Rs. " + limitConfigs.swipePerTransaction)
        strSwipeDailyValue.set("Rs. " + limitConfigs.swipeDaily)
        strTransPerTransactionValue.set("Rs. " + limitConfigs.onlinePerTransaction)
        strTransDailyValue.set("Rs. " + limitConfigs.onlineDaily)
        strAtmPerTransactionValue.set("Rs. " + limitConfigs.atmPerTransaction)
        strAtmDailyValue.set("Rs. " + limitConfigs.atmDaily)
    }

    fun getTransactionLimits() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                tLimitResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()

                    ApiCall.callAPI(ApiName.GET_LIMITS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetLimitsResponse) {
                                tLimitResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            tLimitResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    tLimitResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getLimitResponseObs(): LiveData<Resource<Any>> {
        return tLimitResponseObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class TransactionLimitViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(TransactionLimitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionLimitViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}