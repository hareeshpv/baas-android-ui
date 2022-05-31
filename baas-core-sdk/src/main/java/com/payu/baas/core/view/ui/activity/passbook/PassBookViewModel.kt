@file:JvmSynthetic

package com.payu.baas.core.view.ui.activity.passbook

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.R
import com.payu.baas.core.app.BaasUIApp
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.datasource.PassBookFilterDataSource
import com.payu.baas.core.model.entities.model.TypeModel
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetAccountBalanceDetailsResponse
import com.payu.baas.core.model.responseModels.GetPassBookTransactionsResponse
import com.payu.baas.core.model.responseModels.TransactionDetailsResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.callback.ClickHandler
import com.payu.baas.core.view.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

internal class PassBookViewModel(
    baseCallBack: BaseCallback?,
    clickHandler: ClickHandler?,
    context: Context
) : BaseViewModel(baseCallBack, clickHandler, context) {

    private val accountBalanceDetailsObs = MutableLiveData<Resource<Any>>()
    private val passBookTransactionsListObs = MutableLiveData<Resource<Any>>()
    private val transactionStatusDetailObs = MutableLiveData<Resource<Any>>()

    var selectedStartDate: String = ""
    var selectedEndDate: String = ""
    var selectedTimePeriodType: TypeModel? = null

    /**
     * We have to notify UI for selected Time Period. So to manage this we have
     * use Observable
     */
    var appliedTimePeriodO: ObservableField<String> = ObservableField("")

    var selectedTransactionType: TypeModel? = null
    var selectedAccountType: TypeModel? = null
    var appliedFilterO: ObservableField<String> = ObservableField("")

    //Notify user when Time Period filter or Filter filer is applied
    var clearTransactionListO: MutableLiveData<Boolean> = MutableLiveData(false)

    var pageNumber = 0

    //Here we store transaction id to view transaction details
    var transactionID: String? = null

    init {
        //Default Selected Time Period as 'All Time'
        selectedTimePeriodType = PassBookFilterDataSource.timePeriodTypes[0]
        appliedTimePeriodO.set(PassBookFilterDataSource.timePeriodTypes[0].label)

        //Default Selected Transaction Type as 'All'
        selectedTransactionType = PassBookFilterDataSource.transactionTypes[0]
        //Default Selected Account Type as 'All'
        selectedAccountType = PassBookFilterDataSource.accountTypes[0]

        getAccountBalanceDetails()

    }

    fun onClick(view: View) {
        clickHandler!!.click(view)
    }


    /**
     * Here we get account balance details
     */
    fun getAccountBalanceDetails() {

        /*accountBalanceDetailsObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'prepaidBalance':'1000','advanceBalance':'700','totalBalance':'1700'}",
                    GetAccountBalanceDetailsResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                accountBalanceDetailsObs.postValue(Resource.loading(null))
                try {
                    ApiCall.callAPI(
                        ApiName.GET_ACCOUNT_BALANCE_DETAILS,
                        ApiParams(),
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetAccountBalanceDetailsResponse) {
                                    accountBalanceDetailsObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                accountBalanceDetailsObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
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

    fun getAccountBalanceDetailsResponseObs(): LiveData<Resource<Any>> {
        return accountBalanceDetailsObs
    }

    fun applyTimePeriodSelectionFilter() {
        selectedTimePeriodType?.let {
            if (it.label == PassBookFilterDataSource.CUSTOM_TIME_PERIOD) {
                if (selectedStartDate.isNotEmpty()
                    && selectedEndDate.isNotEmpty()
                ) {
                    appliedTimePeriodO.set("$selectedStartDate - $selectedEndDate")
                }
            } else {
                appliedTimePeriodO.set(it.label)
            }
            clearTransactionListO.value = true
        }
    }


    fun applyFilter() {

        var appliedFilterCount = 0
        selectedTransactionType?.let {
            if (it.label != PassBookFilterDataSource.ALL)
                appliedFilterCount++
        }
        selectedAccountType?.let {
            if (it.label != PassBookFilterDataSource.ALL)
                appliedFilterCount++
        }

        if (appliedFilterCount > 0) {
            appliedFilterO.set(appliedFilterCount.toString())
        } else {
            appliedFilterO.set("")
        }

        clearTransactionListO.value = true

    }


    /**
     * Here we fetch passbook transactions
     */
    fun getPassBookTransactions() {

        if (pageNumber == -1)
            return


        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                passBookTransactionsListObs.postValue(Resource.loading(null))
                try {

                    var strStartDate = ""
                    var strEndDate = ""
                    selectedTimePeriodType?.let { timePeriod ->
                        when (timePeriod.label) {

                            PassBookFilterDataSource.ALL_TIME -> {
                                strStartDate = ""
                                strEndDate = ""
                            }
                            PassBookFilterDataSource.LAST_MONTH -> {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -1)
                                strStartDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(cal.time)
                                strEndDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(Date())
                            }
                            PassBookFilterDataSource.LAST_3_MONTHS -> {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -3)
                                strStartDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(cal.time)
                                strEndDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(Date())
                            }
                            PassBookFilterDataSource.LAST_1_YEAR -> {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -12)
                                strStartDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(cal.time)
                                strEndDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(Date())
                            }
                            PassBookFilterDataSource.CUSTOM_TIME_PERIOD -> {
                                if (selectedStartDate.isNotEmpty()
                                    && selectedEndDate.isNotEmpty()
                                ) {

                                    //Here we our selected Start & End dates format is dd/MM/yyyy format
                                    //and required format for API call is yyyy-MM-dd.
                                    //So need to convert selected dates format
                                    val startDate = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(selectedStartDate)
                                    val endDate = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(selectedEndDate)
                                    startDate?.let {
                                        strStartDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(it)
                                    }
                                    endDate?.let {
                                        strEndDate = SimpleDateFormat(BaaSConstantsUI.dd_MM_yyyy, Locale.getDefault()).format(it)
                                    }
                                }
                            }
                        }
                    }

                    var _accountType = ""
                    selectedAccountType?.let { type ->
                        if (type.code != PassBookFilterDataSource.ALL) {
                            _accountType = type.code
                        }
                    }
                    var _transactionType = ""
                    selectedTransactionType?.let { type ->
                        if (type.code != PassBookFilterDataSource.ALL) {
                            _transactionType = type.code
                        }
                    }

                    val apiParams = ApiParams().apply {
                        startDate = strStartDate
                        endDate = strEndDate
                        accountType = _accountType
                        debitIndicator = _transactionType
                        page = pageNumber
                    }

                    /*if (pageNumber == 0) {
                        passBookTransactionsListObs.postValue(

                            Resource.success(
                                Gson().fromJson(
                                    "{'transactionList':[{'amount':'1000.25','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Satvindar Kaur'},{'amount':'500.50','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Feb 2022 05:00 am','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'ICICI Bank'},{'amount':'250.33','txnStatus':'Failed','debitCreditIndicator':'Credit','date':'10 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Ghanshyaam'},{'amount':'100.00','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'07 Feb 2022 09:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'McDonald’s'},{'amount':'25.90','txnStatus':'Success','debitCreditIndicator':'Debit','date':'04 Feb 2022 01:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'TusharGabani'},{'amount':'1000.00','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'500.66','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'250.98','txnStatus':'Failed','debitCreditIndicator':'Debit','date':'10 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'100.20','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'09 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'}],'page':'last page'}",
                                    GetPassBookTransactionsResponse::class.java
                                )
                            )
                        )
                    } else if (pageNumber == 1) {
                        passBookTransactionsListObs.postValue(
                            Resource.success(
                                Gson().fromJson(
                                    "{'transactionList':[{'amount':'1000.25','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Satvindar Kaur'},{'amount':'500.50','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Feb 2022 05:00 am','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'ICICI Bank'},{'amount':'250.33','txnStatus':'Failed','debitCreditIndicator':'Credit','date':'10 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Ghanshyaam'},{'amount':'100.00','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'07 Feb 2022 09:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'McDonald’s'},{'amount':'25.90','txnStatus':'Success','debitCreditIndicator':'Debit','date':'04 Feb 2022 01:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'TusharGabani'},{'amount':'1000.00','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'500.66','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'250.98','txnStatus':'Failed','debitCreditIndicator':'Debit','date':'10 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'100.20','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'09 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'}],'page':'1'}",
                                    GetPassBookTransactionsResponse::class.java
                                )
                            )
                        )
                    } else if (pageNumber == 2) {
                        passBookTransactionsListObs.postValue(
                            Resource.success(
                                Gson().fromJson(
                                    "{'transactionList':[{'amount':'1000.25','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Satvindar Kaur'},{'amount':'500.50','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Feb 2022 05:00 am','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'ICICI Bank'},{'amount':'250.33','txnStatus':'Failed','debitCreditIndicator':'Credit','date':'10 Feb 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'Ghanshyaam'},{'amount':'100.00','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'07 Feb 2022 09:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'McDonald’s'},{'amount':'25.90','txnStatus':'Success','debitCreditIndicator':'Debit','date':'04 Feb 2022 01:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'TusharGabani'},{'amount':'1000.00','txnStatus':'Success','debitCreditIndicator':'Credit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'500.66','txnStatus':'Success','debitCreditIndicator':'Debit','date':'11 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'250.98','txnStatus':'Failed','debitCreditIndicator':'Debit','date':'10 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'},{'amount':'100.20','txnStatus':'Pending','debitCreditIndicator':'Debit','date':'09 Jan 2022 06:03 pm','txnLogId':'txn_b4036e571e14406cbe73a1c3511cd9fe','description':'xyz'}],'page':'last page'}",
                                    GetPassBookTransactionsResponse::class.java
                                )
                            )
                        )
                    }*/
                    ApiCall.callAPI(ApiName.GET_TRANSACTION_LIST, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {

                            if (apiResponse is GetPassBookTransactionsResponse) {
                                passBookTransactionsListObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            if (errorResponse.errorMessage!!.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                                || errorResponse.errorMessage!!.contains(
                                    BaaSConstantsUI.DEVICE_BINDING_FAILED
                                )
                            )
                                passBookTransactionsListObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            else
                                passBookTransactionsListObs.postValue(
                                    Resource.retry(
                                        "Apki transactions nahi dikha sake. Kripiya dobara koshish kijiye",
                                        errorResponse
                                    )
                                )
                        }
                    })
                } catch (e: Exception) {
                    passBookTransactionsListObs.postValue(
                        Resource.retry(
                            "Apki transactions nahi dikha sake. Kripiya dobara koshish kijiye",
                            null
                        )
                    )
                }
            }
        }
    }

    fun getPassBookTransactionListResponseObs(): LiveData<Resource<Any>> {
        return passBookTransactionsListObs
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

        if (baseCallBack?.isInternetAvailable(true) == true) {
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
                                    if (errorResponse.errorMessage!!.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                                        || errorResponse.errorMessage!!.contains(
                                            BaaSConstantsUI.DEVICE_BINDING_FAILED
                                        )
                                    )
                                        transactionStatusDetailObs.postValue(
                                            Resource.error(
                                                errorResponse.errorMessage!!,
                                                errorResponse
                                            )
                                        )
                                    else
                                        transactionStatusDetailObs.postValue(
                                            Resource.retry(
                                                errorResponse.errorMessage!!,
                                                errorResponse
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
        }
    }

    fun getTransactionStatusDetailsResponseObs(): LiveData<Resource<Any>> {
        return transactionStatusDetailObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class PassBookViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val clickHandler: ClickHandler?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(PassBookViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PassBookViewModel(baseCallBack, clickHandler, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}