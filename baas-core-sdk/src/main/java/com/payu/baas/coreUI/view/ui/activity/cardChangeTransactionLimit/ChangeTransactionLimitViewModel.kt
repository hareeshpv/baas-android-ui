package com.payu.baas.coreUI.view.ui.activity.cardChangeTransactionLimit

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.model.TransactionLimitModel
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.SetLimitResponse
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ChangeTransactionLimitViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val tLimitResponseObs = MutableLiveData<Resource<Any>>()
    private val limitValue = "10000"
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

    }

    fun setTextForLimitTitles() {
        strSwipeLimitTitle.set(context.resources.getString(R.string.swipe_limit_text))
        strTransLimitLayoutTitle.set(context.resources.getString(R.string.transaction_limits_text))
        strAtmWithdrawalLimitTitle.set(context.resources.getString(R.string.atm_withdrawl_limit_text))
    }

    /* dummy values for limits before hitting the api*/
    fun setLimtValues(transactionLimits: TransactionLimitModel) {
        strSwipePerTransactionValue.set(transactionLimits.swipePerTransaction)
        strSwipeDailyValue.set(transactionLimits.swipeDaily)
        strTransPerTransactionValue.set(transactionLimits.onlinePerTransaction)
        strTransDailyValue.set(transactionLimits.onlineDaily)
        strAtmPerTransactionValue.set(transactionLimits.atmPerTransaction)
        strAtmDailyValue.set(transactionLimits.atmDaily)
    }

    fun setTransactionLimits() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                tLimitResponseObs.postValue(Resource.loading(null))
                try {
                    val transactionLimitsArray = JSONArray()

                    val atmJsonObj = JSONObject()
                    atmJsonObj.put(BaaSConstantsUI.BS_KEY_CHANNEL, BaaSConstantsUI.BS_VALUE_ATM_CHANNEL)
                    atmJsonObj.put(
                        BaaSConstantsUI.BS_KEY_PER_TRANSACTION,
                        strAtmPerTransactionValue.get()
                    )
                    atmJsonObj.put(BaaSConstantsUI.BS_KEY_DAILY, strAtmDailyValue.get())

                    val posJsonObj = JSONObject()
                    posJsonObj.put(
                        BaaSConstantsUI.BS_KEY_CHANNEL,
                        BaaSConstantsUI.BS_VALUE_SWIPE_CHANNEL
                    )
                    posJsonObj.put(
                        BaaSConstantsUI.BS_KEY_PER_TRANSACTION,
                        strSwipePerTransactionValue.get()
                    )
                    posJsonObj.put(BaaSConstantsUI.BS_KEY_DAILY, strSwipeDailyValue.get())

                    val ecomJsonObj = JSONObject()
                    ecomJsonObj.put(
                        BaaSConstantsUI.BS_KEY_CHANNEL,
                        BaaSConstantsUI.BS_VALUE_ONLINE_CHANNEL
                    )
                    ecomJsonObj.put(
                        BaaSConstantsUI.BS_KEY_PER_TRANSACTION,
                        strTransPerTransactionValue.get()
                    )
                    ecomJsonObj.put(BaaSConstantsUI.BS_KEY_DAILY, strTransDailyValue.get())

                    transactionLimitsArray.put(atmJsonObj)
                    transactionLimitsArray.put(posJsonObj)
                    transactionLimitsArray.put(ecomJsonObj)

                    val apiParams = ApiParams().apply {
                        transactionLimits = transactionLimitsArray
                    }
                    ApiCall.callAPI(ApiName.SET_LIMITS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SetLimitResponse) {
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
    internal class ChangeTransactionLimitViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ChangeTransactionLimitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChangeTransactionLimitViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}