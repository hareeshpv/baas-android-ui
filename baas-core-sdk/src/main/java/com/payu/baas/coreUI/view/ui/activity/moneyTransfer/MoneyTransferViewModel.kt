package com.payu.baas.coreUI.view.ui.activity.moneyTransfer

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.app.BaasUIApp
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.model.BeneficiaryModel
import com.payu.baas.core.model.model.IFSCDetailsModel
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.callback.ClickHandler
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray

class MoneyTransferViewModel(
    baseCallBack: BaseCallback?,
    clickHandler: ClickHandler?,
    context: Context
) : BaseViewModel(baseCallBack, clickHandler, context) {

    private val karzaKeyResponseObs = MutableLiveData<Resource<Any>>()
    private val verifyIFSCCodeObs = MutableLiveData<Resource<Any>>()
    private val createUserBeneficiaryObs = MutableLiveData<Resource<Any>>()
    private val transactionChargesObs = MutableLiveData<Resource<Any>>()
    private val preValidateTransactionObs = MutableLiveData<Resource<Any>>()
    private val transferAmountObs = MutableLiveData<Resource<Any>>()
    private val recentBeneficiaryListObs = MutableLiveData<Resource<Any>>()
    private val allBeneficiaryListObs = MutableLiveData<Resource<Any>>()
    private val deleteBeneficiariesObs = MutableLiveData<Resource<Any>>()
    private val undoDeleteBeneficiariesObs = MutableLiveData<Resource<Any>>()

    val strAccountNumber: ObservableField<String> = ObservableField("")
    val strReEnteredAccountNumber: ObservableField<String> = ObservableField("")
    val accountNumberVerified: ObservableField<Boolean> = ObservableField(false)
    val strIFSCCode: ObservableField<String> = ObservableField("")
    val strAccountHolderName: ObservableField<String> = ObservableField("")
    val ifscCodeVerifiedSuccessfully: ObservableField<Boolean> = ObservableField(false)
    val showProgressBar: ObservableField<Boolean> = ObservableField(false)
    val errorMessageForEnterAccountNumber: ObservableField<String> = ObservableField("")
    val errorMessageForReEnterAccountNumber: ObservableField<String> = ObservableField("")
    val errorMessageForIFSCCode: ObservableField<String> = ObservableField("")
    val errorMessageForAccountHolderName: ObservableField<String> = ObservableField("")

    val strEnteredAmount: ObservableField<String> = ObservableField("")
    val strNote: ObservableField<String> = ObservableField("")

    var verifiedIFSCDetails: IFSCDetailsModel? = null
    var addedBeneficiary: BeneficiaryModel? = null
    var loadingBeneficiaryList = false
    var pageNumber = 0
    var beneficiaryList = arrayListOf<BeneficiaryModel>()
    var selectedBeneficiariesForDelete = arrayListOf<BeneficiaryModel>()

    fun onClick(view: View) {
        clickHandler?.click(view)
    }

    fun onAccountNumberChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        strAccountNumber.set(s.toString())
        if (s.length in 1..8
            || s.length > 18
        ) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForEnterAccountNumber.set(it.getString(R.string.msg_enter_valid_account_number))
            }
        } else {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForEnterAccountNumber.set("")
            }
        }
        strReEnteredAccountNumber.set("")
        errorMessageForReEnterAccountNumber.set("")
        accountNumberVerified.set(false)
    }

    fun onReEnteredAccountNumberChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        strReEnteredAccountNumber.set(s.toString())

        val accountNumber = strAccountNumber.get().toString().trim()
        if (accountNumber.length in 9..18) {
            if ((strAccountNumber.get().toString().trim().isNotEmpty())
                && (accountNumber == strReEnteredAccountNumber.get().toString().trim())
            ) {
                accountNumberVerified.set(true)
                com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                    errorMessageForReEnterAccountNumber.set("")
                }
            } else {
                accountNumberVerified.set(false)
                com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                    errorMessageForReEnterAccountNumber.set(it.getString(R.string.msg_account_number_not_matched))
                }
            }
        } else {
            accountNumberVerified.set(false)
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForReEnterAccountNumber.set("")
            }
        }
    }

    /**
     * We don't have to show TICK icon when user type/change IFSC Code
     */
    fun onIFSCCodeChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        strIFSCCode.set(s.toString())
        ifscCodeVerifiedSuccessfully.set(false)
        verifiedIFSCDetails = null
    }

    fun onAccountHolderChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        strAccountHolderName.set(s.toString())
        if (s.toString().isNotEmpty()) {
            errorMessageForAccountHolderName.set("")
        }
    }

    /**
     * Here we verify IFSC Code
     */
    fun verifyIFSCCode() {
        if (baseCallBack?.isInternetAvailable(true) == true) {

            val apiParams = ApiParams().apply {
                ifsc = strIFSCCode.get().toString().trim()
            }

            viewModelScope.launch {
                showProgressBar.set(true)
                try {
                    ApiCall.callAPI(
                        ApiName.IFSC_CODE,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                showProgressBar.set(false)
                                if (apiResponse is VerifyIFSCResponse) {
                                    verifyIFSCCodeObs.postValue(
                                        Resource.success(
                                            apiResponse
                                        )
                                    )
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                showProgressBar.set(false)
                                verifyIFSCCodeObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    showProgressBar.set(false)
                    createUserBeneficiaryObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getVerifyIFSCCodeResponseObs(): LiveData<Resource<Any>> {
        return verifyIFSCCodeObs
    }


    /**
     *  Here we check whether entered account numbers are matched or not
     */
    fun isAccountNumberVerified() {
        errorMessageForReEnterAccountNumber.set("")
        val accountNumber = strAccountNumber.get().toString().trim()
        if (accountNumber.length in 9..18) {
            if ((strReEnteredAccountNumber.get().toString().trim()
                    .isEmpty())
                && accountNumberVerified.get() == false
            ) {
                com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                    errorMessageForReEnterAccountNumber.set(it.getString(R.string.msg_account_number_not_matched))
                }
            } else if ((strReEnteredAccountNumber.get().toString().trim()
                    .isNotEmpty())
                && accountNumberVerified.get() == false
            ) {
                com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                    errorMessageForReEnterAccountNumber.set(it.getString(R.string.msg_account_number_not_matched))
                }
            }
        }
    }

    /**
     * Here we create user beneficiary
     */
    fun createUserBeneficiary() {

        //Here we didn't check conditions for Account Number because we enable button only when
        //Both Account Number verified successfully.
        errorMessageForIFSCCode.set("")
        errorMessageForAccountHolderName.set("")

        if (strIFSCCode.get().toString().trim().isEmpty()
            || strIFSCCode.get().toString().trim().length != 11
        ) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForIFSCCode.set(it.getString(R.string.msg_enter_valid_ifsc_code))
            }
        } else if (ifscCodeVerifiedSuccessfully.get() == false) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForIFSCCode.set(it.getString(R.string.msg_entered_ifsc_code_invalid_or_not_verified))
            }
        }

        if (strAccountHolderName.get().toString().trim().isEmpty()) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                errorMessageForAccountHolderName.set(it.getString(R.string.msg_enter_account_holder_name))
            }
        }


        if (errorMessageForIFSCCode.get().toString().isEmpty()
            && errorMessageForAccountHolderName.get().toString().isEmpty()
        ) {

            /*createUserBeneficiaryObs.postValue(
                Resource.success(
                    Gson().fromJson(
                        "{'msg':'SUCCESS','userBeneficiary':{'beneficiaryId':'4ba812c9-934d-4750-957d-1ac07b5af31b','beneficiaryName':'AjaySharma','ifsc':'HDFC0004990','last4Digits':'4936','addedOn':'2021-12-29T12:58:23.902+00:00'}}",
                        CreateBeneficiaryResponse::class.java
                    )
                )
            )*/

            if (baseCallBack?.isInternetAvailable(true) == true) {

                val apiParams = ApiParams().apply {
                    beneficiaryName = UtilsUI.toCamelCase(strAccountHolderName.get().toString().trim())
                    accountNumber = strAccountNumber.get().toString().trim()
                    ifsc = strIFSCCode.get().toString().trim()
                }

                viewModelScope.launch {
                    createUserBeneficiaryObs.postValue(Resource.loading(null))
                    try {
                        ApiCall.callAPI(
                            ApiName.ADD_BENEFICIARY,
                            apiParams,
                            object : ApiHelperUI {
                                override fun onSuccess(apiResponse: ApiResponse) {

                                    if (apiResponse is CreateBeneficiaryResponse) {
                                        createUserBeneficiaryObs.postValue(
                                            Resource.success(
                                                apiResponse
                                            )
                                        )
                                    }
                                }

                                override fun onError(errorResponse: ErrorResponse) {
                                    createUserBeneficiaryObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                                }
                            })
                    } catch (e: Exception) {
                        createUserBeneficiaryObs.postValue(Resource.error(e.toString(), null))
                    }
                }
            }
        }
    }

    fun getCreateUserBeneficiaryResponseObs(): LiveData<Resource<Any>> {
        return createUserBeneficiaryObs
    }

    /**
     * Here we get transaction charges
     */
    fun getTransactionCharges() {

        /*transactionChargesObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'charges':10.0}",
                    GetTransactionChargesResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {

            viewModelScope.launch {
                transactionChargesObs.postValue(Resource.loading(null))
                try {
                    ApiCall.callAPI(
                        ApiName.GET_TRANSACTION_CHARGES,
                        ApiParams(),
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetTransactionChargesResponse) {
                                    transactionChargesObs.postValue(
                                        Resource.success(
                                            apiResponse
                                        )
                                    )
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                if (errorResponse.errorMessage!!.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                                    || errorResponse.errorMessage!!.contains(
                                        BaaSConstantsUI.DEVICE_BINDING_FAILED
                                    )
                                )
                                    transactionChargesObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                                else
                                    transactionChargesObs.postValue(
                                        Resource.retry(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                            }
                        })
                } catch (e: Exception) {
                    transactionChargesObs.postValue(Resource.retry(e.toString(), null))
                }
            }
        }
    }

    fun getTransactionChargesResponseObs(): LiveData<Resource<Any>> {
        return transactionChargesObs
    }


    /**
     * Here we get details related to entered amount
     */
    fun preValidateTransaction() {

        if (strEnteredAmount.get().toString().trim().isEmpty()) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                baseCallBack?.showAlertWithSingleButton(
                    it.getString(R.string.app_name),
                    it.getString(R.string.msg_enter_amount),
                    it.getString(R.string.ok)
                )
            }
        } else if (strEnteredAmount.get().toString().trim().toDouble() <= 0) {
            com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
                baseCallBack?.showAlertWithSingleButton(
                    it.getString(R.string.app_name),
                    it.getString(R.string.msg_enter_valid_amount),
                    it.getString(R.string.ok)
                )
            }
        } else {

            /*preValidateTransactionObs.postValue(
                Resource.success(
                    Gson().fromJson(
                        "{'maxAmountTransfer':59.0,'chargedAmount':10.0,'advanceChargeAmount':0.0}",
                        PrevalidateTransactionResponse::class.java
                    )
                )
            )*/


            if (baseCallBack?.isInternetAvailable(true) == true) {

                val apiParams = ApiParams().apply {
                    txnAmount = strEnteredAmount.get().toString().trim()
                }

                viewModelScope.launch {
                    preValidateTransactionObs.postValue(Resource.loading(null))
                    try {
                        ApiCall.callAPI(
                            ApiName.PREVALIDATE_TRANSACTION,
                            apiParams,
                            object : ApiHelperUI {
                                override fun onSuccess(apiResponse: ApiResponse) {

                                    if (apiResponse is PrevalidateTransactionResponse) {
                                        preValidateTransactionObs.postValue(
                                            Resource.success(
                                                apiResponse
                                            )
                                        )
                                    }
                                }

                                override fun onError(errorResponse: ErrorResponse) {
                                    preValidateTransactionObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                                }
                            })
                    } catch (e: Exception) {
                        preValidateTransactionObs.postValue(Resource.error(e.toString(), null))
                    }
                }
            }
        }
    }

    fun getPreValidateTransactionResponseObs(): LiveData<Resource<Any>> {
        return preValidateTransactionObs
    }

    /**
     * Here we transfer amount
     */
    fun transferAmount() {
        if (addedBeneficiary == null) {
            return
        }

        /*transferAmountObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'txn_logs':[{'txn_log_id':'txn_b3c8475d76b346b5b47d62a0fe329bc5'}]}",
                    BeneficiaryBankTransferResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {

            val apiParams = ApiParams().apply {
                amount = strEnteredAmount.get().toString().trim().toDouble()
                remarks = strNote.get().toString().trim()
                beneficiaryId = addedBeneficiary?.beneficiaryId
            }

            viewModelScope.launch {
                transferAmountObs.postValue(Resource.loading(null))
                try {
                    ApiCall.callAPI(
                        ApiName.BENEFICIARY_BANK_TRANSFER,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is BeneficiaryBankTransferResponse) {
                                    transferAmountObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                transferAmountObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    transferAmountObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getTransferAmountResponseObs(): LiveData<Resource<Any>> {
        return transferAmountObs
    }


    /**
     * Here we get Recent Beneficiary List
     */
    fun getRecentBeneficiaryList() {
        /*recentBeneficiaryListObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'size':0,'page':10,'sort':'beneficiaryName','result':[{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c4','beneficiaryName':'AjaySharma','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560126','beneficiaryName':'AjaySharma','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'}]}",
                    GetBeneficiaryResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                recentBeneficiaryListObs.postValue(Resource.loading(null))
                try {

                    val apiParams = ApiParams().apply {
                        page = 0
                        size = 3 //Have to show Last 3 most recent payees here
                        sort = "beneficiaryName"
                    }

                    ApiCall.callAPI(
                        ApiName.GET_RECENT_BENEFICIARY,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetRecentBeneficiaryResponse) {
                                    recentBeneficiaryListObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                recentBeneficiaryListObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    recentBeneficiaryListObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getRecentBeneficiaryListResponseObs(): LiveData<Resource<Any>> {
        return recentBeneficiaryListObs
    }

    /**
     * Here we get All Beneficiary List - Pagination
     */
    fun getAllBeneficiaryList() {
        /*allBeneficiaryListObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'size':0,'page':15,'sort':'beneficiaryName','result':[{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c1','beneficiaryName':'AjaySharma_1','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf56012','beneficiaryName':'AjaySharma_2','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c3','beneficiaryName':'AjaySharma_3','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560124','beneficiaryName':'AjaySharma_4','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c5','beneficiaryName':'AjaySharma_5','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560126','beneficiaryName':'AjaySharma_6','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c7','beneficiaryName':'AjaySharma_7','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560128','beneficiaryName':'AjaySharma_8','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f343164c9','beneficiaryName':'AjaySharma_9','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560110','beneficiaryName':'AjaySharma_10','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f34316411','beneficiaryName':'AjaySharma_11','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560112','beneficiaryName':'AjaySharma_12','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'1570d04a-1d6b-46f6-9683-c11f34316413','beneficiaryName':'AjaySharma_13','last4Digits':'4190','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560114','beneficiaryName':'AjaySharma_14','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'},{'beneficiaryId':'365a7a2f-b20c-4a51-811f-fa0dcf560115','beneficiaryName':'AjaySharma_15','last4Digits':'4136','icon':'HDFC.svg','bankName':'HDFC'}]}",
                    GetBeneficiaryResponse::class.java
                )
            )
        )*/

        if (pageNumber == -1)
            return

        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                allBeneficiaryListObs.postValue(Resource.loading(null))
                try {

                    val apiParams = ApiParams().apply {
                        page = pageNumber
                        size = 10
                        sort = "beneficiaryName"
                    }

                    ApiCall.callAPI(
                        ApiName.GET_BENEFICIARY,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is GetBeneficiaryResponse) {
                                    allBeneficiaryListObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                allBeneficiaryListObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    allBeneficiaryListObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getAllBeneficiaryListResponseObs(): LiveData<Resource<Any>> {
        return allBeneficiaryListObs
    }

    /**
     * Here we delete selected beneficiary
     */
    fun deleteBeneficiaries(selectedBeneficiariesIds: JSONArray) {
        /*deleteBeneficiariesObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'msg':'Deleted User Successfully'}",
                    DeleteBeneficiaryResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {

            val apiParams = ApiParams().apply {
                userBeneFiciaryIds =
                    selectedBeneficiariesIds
            }

            viewModelScope.launch {
                deleteBeneficiariesObs.postValue(Resource.loading(null))
                try {
                    ApiCall.callAPI(
                        ApiName.DELETE_BENEFICIARY,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is DeleteBeneficiaryResponse) {
                                    deleteBeneficiariesObs.postValue(Resource.success(apiResponse))
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                deleteBeneficiariesObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    deleteBeneficiariesObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getDeleteBeneficiariesResponseObs(): LiveData<Resource<Any>> {
        return deleteBeneficiariesObs
    }


    /**
     * Here we undo deleted beneficiary
     */
    fun undoDeletedBeneficiaries(beneficiariesIds: JSONArray) {
        /*undoDeleteBeneficiariesObs.postValue(
            Resource.success(
                Gson().fromJson(
                    "{'msg':'Undo User Successfully'}",
                    UndoDeletedBeneficiaryResponse::class.java
                )
            )
        )*/

        if (baseCallBack?.isInternetAvailable(true) == true) {

            val apiParams = ApiParams().apply {
                userBeneFiciaryIds =
                    beneficiariesIds
            }

            viewModelScope.launch {
                undoDeleteBeneficiariesObs.postValue(Resource.loading(null))
                try {
                    ApiCall.callAPI(
                        ApiName.UNDO_DELETED_BENEFICIARY,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {

                                if (apiResponse is UndoDeletedBeneficiaryResponse) {
                                    undoDeleteBeneficiariesObs.postValue(
                                        Resource.success(
                                            apiResponse
                                        )
                                    )
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
                                undoDeleteBeneficiariesObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    undoDeleteBeneficiariesObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getUndoDeletedBeneficiariesResponseObs(): LiveData<Resource<Any>> {
        return undoDeleteBeneficiariesObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class MoneyTransferViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val clickHandler: ClickHandler?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(MoneyTransferViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MoneyTransferViewModel(baseCallBack, clickHandler, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}