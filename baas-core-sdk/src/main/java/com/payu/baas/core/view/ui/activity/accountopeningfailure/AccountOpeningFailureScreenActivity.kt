package com.payu.baas.core.view.ui.activity.accountopeningfailure

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityAccountOpeningFailureScreenBinding
import com.payu.baas.core.model.entities.model.PanDetailsModelUI
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.JsonUtil
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.reviewAndSubmit.ReviewAndSubmitViewModel
import java.util.*

class AccountOpeningFailureScreenActivity : BaseActivity() {
    lateinit var binding: ActivityAccountOpeningFailureScreenBinding

    //    lateinit var accountOpeningFailureBaseCallback: BaseCallback
    private lateinit var viewModel: ReviewAndSubmitViewModel
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_account_opening_failure_screen)
        activity = this
//        accountOpeningFailureBaseCallback = this

        setupViewModel()
        setupUI()
//        setupObserver()
    }

    private fun setupUI() {
        val extra = intent.extras
        if (extra != null) {
            val message = extra.getString(BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY)
            binding.tvErrorMessageForAadhar.text = message

        }
        var strPanDetails =
            com.payu.baas.core.model.storage.SessionManagerUI.getInstance(this).userPanDetails
        if (!strPanDetails.isNullOrEmpty()) {
            var panDetails =
                JsonUtil.toObject(strPanDetails, PanDetailsModelUI::class.java) as PanDetailsModelUI
            binding.tvName.setText(panDetails.firstName+ " " + panDetails.lastName)
        }

        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
//        if (BaaSConstantsUI.CL_USER_NOT_VERIFIED == "1") {
            viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                BaaSConstantsUI.CL_USER_ACCOUNT_OPENING_FAILED,
                BaaSConstantsUI.CL_USER_ACCOUNT_OPENING_FAILED_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date()
            )
//        } else {
//            viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
//                BaaSConstantsUI.CL_USER_KARZA_VERIFICATION_FAILED,
//                BaaSConstantsUI.CL_USER_KARZA_VERIFICATION_FAILED_EVENT_ID,
//                SessionManager.getInstance(this).accessToken,
//                imei,
//                mobileNumber,
//                Date()
//            )
//        }

    }

    fun closeThisScreen(view: View) {
        finish()
    }

    fun re_try_again(view: View) {

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReviewAndSubmitViewModel.ReviewViewModelFactory(this, this)
        )[ReviewAndSubmitViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei
        viewModel.mobileNo_ = mobileNumber
//        viewModel.termsConditionBaseCallback = accountOpeningFailureBaseCallback

    }

//    private fun setupObserver() {
//        viewModel.getKycSelfieResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_SELFIE)
//        })
//        viewModel.getKycAadhaarResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_AADHAR)
//        })
//        viewModel.getKycResultsResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_RESULTS)
//        })
//        viewModel.getVerifyKycResponseObs().observe(this, {
//            parseResponse(it, ApiName.VERIFY_KYC_RESULTS)
//        })
//        viewModel.getKycLocationResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_LOCATION)
//        })
//        viewModel.getOnBoardResponseObs().observe(this, {
//            parseResponse(it, ApiName.SAVE_ADDRESS)
//        })
//        viewModel.getAccessTokenResponseObs().observe(this, {
//            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
//        })
//    }

