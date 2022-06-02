package com.payu.baas.coreUI.view.ui.activity.panvalidate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.PanValidateResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_VERIFY_PAN_BUTTON_CLICK
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import com.payu.baas.coreUI.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch
import java.util.*

class VerifyPanViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val verifyPANResponseObs = MutableLiveData<Resource<Any>>()
    val strVerifyPanNumber: ObservableField<String> = ObservableField("")

    //    lateinit var validatePanBaseCallback: BaseCallbacks
    var mobileNo_: String? = null
    var imeiNumber: String? = null

    init {
        mobileNo_ = SessionManagerUI.getInstance(context).userMobileNumber
    }

    fun verifyPAN() {
        when (CL_USER_VERIFY_PAN_BUTTON_CLICK) {
            "1" -> {
                baseCallBack!!.cleverTapUserOnBoardingEvent(
                    BaaSConstantsUI.CL_USER_VERIFY_PAN_FROM_PROFILE,
                    BaaSConstantsUI.CL_USER_PAN_VERIFY_PASSCODE_RESET_PROFILE_EVENT_ID,
                    SessionManager.getInstance(context).accessToken,
                    imeiNumber,
                    mobileNo_,
                    Date()
                )
            }
            "2" -> {
                baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_BLOCK_CARD,
                    BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_BLOCK_CARD_EVENT_ID,
                    SessionManager.getInstance(context).accessToken,
                    imeiNumber,
                    mobileNo_,
                    Date(),
                    "",
                    ""
                )

            }
            "3" -> {
                baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_TRANSACTION_LIMIT,
                    BaaSConstantsUI.CL_USER_PAN_VERIFY_FORGOT_PASSCODE_TRANSACTION_LIMIT_EVENT_ID,
                    SessionManager.getInstance(context).accessToken,
                    imeiNumber,
                    mobileNo_,
                    Date(),
                    "",
                    ""
                )
            }
            //From Money Transfer Module
            "4" -> {
                baseCallBack!!.cleverTapUserMoneyTransferEvent(
                    BaaSConstantsUI.CL_PAN_VERIFY_FORGOT_PASSCODE_MONEY_TRANSFER,
                    BaaSConstantsUI.CL_PAN_VERIFY_FORGOT_PASSCODE_MONEY_TRANSFER_EVENT_ID,
                    SessionManager.getInstance(context).accessToken,
                    imeiNumber,
                    mobileNo_,
                    Date(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            }
            else -> {
                baseCallBack!!.cleverTapUserOnBoardingEvent(
                    BaaSConstantsUI.CL_USER_PAN_VERIFY,
                    BaaSConstantsUI.CL_USER_PAN_VERIFICATION_EVENT_ID,
                    SessionManager.getInstance(context).accessToken,
                    imeiNumber,
                    mobileNo_,
                    Date()
                )
            }
        }
        if (!baseCallBack!!.isInternetAvailable(false)) {
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack!!.callNextScreen(Intent(context, ErrorActivity::class.java), mBundle)
        } else {
            viewModelScope.launch {
                verifyPANResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        panNumber = strVerifyPanNumber.get().toString().trim()
                    }
                    ApiCall.callAPI(ApiName.PAN_VALIDATE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is PanValidateResponse) {
                                verifyPANResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            verifyPANResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    verifyPANResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }

        }
    }

    fun getVerifyPANResponseObs(): LiveData<Resource<Any>> {
        return verifyPANResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class VerifyPanViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(VerifyPanViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VerifyPanViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}