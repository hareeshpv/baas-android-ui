package com.payu.baas.coreUI.view.ui.activity.dashboard

import android.content.Context
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class DashboardViewModel(
    baseCallBack: BaseCallback,
    context: Context
) : BaseViewModel(baseCallBack, null,context){

    private val s3BucketUrlObs = MutableLiveData<Resource<Any>>()
    private val userDetailsObs = MutableLiveData<Resource<Any>>()
    private val accountBalanceDetailsObs = MutableLiveData<Resource<Any>>()
    private val accountDetailsObs = MutableLiveData<Resource<Any>>()
    private val notificationObs = MutableLiveData<Resource<Any>>()
    private val cardImageObs = MutableLiveData<Resource<Any>>()
    private val tipsObs = MutableLiveData<Resource<Any>>()
    private val offersAndDealsObs = MutableLiveData<Resource<Any>>()
    private val cardFulfilmentObs = MutableLiveData<Resource<Any>>()
    private val cardPinStatusObs = MutableLiveData<Resource<Any>>()
    private val cardValidateKitStatusObs = MutableLiveData<Resource<Any>>()
    private val helpObs = MutableLiveData<Resource<Any>>()
    private val validateCardKitObs = MutableLiveData<Resource<Any>>()
    private val cardReOrderObs = MutableLiveData<Resource<Any>>()
    private val zdCredentialsObs = MutableLiveData<Resource<Any>>()

    init {
        getS3BucketUrl()
    }

    fun getS3BucketUrl() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                s3BucketUrlObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.GET_S3_BUCKET_LINK, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is GetS3BucketLinkResponse) {
                                s3BucketUrlObs.postValue(Resource.success(apiResponse))
                            }

                            callApis()
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            s3BucketUrlObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )

                            callApis()
                        }
                    })
                } catch (e: Exception) {
                    s3BucketUrlObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getS3BucketUrlObs(): LiveData<Resource<Any>> {
        return s3BucketUrlObs
    }

    private fun callApis() {
        getZendeskDetails()
        getUserDetails()
        getAccountBalanceDetails()
        getAccountDetails()
        getNotifications()
        getCardImageDetails()
        getTips()
        getOffersAndDeals()
        getCardFulfillment()
    }
    fun getZendeskDetails() {
        if (baseCallBack!!.isInternetAvailable(true)) {
            viewModelScope.launch {
                zdCredentialsObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                    }

                    ApiCall.callAPI(ApiName.ZENDESK_CREDENTIALS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is ZendeskCredentialsResponse) {
                                zdCredentialsObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            zdCredentialsObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    zdCredentialsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }
    fun getUserDetails() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                userDetailsObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {

                    }

                    ApiCall.callAPI(ApiName.GET_USER_DETAILS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is GetUserDetailsResponse) {
                                userDetailsObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            userDetailsObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    userDetailsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getUserDetailsObs(): LiveData<Resource<Any>> {
        return userDetailsObs
    }

    fun getAccountBalanceDetails() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                accountBalanceDetailsObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(
                        ApiName.GET_ACCOUNT_BALANCE_DETAILS,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetAccountBalanceDetailsResponse) {
                                    accountBalanceDetailsObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                accountBalanceDetailsObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage.toString(),
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    accountBalanceDetailsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getAccountBalanceDetailsObs(): LiveData<Resource<Any>> {
        return accountBalanceDetailsObs
    }

    fun getAccountDetails() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                accountDetailsObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(
                        ApiName.GET_ACCOUNT_DETAILS,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetAccountDetailsResponse) {
                                    accountDetailsObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                accountDetailsObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage.toString(),
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    accountDetailsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getAccountDetailsObs(): LiveData<Resource<Any>> {
        return accountDetailsObs
    }

    fun getNotifications() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                notificationObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.GET_NOTIFICATIONS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is NotificationResponse) {
                                notificationObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            notificationObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    notificationObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getNotificationObs(): LiveData<Resource<Any>> {
        return notificationObs
    }


    fun getCardImageDetails() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                cardImageObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.CARD_IMAGE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is CardImageResponse) {
                                cardImageObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cardImageObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    cardImageObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardImageObs(): LiveData<Resource<Any>> {
        return cardImageObs
    }

    fun getTips() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                tipsObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.GET_TIPS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is TipResponse) {
                                tipsObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            tipsObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    tipsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getTipsObs(): LiveData<Resource<Any>> {
        return tipsObs
    }

    fun getOffersAndDeals() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                with(offersAndDealsObs) { postValue(Resource.loading(null)) }
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.GET_OFFER, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is OffersResponse) {
                                offersAndDealsObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            offersAndDealsObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    offersAndDealsObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getOffersAndDealsObs(): LiveData<Resource<Any>> {
        return offersAndDealsObs
    }

    fun getCardFulfillment() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                cardFulfilmentObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.CARD_FULFILMENT, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is CardFulfilmentResponse) {
                                cardFulfilmentObs.postValue(Resource.success(apiResponse))
                            }

                            getCardPinStatus()
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cardFulfilmentObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )

                            getCardPinStatus()
                        }
                    })
                } catch (e: Exception) {
                    cardFulfilmentObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardFulfilmentObs(): LiveData<Resource<Any>> {
        return cardFulfilmentObs
    }

    fun getCardPinStatus() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                cardPinStatusObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.GET_PIN_STATUS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is GetCardPinStatusResponse) {
                                cardPinStatusObs.postValue(Resource.success(apiResponse))
                            }

                            getCardValidateKitStatus()
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            cardPinStatusObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )

                            getCardValidateKitStatus()
                        }
                    })
                } catch (e: Exception) {
                    cardPinStatusObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardPinStatusObs(): LiveData<Resource<Any>> {
        return cardPinStatusObs
    }

    fun getCardValidateKitStatus() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                cardValidateKitStatusObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(
                        ApiName.GET_VALIDATE_CARD_KIT_STATUS,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetValidateCardKitStatusResponse) {
                                    cardValidateKitStatusObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                cardValidateKitStatusObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage.toString(),
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    cardValidateKitStatusObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getCardValidateKitStatusObs(): LiveData<Resource<Any>> {
        return cardValidateKitStatusObs
    }


    fun getHelp() {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                helpObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {}

                    ApiCall.callAPI(ApiName.HELP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is HelpResponse) {
                                helpObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            helpObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    helpObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getHelpObs(): LiveData<Resource<Any>> {
        return helpObs
    }

    fun validateCardKit(isCardDelivered: Boolean) {
        if (baseCallBack!!.isInternetAvailable(true)) {

            viewModelScope.launch {
                validateCardKitObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        isCardReceived = isCardDelivered
                    }

                    ApiCall.callAPI(ApiName.VALIDATE_CARD_KIT, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is ValidateCardKitResponse) {
                                validateCardKitObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            validateCardKitObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    validateCardKitObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getValidateCardKitObs(): LiveData<Resource<Any>> {
        return validateCardKitObs
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

    internal class DashboardViewModelFactory(
        private val baseCallBack: BaseCallback,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(baseCallBack,context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}