//    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
//        when (it?.status) {
//            Status.SUCCESS -> {
//                when (apiName) {
//                    ApiName.KYC_SELFIE, ApiName.KYC_AADHAR,
//                    ApiName.KYC_RESULTS, ApiName.KYC_LOCATION -> {
//                        viewModel.setProgressForApi(viewModel.progressCount + 15)
//                    }
//                    ApiName.VERIFY_KYC_RESULTS -> {
//                        it.data?.let {
//                            if (it is VerifyKYCResultsResponse) {
//                                var message = it.message
//                                if (message.equals("YES")) {
////                                    viewModel.onBoardUser()
//                                    viewModel.setProgressForApi(viewModel.progressCount + 15)
//                                } else {
//                                    // show erro message
//                                    viewModel.hideCustomProgress()
//                                    var bundle = Bundle()
//                                    bundle.putString(
//                                        BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY,
//                                        message
//                                    )
//                                    viewModel.baseCallBack?.callNextScreen(
//                                        Intent(
//                                            this,
//                                            AccountOpeningFailureScreenActivity::class.java
//                                        ), bundle
//                                    )
//                                    finish()
//                                }
//                            }
//                        }
//                    }
//                    ApiName.SAVE_ADDRESS -> {
//                        it.data?.let {
//                            if (it is SaveAddressResponse) {
//                                var message = it.message
//                                if (message!!.equals("Details added and user onboarded successfully")
//                                    || message!!.contains("successfully")
//                                ) {
//                                    viewModel.setProgressForApi(viewModel.progressCount + 15)
//                                } else {
//                                    var bundle = Bundle()
//                                    bundle.putString(
//                                        BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY,
//                                        message
//                                    )
//                                    viewModel.baseCallBack?.callNextScreen(
//                                        Intent(
//                                            this,
//                                            AccountOpeningFailureScreenActivity::class.java
//                                        ), bundle
//                                    )
//                                    finish()
//                                }
//                            }
//                        }
//                    }
//                    ApiName.GET_CLIENT_TOKEN -> {
//                        it.data?.let {
//                            if (it is GetClientTokenResponse) {
//                                if (!it.accessToken.isNullOrEmpty()) {
//                                    viewModel.reTry()
//                                }
//                            }
//                        }
//
//                    }
//                }
//
////                hideProgress()
////                when (apiName) {
////                    ApiName.SAVE_ADDRESS -> {
////                        it.data?.let {
////                            if (it is SaveAddressResponse) {
////                                if (it.message.equals("User could not be onboarded")) {
////
////                                } else {
////                                    viewModel.baseCallBack?.callNextScreen(
////                                        Intent(
////                                            this,
////                                            WelcomeScreenActivity::class.java
////                                        ), null
////                                    )
////                                }
////                            }
////                        }
////                    }
////                    ApiName.GET_CLIENT_TOKEN -> {
////                        it.data?.let {
////                            if (it is GetClientTokenResponse) {
////                                if (!it.accessToken.isNullOrEmpty()) {
////                                    viewModel.reTry()
////                                }
////                            }
////                        }
////
////                    }
////                }
//
//            }
//            Status.LOADING -> {
//                viewModel.loadingLimitForProgress(apiName)
////                viewModel.showProgressActivityAsDialog(apiName)
//            }
//            Status.ERROR -> {
//                //Handle Error
////                hideProgress()
//                viewModel.hideCustomProgress()
////                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                var errorRes: Any
//                var msg = "Apki details verify nahi ho pai hain."
//                if (!(it.message.isNullOrEmpty()))
//                    if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
//                        || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED)
//                    ) {
//                        viewModel.reGenerateAccessToken(true)
//                    } else {
//                        try {
//                            when (apiName) {
//                                ApiName.GET_CLIENT_TOKEN -> {
//                                    msg = it.message
//                                }
//                                ApiName.KYC_SELFIE -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        KYCSelfieResponse::class.java
//                                    ) as KYCSelfieResponse
//                                    if (!errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                                ApiName.KYC_AADHAR -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        KYCAadharResponse::class.java
//                                    ) as KYCAadharResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                                ApiName.KYC_LOCATION -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        KYCLocationResponse::class.java
//                                    ) as KYCLocationResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                                ApiName.KYC_RESULTS -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        AddKYCResultsResponse::class.java
//                                    ) as AddKYCResultsResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                                ApiName.VERIFY_KYC_RESULTS -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        VerifyKYCResultsResponse::class.java
//                                    ) as VerifyKYCResultsResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                                ApiName.SAVE_ADDRESS -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        SaveAddressResponse::class.java
//                                    ) as SaveAddressResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                            }
//                            if (msg.equals(BaaSConstantsUI.INVALID_ACCESS_TOKEN)) {
//                                viewModel.reGenerateAccessToken(true)
//                            } else {
//                                binding.tvErrorMessageForAadhar.setText(msg)
//                            }
//                        } catch (e: Exception) {
//                            viewModel.baseCallBack?.showToastMessage(it.message)
//                        }
//                    }
//
//            }
//            Status.RETRY -> {
//                hideProgress()
//                when (apiName) {
//                    ApiName.KYC_SELFIE -> {
//                        viewModel.saveUserSelfie()
//                    }
//                    ApiName.KYC_AADHAR -> {
//                        viewModel.saveKYCAadhar()
//                    }
//                    ApiName.KYC_RESULTS -> {
//                        viewModel.addKycResults()
//                    }
//                    ApiName.VERIFY_KYC_RESULTS -> {
//                        viewModel.verifyKycResults()
//                    }
//                    ApiName.KYC_LOCATION -> {
//                        viewModel.saveKYCLocation()
//                    }
//                    ApiName.SAVE_ADDRESS -> {
//                        viewModel.onBoardUser()
//                    }
//                    ApiName.GET_CLIENT_TOKEN -> {
//                        viewModel.reGenerateAccessToken(true)
//                    }
//                }
//            }
//        }
//    }
}