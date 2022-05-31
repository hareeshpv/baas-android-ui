package com.payu.baas.core.view.ui.activity.cards

import android.content.Context
import android.content.Intent
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
import com.payu.baas.core.view.ui.activity.cardTransactionLimit.TransactionLimitsActivity
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class CardsViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

//    private val context = getApplication<Application>().applicationContext

    private val cDetailsResponseObs = MutableLiveData<Resource<Any>>()
    private val cImageResponseObs = MutableLiveData<Resource<Any>>()
    private val cPinStatusResponseObs = MutableLiveData<Resource<Any>>()
    private val sTransactionResponseObs = MutableLiveData<Resource<Any>>()
//    private val gTransactionResponseObs = MutableLiveData<Resource<Any>>()
    private val blockResponseObs = MutableLiveData<Resource<Any>>()
    private val unBlockResponseObs = MutableLiveData<Resource<Any>>()
    private val cardReOrderObs = MutableLiveData<Resource<Any>>()
    private val cardStatusObs = MutableLiveData<Resource<Any>>()
    val strUserName: ObservableField<String> = ObservableField("")
    var isGoingToChangePin: Boolean = false
    var tModeAllow: Boolean = false
    var tModeChannel: String = ""
    var mobileNumber: String? = null
    var imeiNumber: String? = null

    init {
        strUserName.set(SessionManagerUI.getInstance(context).userName)
        getCardDetails()
        mobileNumber= SessionManagerUI.getInstance(context).userMobileNumber

//        getTransactionMode()
    }

    fun openTransacationLimitsScreen() {
        baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_VIEW_TRANSACTION_LIMITS,
            BaaSConstantsUI.CL_USER_VIEW_TRANSACTION_LIMITS_EVENT_ID,
            SessionManager.getInstance(context).accessToken,
            imeiNumber,
            mobileNumber,
            Date(),
            "",
            ""
        )
        baseCallBack?.callNextScreen(Intent(context, TransactionLimitsActivity::class.java), null)
    }

