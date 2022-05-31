package com.payu.baas.core.view.ui.activity.advance

import android.content.Context
import android.view.View
import androidx.lifecycle.*
import com.payu.baas.core.R
import com.payu.baas.core.app.BaasUIApp
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.util.*
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class AdvanceViewModel(
    baseCallBack: BaseCallback?,
    clickHandler: ClickHandler,
    context: Context
) : BaseViewModel(baseCallBack, clickHandler, context) {

    private val transactionsList = MutableLiveData<Resource<Any>>()
    private val weeklyTransactionsList = MutableLiveData<Resource<Any>>()
    private val transactionStatusDetailObs = MutableLiveData<Resource<Any>>()
    private val accountDetailsObs = MutableLiveData<Resource<Any>>()
    var pageNumber = 0
    //Here we store transaction id to view transaction details
    var transactionID: String? = null
    init {
//        getAdvanceTransactions()
//        getAccountDetails()
    }

    fun onClick(view: View) {
        clickHandler?.click(view)
    }

    fun getAdvanceTransactions() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                transactionsList.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()

                    ApiCall.callAPI(ApiName.GET_SALARY_ADVANCE_INFO, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetSalaryAdvanceInfoResponse) {
                                transactionsList.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            transactionsList.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    transactionsList.postValue(Resource.error(e.toString(), null))
                }
            }
        }

        /* transactionsList.postValue(
             Resource.success(
                 Gson().fromJson(
                     "{'currentUsage':{'advanceAvailable':8,'advanceUsed':20,'feesCharged':20,'dueDate':'2021-11-21T18:30:00.000+00:00'},'usageHistory':[{'startDateTime':'2021-11-21T18:30:00.000+00:00','endDateTime':'2021-11-21T18:30:00.000+00:00','advanceAvailable':0,'advanceUsed':33,'feesCharged':2,'latePaymentFees':0},{'startDateTime':'2021-11-21T18:30:00.000+00:00','endDateTime':'2021-11-21T18:30:00.000+00:00','advanceAvailable':0,'advanceUsed':33,'feesCharged':2,'paidBack':19,'latePaymentFees':0}],'active':false}",
                     GetSalaryAdvanceInfoResponse::class.java
                 )
             )
         )*/

        /*passBookTransactionsList.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'list':[]}",
                    GetPassBookTransactionsResponse::class.java
                )
            )
        )*/

        /*if (baseCallBack.isInternetAvailable(true)) {
            viewModelScope.launch {
                passBookTransactionsList.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber = "123345"
                    }

                    ApiCall.callAPI(ApiName.SEND_OTP, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is SendOtpResponse) {
                                passBookTransactionsList.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                        }
                    })
                } catch (e: Exception) {
                    passBookTransactionsList.postValue(Resource.error(e.toString(), null))
                }
            }
        }*/
    }

    fun getAdvanceTransactionListResponseObs(): LiveData<Resource<Any>> {
        return transactionsList
    }

    fun getAdvanceWeeklyTransactionsDetailList(pStartDate: String, pEndDate: String) {
        if (pageNumber == -1)
            return

        if (baseCallBack?.isInternetAvailable(true) == true)
            viewModelScope.launch {
                weeklyTransactionsList.postValue(Resource.loading(null))
                try {
//                    var strStartDate = ""
//                    var strEndDate = ""
//                    strStartDate = UtilsUI.convertAdvanceDate(pStartDate!!)
//                    strEndDate = UtilsUI.convertAdvanceDate(pEndDate!!)
                    val apiParams = ApiParams().apply {
                        startDate = pStartDate
                        endDate = pEndDate
                        accountType = "Limit_Account"
                        debitIndicator = ""
                        page = pageNumber
                    }
                    ApiCall.callAPI(ApiName.GET_TRANSACTION_LIST, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetPassBookTransactionsResponse) {
                                weeklyTransactionsList.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            weeklyTransactionsList.postValue(
                                Resource.error(
                                    errorResponse.errorMessage.toString(),
                                    errorResponse
                                )
                            )
//                            weeklyTransactionsList.postValue(
//                                Resource.retry(
//                                    "Apki transactions nahi dikha sake. Kripiya dobara koshish kijiye",
//                                    null
//                                )
//                            )
                        }
                    })
                } catch (e: Exception) {
                    weeklyTransactionsList.postValue(
                        Resource.error(
                            e.toString(),
                            null
                        )
                    )
//                    weeklyTransactionsList.postValue(
//                        Resource.retry(
//                            "Apki transactions nahi dikha sake. Kripiya dobara koshish kijiye",
//                            null
//                        )
//                    )
                }
//            }
        }
        //Note : Here you have to call respective API and store response into 'weeklyTransactionsList' live data
//        weeklyTransactionsList.postValue(
//            Resource.success(
//                Gson().fromJson(
//                    "{}",
//                    GetSalaryAdvanceInfoResponse::class.java
//                )
//            )
//        )

    }

    fun getAdvanceWeeklyTransactionsDetailListResponseObs(): LiveData<Resource<Any>> {
        return weeklyTransactionsList
    }
    /**
     * Here we fetch transaction details for particular transaction
     */
    fun getTransactionStatusDetails(giveRetryOption: Boolean = false) {
        if (transactionID == null) {
            BaasUIApp.ctx?.let {
                baseCallBack?.showAlertWithSingleButton(
                    it.getString(R.string.app_name),
                    it.getString(R.string.msg_transaction_id_not_found),
                    it.getString(R.string.ok)
                )
            }
            return
        }

        /*transactionStatusDetailObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'narration':'Money Received','statusIcon':'Pending.svg','extraNotes':'Apka internet nahi chal rha hai. Kripya internet on kariye.','topSalutation':'From','topTitle':'GENRIC_CREDIT / PayU','notes':'','topIcon':'credit-reversal','bottomSalutation':'In your','bottomTitle':'Advance Account','bottomIcon':'salary-account','transactionId':'txn_b4036e571e14406cbe73a1c3511cd9fe','refId':'','amount':2512.50,'dateTime':'14 Nov, 2:00 PM'}",
                    TransactionDetailsResponse::class.java
                )
            )
        )*/

//        if (baseCallBack?.isInternetAvailable(false) == true) {
            viewModelScope.launch {
                transactionStatusDetailObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        id = transactionID
                    }

                    ApiCall.callAPI(
                        ApiName.GET_TRANSACTION_DETAIL,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is TransactionDetailsResponse) {
                                    transactionStatusDetailObs.postValue(
                                        Resource.success(
                                            apiResponse
                                        )
                                    )
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                if (giveRetryOption) {
                                    transactionStatusDetailObs.postValue(
                                        Resource.retry(
                                            errorResponse.errorMessage!!,
                                            null
                                        )
                                    )
                                } else {
                                    transactionStatusDetailObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                                }
                            }
                        })
                } catch (e: Exception) {
                    if (giveRetryOption) {
                        transactionStatusDetailObs.postValue(Resource.retry(e.toString(), null))
                    } else {
                        transactionStatusDetailObs.postValue(Resource.error(e.toString(), null))
                    }
                }
            }
//        }
    }

    fun getTransactionStatusDetailsResponseObs(): LiveData<Resource<Any>> {
        return transactionStatusDetailObs
    }
    fun showTooltip() {
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class AdvanceViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val clickHandler: ClickHandler,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AdvanceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AdvanceViewModel(baseCallBack, clickHandler, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}