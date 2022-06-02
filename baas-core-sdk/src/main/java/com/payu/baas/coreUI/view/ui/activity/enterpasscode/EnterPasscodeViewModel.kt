package com.payu.baas.coreUI.view.ui.activity.enterpasscode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.LoginResponse
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import com.payu.baas.coreUI.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch

class EnterPasscodeViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val enterPasscodeResponseObs = MutableLiveData<Resource<Any>>()
    private val blockResponseObs = MutableLiveData<Resource<Any>>()
    private val tLimitResponseObs = MutableLiveData<Resource<Any>>()
    val pin: ObservableField<String> = ObservableField("")
    var mobile_num: String? = null
    var strPinLength: String? = null

    //    lateinit var enterPasscodeBaseCallback: BaseCallback
    @SuppressLint("StaticFieldLeak")
    fun passcode() {
        strPinLength = pin.get().toString().trim()
        if (strPinLength!!.length == 4) {
            if (!(baseCallBack!!.isInternetAvailable(false))) {
                val mBundle = Bundle()
                mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
                baseCallBack?.callNextScreen(Intent(context, ErrorActivity::class.java), mBundle)
            } else {
                viewModelScope.launch {
                    enterPasscodeResponseObs.postValue(Resource.loading(null))
                    try {
                        val apiParams = ApiParams().apply {
                            username = mobile_num ?: ""
                            password = strPinLength ?: ""
                        }

                        ApiCall.callAPI(ApiName.LOGIN, apiParams, object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                if (apiResponse is LoginResponse) {
                                    enterPasscodeResponseObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
//                                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
//                                    baseCallBack.showTechinicalError()
//                                } else
                                    enterPasscodeResponseObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                            }
                        })

                    } catch (e: Exception) {
                        enterPasscodeResponseObs.postValue(Resource.error(e.toString(), null))
                    }

                }

            }

        }

    }


    fun getEnterPasscodeResponseObs(): LiveData<Resource<Any>> {
        return enterPasscodeResponseObs
    }

//    fun blockCard(strOtherReasonForCardBlock : String) {
//        viewModelScope.launch {
//            blockResponseObs.postValue(Resource.loading(null))
//            try {
//                val apiParams = ApiParams().apply {
//                    reason = strOtherReasonForCardBlock
//                }
//
//                ApiCall.callAPI(ApiName.BLOCK_CARD, apiParams, object : ApiHelperUI {
//                    override fun onSuccess(apiResponse: ApiResponse) {
//                        if (apiResponse is BlockCardResponse) {
//                            blockResponseObs.postValue(Resource.success(apiResponse))
//                        }
//                    }
//
//                    override fun onError(errorResponse: ErrorResponse) {
//                        blockResponseObs.postValue(
//                            Resource.error(
//                                errorResponse.errorMessage!!,
//                                null
//                            )
//                        )
//                    }
//                })
//            } catch (e: Exception) {
//                blockResponseObs.postValue(Resource.error(e.toString(), null))
//            }
//        }
//    }

//    fun getBlockResponseObs(): LiveData<Resource<Any>> {
//        return blockResponseObs
//    }
//    fun setTransactionLimits(strChangedTransactionLimit : String) {
//        viewModelScope.launch {
//            tLimitResponseObs.postValue(Resource.loading(null))
//            try {
//              var transactionLimitModel  = JsonUtil.toObject(strChangedTransactionLimit,
//                  TransactionLimitModel::class.java) as
//                      TransactionLimitModel
//                val limitConfigJsonArray = JSONArray()
//                val limitConfigJsonObj = JSONObject()
//                val thresholdJsonArray = JSONArray()
//                val thresholdJsonObj = JSONObject()
//
//                thresholdJsonObj.put(BaaSConstantsUI.BS_KEY_AMMOUNT_LIMIT, transactionLimitModel.swipePerTransaction)
//                thresholdJsonObj.put(BaaSConstantsUI.BS_KEY_TARGET_DURATION, "DAY")
//                thresholdJsonObj.put(BaaSConstantsUI.BS_KEY_USAGE_LIMIT, 10)
//
//                thresholdJsonArray.put(thresholdJsonObj)
//
//                limitConfigJsonObj.put(BaaSConstantsUI.BS_KEY_CHANNEL, "POS")
//                limitConfigJsonObj.put(BaaSConstantsUI.BS_KEY_LOCATION, "DOM")
//                limitConfigJsonObj.put(BaaSConstantsUI.BS_KEY_TRANSACTION_MODE, "CONTACTLESS")
//                limitConfigJsonObj.put(BaaSConstantsUI.BS_KEY_THRESHOLDS, thresholdJsonArray)
//
//                limitConfigJsonArray.put(limitConfigJsonObj)
//
//                val apiParams = ApiParams().apply {
//                    limitConfigs = limitConfigJsonArray
//                }
//
//                ApiCall.callAPI(ApiName.SET_LIMITS, apiParams, object : ApiHelperUI {
//                    override fun onSuccess(apiResponse: ApiResponse) {
//                        if (apiResponse is SetLimitResponse) {
//                            tLimitResponseObs.postValue(Resource.success(apiResponse))
//                        }
//                    }
//
//                    override fun onError(errorResponse: ErrorResponse) {
//                        tLimitResponseObs.postValue(
//                            Resource.error(
//                                errorResponse.errorMessage!!,
//                                null
//                            )
//                        )
//                    }
//                })
//            } catch (e: Exception) {
//                tLimitResponseObs.postValue(Resource.error(e.toString(), null))
//            }
//        }
//    }

//    fun getLimitResponseObs(): LiveData<Resource<Any>> {
//        return tLimitResponseObs
//    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class PasscodeViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(EnterPasscodeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EnterPasscodeViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}