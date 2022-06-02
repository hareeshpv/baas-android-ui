package com.payu.baas.coreUI.view.ui.activity.kyc

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityCompleteKycactivityBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetApplicationIdResultsResponse
import com.payu.baas.core.model.responseModels.GetUserStateResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.enums.LastCall
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.panEmpVerification.PANEmployeeDetailActivity
import java.util.*

class CompleteKYCActivity : BaseActivity() {
    lateinit var binding: ActivityCompleteKycactivityBinding
    private lateinit var viewModel: CompleteKycViewModel

    //    lateinit var completeKYCBaseCallback: BaseCallback
    var mobileNumber: String? = null
    var lastCallFrom = LastCall.NONE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_kycactivity)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
    }

    private fun setupObserver() {
        viewModel.getUserStateResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_USER_STATE)
        })
        viewModel.getApplicationResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_APPLICATION_ID)
        })
//        viewModel.getKarzaTokenResponseObs().observe(this, {
//            parseResponse(it, ApiName.KARZA_TOKEN)
//        })
//        viewModel.getTransactionIdResponseObs().observe(this, {
//            parseResponse(it, ApiName.KARZA_ADD_NEW_CUSTOMER)
//        })
//        viewModel.getKarzaUserTokenResponseObs().observe(this, {
//            parseResponse(it, ApiName.KARZA_GENERATE_CUSTOMER_TOKEN)
//        })
//        viewModel.getAccessTokenResponseObs().observe(this, {
//            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
//        })
//        viewModel.getKarzaKeyResponseObs().observe(this, {
//            parseResponse(it, ApiName.KARZA_KEY)
//        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_USER_STATE -> {
                        it.data?.let {
                            if (it is GetUserStateResponse) {
                                if (it.message.isNullOrEmpty()) { // right now we are not having logout so getting this as not clearing data in testing mode after crash or anything.
                                    viewModel.showViewsAsPerUserState(UserState.MOBILE_NOT_SUBMITTED.getValue()) //so added this check
                                } else
//                                    if (SessionManagerUI.getInstance(this).userPanDetails.isNullOrEmpty()
//                                        && !isUserOnBoarded(it.message!!)
                                    if (areKycChecksPassed(it.message!!)
                                        || areKycChecksFailedOrNotVerified(it.message!!)
                                        || isUserOnBoarded(it.message!!)
                                    ) {
                                        viewModel.showViewsAsPerUserState(it.message!!)
                                    } else
                                        viewModel.showViewsAsPerUserState(UserState.MOBILE_VERIFIED.getValue())

                            }
                        }
                    }
                    ApiName.GET_APPLICATION_ID -> {
                        it.data?.let {
                            if (it is GetApplicationIdResultsResponse) {
                                SessionManager.getInstance(this).karzaUserToken =
                                    (it).applicationId!!
                                callNextScreen(
                                    Intent(
                                        this,
                                        KarzaActivity::class.java
                                    ), null
                                )
                            }
                        }
                    }
//                    ApiName.KARZA_GENERATE_CUSTOMER_TOKEN -> {
//                        callNextScreen(
//                            Intent(
//                                this,
//                                KarzaActivity::class.java
//                            ), null
//                        )
//                    }
                }

            }
            Status.LOADING -> {
                showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
                hideProgress()
                var errorResponse = it.data as ErrorResponse
                if (!(it.message.isNullOrEmpty()) && (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED))
                ) {
                    viewModel.reGenerateAccessToken(true)
                } else if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    var msg = ""
                    try {
                        var errorUiRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                            it.message,
                            com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                        ) as com.payu.baas.coreUI.model.ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.llParent,
                        msg,
                        false
                    )
                }