//    fun openBlockReasonScreen() {
//        baseCallBack?.callNextScreen(Intent(context, CardBlockReasonActivity::class.java), null)
//    }
//
//    fun openChangeCardPinScreen() {
//        isGoingToChangePin = true
//        baseCallBack?.callNextScreen(Intent(context, UpadatePinActivity::class.java), null)
//    }


    fun getCardBackImage() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                cImageResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()

                    ApiCall.callAPI(ApiName.CARD_IMAGE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is CardImageResponse) {
                                cImageResponseObs.postValue(Resource.success(apiResponse))
//                            ivCardBack.set(
//                                AppCompatResources.getDrawable(
//                                getApplication(),
//                                R.drawable.ic_svg_lock
//                            ))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cImageResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    cImageResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardImageResponseObs(): LiveData<Resource<Any>> {
        return cImageResponseObs
    }

    fun getCardDetails() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                cDetailsResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()

                    ApiCall.callAPI(ApiName.CARD_DETAILS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is CardDetailResponse) {
                                cDetailsResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cDetailsResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    cDetailsResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardDetailsResponseObs(): LiveData<Resource<Any>> {
        return cDetailsResponseObs
    }

    fun getCardPinStatus() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            isGoingToChangePin = false
            viewModelScope.launch {
                cPinStatusResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()

                    ApiCall.callAPI(ApiName.GET_PIN_STATUS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetCardPinStatusResponse) {
                                cPinStatusResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cPinStatusResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    cPinStatusResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardPinStatusResponseObs(): LiveData<Resource<Any>> {
        return cPinStatusResponseObs
    }

//    fun getTransactionMode() {
//        if (baseCallBack?.isInternetAvailable(true) == true) {
//            viewModelScope.launch {
//                gTransactionResponseObs.postValue(Resource.loading(null))
//                try {
//                    val apiParams = ApiParams()
//
//                    ApiCall.callAPI(ApiName.GET_TRANSACTION_MODE, apiParams, object : ApiHelperUI {
//                        override fun onSuccess(apiResponse: ApiResponse) {
//                            if (apiResponse is GetCardPinStatusResponse) {
//                                gTransactionResponseObs.postValue(Resource.success(apiResponse))
//                            }
//                        }
//
//                        override fun onError(errorResponse: ErrorResponse) {
//                            gTransactionResponseObs.postValue(
//                                Resource.error(
//                                    errorResponse.errorMessage!!,
//                                    null
//                                )
//                            )
//                        }
//                    })
//                } catch (e: Exception) {
//                    gTransactionResponseObs.postValue(Resource.error(e.toString(), null))
//                }
//            }
//        }
//    }
//
//    fun getTransactionModeResponseObs(): LiveData<Resource<Any>> {
//        return gTransactionResponseObs
//    }

    fun setTransactionMode() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                sTransactionResponseObs.postValue(Resource.loading(null))
                try {
                    val transactionModeJsonObj = JSONObject()

                    transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_ALLOW, tModeAllow)
                    transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_CHANNEL, tModeChannel)
                    transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_LOCATION, "DOM")

                    val apiParams = ApiParams().apply {
                        transactionModes = transactionModeJsonObj
                    }

                    ApiCall.callAPI(ApiName.SET_TRANSACTION_MODE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is SetTransactionModeResponse) {
                                sTransactionResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            sTransactionResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    sTransactionResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getSTransactionModeResponseObs(): LiveData<Resource<Any>> {
        return sTransactionResponseObs
    }

    fun setCardStatus() {
//        viewModelScope.launch {
//            cardStatusObs.postValue(Resource.loading(null))
//            try {
//                val transactionModeJsonObj = JSONObject()
//
//                transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_ALLOW, tModeAllow)
//                transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_CHANNEL, tModeChannel)
//                transactionModeJsonObj.put(BaaSConstantsUI.BS_KEY_LOCATION, "DOM")
//
//                val apiParams = ApiParams().apply {
//                    transactionModes = transactionModeJsonObj
//                }
//
//                ApiCall.callAPI(ApiName.SET_TRANSACTION_MODE, apiParams, object : ApiHelperUI {
//                    override fun onSuccess(apiResponse: ApiResponse) {
//                        if (apiResponse is GetCardPinStatusResponse) {
//                            cardStatusObs.postValue(Resource.success(apiResponse))
//                        }
//                    }
//
//                    override fun onError(errorResponse: ErrorResponse) {
//                        cardStatusObs.postValue(
//                            Resource.error(
//                                errorResponse.errorMessage!!,
//                                null
//                            )
//                        )
//                    }
//                })
//            } catch (e: Exception) {
//                cardStatusObs.postValue(Resource.error(e.toString(), null))
//            }
//        }
    }

    fun getCardStatusResponseObs(): LiveData<Resource<Any>> {
        return cardStatusObs
    }
    fun blockCard() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                blockResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        reason = "Temps"
                    }

                    ApiCall.callAPI(ApiName.BLOCK_CARD, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is BlockCardResponse) {
                                blockResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            blockResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    blockResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getBlockResponseObs(): LiveData<Resource<Any>> {
        return blockResponseObs
    }
    fun unBlockCard() {
        viewModelScope.launch {
            unBlockResponseObs.postValue(Resource.loading(null))
            try {
                val apiParams = ApiParams()

                ApiCall.callAPI(ApiName.UNBLOCK_CARD, apiParams, object : ApiHelperUI {
                    override fun onSuccess(apiResponse: ApiResponse) {
                        if (apiResponse is UnblockCardResponse) {
                            unBlockResponseObs.postValue(Resource.success(apiResponse))
                        }
                    }

                    override fun onError(errorResponse: ErrorResponse) {
                        unBlockResponseObs.postValue(
                            Resource.error(
                                errorResponse.errorMessage!!,
                                errorResponse
                            )
                        )
                    }
                })
            } catch (e: Exception) {
                unBlockResponseObs.postValue(Resource.error(e.toString(), null))
            }
        }
    }

        fun getUnBlockResponseObs(): LiveData<Resource<Any>> {
        return unBlockResponseObs
    }
    fun cardReOrder() {
        if (baseCallBack!!.isInternetAvailable(true)) {
            viewModelScope.launch {
                cardReOrderObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.CARD_REORDER, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is CardReorderResponse) {
                                cardReOrderObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cardReOrderObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    cardReOrderObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardReOrderObs(): LiveData<Resource<Any>> {
        return cardReOrderObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class CardViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CardsViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}