//                if (it.message!!.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
//                    || it.message!!.contains(
//                        BaaSConstantsUI.DEVICE_BINDING_FAILED
//                    )
//                )
//                    viewModel.reGenerateAccessToken(true)
//                else
//                    try {
//                        when (apiName) {
//                            ApiName.GET_USER_STATE -> {
//                                val errorRes = JsonUtil.toObject(
//                                    it.message,
//                                    GetUserStateResponse::class.java
//                                ) as GetUserStateResponse
//                                if (!errorRes.systemMessage.isNullOrEmpty() &&
//                                    errorRes.systemMessage.equals(
//                                        BaaSConstantsUI.INVALID_ACCESS_TOKEN
//                                    )
//                                ) {
//                                    lastCallFrom = LastCall.USER_STATE
//                                    viewModel.reGenerateAccessToken(true)
//                                }
//                            }
//                            ApiName.GET_APPLICATION_ID -> {
//                                val errorRes = JsonUtil.toObject(
//                                    it.message,
//                                    GetApplicationIdResultsResponse::class.java
//                                ) as GetApplicationIdResultsResponse
//                                if (!errorRes.systemMessage.isNullOrEmpty() && errorRes.systemMessage.equals(
//                                        BaaSConstantsUI.INVALID_ACCESS_TOKEN
//                                    )
//                                ) {
//                                    lastCallFrom = LastCall.APPLICATION_ID
//                                    viewModel.reGenerateAccessToken(true)
//                                }
//                            }
////                            ApiName.KARZA_KEY -> {
////                                val errorRes = JsonUtil.toObject(
////                                    it.message,
////                                    GetKarzaKeyResponse::class.java
////                                ) as GetKarzaKeyResponse
////                                if (!errorRes.systemMessage.isNullOrEmpty() && errorRes.systemMessage.equals(
////                                        BaaSConstantsUI.INVALID_ACCESS_TOKEN
////                                    )
////                                ) {
////                                    lastCallFrom = LastCall.KARZA_KEY
////                                    viewModel.reGenerateAccessToken(true)
////                                }
////                            }
//                        }
//                    } catch (e: Exception) {
//                        viewModel.baseCallBack?.showToastMessage(it.message)
//                    }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_USER_STATE -> {
                        viewModel.getUserState()
                    }
//                    ApiName.KARZA_KEY -> {
//                        viewModel.getKarzaKey()
//                    }
//                    ApiName.KARZA_TOKEN -> {
//                        viewModel.generateKarzaToken()
//                    }
                    ApiName.GET_APPLICATION_ID -> {
                        viewModel.getApplicationId()
                    }
//                    ApiName.KARZA_ADD_NEW_CUSTOMER -> {
//                        viewModel.generateNewTranasactionId()
//                    }
//                    ApiName.KARZA_GENERATE_CUSTOMER_TOKEN -> {
//                        viewModel.getKarazUserToken()
//                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            CompleteKycViewModel.CompleteKycViewModelFactory(this, this)
        )[CompleteKycViewModel::class.java]
        binding.viewModel = viewModel
    }

    fun startVerification(view: View) {
        when (view) {
            binding.parentStart.parentStatus -> {
                callNextScreen(Intent(this, PANEmployeeDetailActivity::class.java), null)
            }
            binding.parentContinueCardDelivery.parentStatus -> {
                callNextScreen(
                    Intent(this, CardDeliveryAddressDetailActivity::class.java),
                    null
                )
            }
            binding.parentContinueSelfie.parentStatus,
            binding.parentContinueAadhaar.parentStatus,
            binding.parentContinueSubmit.parentStatus -> {
//                callKarzaSdk(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(this)
        val userState = SessionManagerUI.getInstance(this).userStatusCode
        if (userState.isNullOrEmpty() || userState.equals(UserState.MOBILE_VERIFIED.getValue())) {
            viewModel.getUserState()
        } else {
            viewModel.showViewsAsPerUserState(userState)
        }
    }

    fun openPreviousScreen(view: View) {
        finish()
        viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_SET_PASSCODE,
            BaaSConstantsUI.CL_USER_SET_PASSCODE_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )
    }

    fun areKycChecksPassed(uState: String): Boolean {
        if ((uState.equals(UserState.KYC_CHECKS_PASSED.getValue()))
        )
            return true
        return false
    }
    fun areKycChecksFailedOrNotVerified(uState: String): Boolean {
        if ((uState.equals(UserState.KYC_CHECKS_FAILED.getValue()))
                    || (uState.equals(UserState.ONBOARDING_FAILED_1.getValue()))
                    || (uState.equals(UserState.ONBOARDING_FAILED_2.getValue()))
                    || (uState.equals(UserState.KYC_RESULT_SAVED.getValue()))
                    || (uState.equals(UserState.AADHARXML_SAVED.getValue()))
                    || (uState.equals(UserState.SELFIE_SAVED.getValue()))
                    || (uState.equals(UserState.LAT_LONG_IP_SAVED.getValue()))
        )
            return true
        return false
    }
    fun isUserOnBoarded(uState: String): Boolean {
        if ((uState.equals(UserState.ONBOARDING_SUCCESS.getValue())
                    || uState.equals(UserState.ONBOARDED.getValue())
                    || uState.equals(UserState.PASSCODE_SET.getValue()))
        ) {
            if(uState.equals(UserState.PASSCODE_SET.getValue()))
            viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(BaaSConstantsUI.CL_USER_VERIFY_OTP,
                BaaSConstantsUI.CL_USER_OTP_VERIFICATION_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date())
            return true
        }
        viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION,
            BaaSConstantsUI.CL_USER_MOBILE_VERIFICATION_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date()
        )
        return false
    }
}