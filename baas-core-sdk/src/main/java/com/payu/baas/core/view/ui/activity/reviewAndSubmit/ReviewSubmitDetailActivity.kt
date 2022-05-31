package com.payu.baas.core.view.ui.activity.reviewAndSubmit

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityReviewSubmitDetailBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.entities.model.PanDetailsModelUI
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.accountopeningfailure.AccountOpeningFailureScreenActivity
import com.payu.baas.core.view.ui.activity.onboarding.WelcomeScreenActivity
import com.payu.baas.core.view.ui.activity.webView.WebViewActivity
import java.util.*


class ReviewSubmitDetailActivity : BaseActivity() {
    lateinit var binding: ActivityReviewSubmitDetailBinding
    private lateinit var viewModel: ReviewAndSubmitViewModel
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_submit_detail)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    fun openPreviousScreen(view: View) {
        finish()
        viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_BACK_TERMS_CONDITION,
            BaaSConstantsUI.CL_USER_BACK_TERMS_CONDITION_EVENT_ID,
            SessionManager.getInstance(this).accessToken!!, imei, mobileNumber, Date()
        )
    }


    private fun setupUI() {
        var dpImage = SessionManagerUI.getInstance(this).karzaUserSelfie
//        val photoUrl = URL(dpImage)
//        var mIcon = BitmapFactory.decodeStream(
//            photoUrl.openConnection().getInputStream()
//        )
//        binding.profilePic.setImageBitmap(mIcon)

        Glide.with(applicationContext)
            .load(dpImage)
            .error(R.drawable.default_profile)
            .into(binding.profilePic)

        var strPanDetails =
            com.payu.baas.core.model.storage.SessionManagerUI.getInstance(this).userPanDetails
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        if (!strPanDetails.isNullOrEmpty()) {
            var panDetails =
                JsonUtil.toObject(strPanDetails, PanDetailsModelUI::class.java) as PanDetailsModelUI
            viewModel.strName.set(panDetails.firstName + " " + panDetails.lastName)
            viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                BaaSConstantsUI.CL_USER_KYC_SUCCESS,
                BaaSConstantsUI.CL_USER_KYC_SUCCESS_EVENT_ID,
                SessionManager.getInstance(this).accessToken!!,
                imei,
                mobileNumber,
                panDetails.dob,
                panDetails.employeeId,
                viewModel.getAddress(),
                Date()
            )
        }
//        viewModel.getAddress()
        setTermsAndCondition()
    }

    private fun setupObserver() {
        viewModel.getKycSelfieResponseObs().observe(this, {
            parseResponse(it, ApiName.KYC_SELFIE)
        })
        viewModel.getKycAadhaarResponseObs().observe(this, {
            parseResponse(it, ApiName.KYC_AADHAR)
        })
        viewModel.getKycResultsResponseObs().observe(this, {
            parseResponse(it, ApiName.KYC_RESULTS)
        })
        viewModel.getVerifyKycResponseObs().observe(this, {
            parseResponse(it, ApiName.VERIFY_KYC_RESULTS)
        })
        viewModel.getKycLocationResponseObs().observe(this, {
            parseResponse(it, ApiName.KYC_LOCATION)
        })
        viewModel.getOnBoardResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_ONBOARD_STATUS)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
//                hideProgress()
                when (apiName) {
//                    ApiName.KYC_SELFIE, ApiName.KYC_AADHAR,
//                    ApiName.KYC_RESULTS, ApiName.KYC_LOCATION -> {
////                        hideProgress()
////                        viewModel.setProgressForApi(viewModel.progressCount + 15)
//                    }
                    ApiName.VERIFY_KYC_RESULTS -> {
                        hideProgress()
                    }
                    ApiName.GET_ONBOARD_STATUS -> {
                        it.data?.let {
                            if (it is GetOnBoardUserStatusResponse) {
                                var message = it.message
//                                if (message!!.equals(com.payu.baas.core.util.enums.UserState.ONBOARDING_IN_PROGRESS.getValue())) {
//                                    viewModel.onBoardUserStatus()
//                                } else
                                if (message!!.equals(com.payu.baas.core.util.enums.UserState.ONBOARDING_SUCCESS.getValue())) {
//                                    viewModel.hideCustomProgress()
                                    viewModel.baseCallBack?.callNextScreen(
                                        Intent(
                                            this,
                                            WelcomeScreenActivity::class.java
                                        ), null, true
                                    )
                                } else if (message!!.equals(com.payu.baas.core.util.enums.UserState.ONBOARDING_FAILED.getValue())) {
//                                    viewModel.hideCustomProgress()
                                    var bundle = Bundle()
                                    bundle.putString(
                                        BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY,
                                        message
                                    )
                                    viewModel.baseCallBack?.callNextScreen(
                                        Intent(
                                            this,
                                            AccountOpeningFailureScreenActivity::class.java
                                        ), bundle, true
                                    )
                                }
                            }
                        }
                    }
                }

            }
            Status.LOADING -> {
                if (!apiName.equals(ApiName.GET_ONBOARD_STATUS))
                    showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
                if (apiName.equals(ApiName.GET_ONBOARD_STATUS))
                    viewModel.hideCustomProgress()
                else
                    hideProgress()
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                try {
                    var errorRes: Any
                    var msg = "Apki details verify nahi ho pai hain."
                    var errorResponse = it.data as ErrorResponse
                    if (!(it.message.isNullOrEmpty()))
                        if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED)
                        ) {
                            viewModel.reGenerateAccessToken(true)
                        } else {
                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                                && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                            ) {
                                viewModel.baseCallBack!!.showTechinicalError()
                            } else {
                                var errorUiRes = JsonUtil.toObject(
                                    it.message,
                                    ErrorResponseUI::class.java
                                ) as ErrorResponseUI
                                if (apiName == ApiName.VERIFY_KYC_RESULTS && errorUiRes.systemMessage.equals("FAILURE")) {
                                        SessionManagerUI.getInstance(this).userStatusCode =
                                            com.payu.baas.core.util.enums.UserState.KYC_CHECKS_FAILED.getValue()
                                    var bundle = Bundle()
                                    bundle.putString(
                                        BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY,
                                        errorUiRes.userMessage!!
                                    )
                                    viewModel.baseCallBack?.callNextScreen(
                                        Intent(
                                            this,
                                            AccountOpeningFailureScreenActivity::class.java
                                        ), bundle, true
                                    )
                                } else {
                                    var errorUiRes = JsonUtil.toObject(
                                        it.message,
                                        ErrorResponseUI::class.java
                                    ) as ErrorResponseUI
                                    msg = errorUiRes.userMessage!!

                                    UtilsUI.showSnackbarOnSwitchAction(
                                        binding.rlParent,
                                        msg,
                                        false
                                    )
                                }


//                    showToastMessage(it.message)
                            }
//                            when (apiName) {
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
//                                ApiName.GET_ONBOARD_STATUS -> {
//                                    errorRes = JsonUtil.toObject(
//                                        it.message,
//                                        GetOnBoardUserStatusResponse::class.java
//                                    ) as GetOnBoardUserStatusResponse
//                                    if (errorRes.message.isNullOrEmpty() && !errorRes.userMessage.isNullOrEmpty())
//                                        msg = errorRes.userMessage!!
//                                    else if (!errorRes.message.isNullOrEmpty())
//                                        msg = errorRes.message!!
//                                }
//                            }
//                            if (msg.contains("Apka account already")) {
////                                viewModel.reGenerateAccessToken(true)
//                                viewModel.baseCallBack?.callNextScreen(
//                                    Intent(
//                                        this,
//                                        ReloginActivity::class.java
//                                    ), null
//                                )
//                                finish()
//                            } else {
//                                var bundle = Bundle()
//                                bundle.putString(
//                                    BaaSConstantsUI.ACCOUNT_FAILURE_REASON_KEY,
//                                    msg
//                                )
//                                viewModel.baseCallBack?.callNextScreen(
//                                    Intent(
//                                        this,
//                                        AccountOpeningFailureScreenActivity::class.java
//                                    ), bundle, true
//                                )
//                            }
                        }
                } catch (e: Exception) {
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.rlParent,
                        it.message!!,
                        false
                    )
//                            viewModel.baseCallBack?.showToastMessage(it.message)
                }
            }
            Status.RETRY -> {
                if (apiName.equals(ApiName.GET_ONBOARD_STATUS))
                    viewModel.hideCustomProgress()
                else
                    hideProgress()
                when (apiName) {
                    ApiName.KYC_SELFIE -> {
                        viewModel.saveUserSelfie()
                    }
                    ApiName.KYC_AADHAR -> {
                        viewModel.saveKYCAadhar()
                    }
                    ApiName.KYC_RESULTS -> {
                        viewModel.addKycResults()
                    }
                    ApiName.VERIFY_KYC_RESULTS -> {
                        viewModel.verifyKycResults()
                    }
                    ApiName.KYC_LOCATION -> {
                        viewModel.saveKYCLocation()
                    }
                    ApiName.GET_ONBOARD_STATUS -> {
                        viewModel.onBoardUserStatus()
                    }
                    ApiName.GET_CLIENT_TOKEN -> {
                        viewModel.reGenerateAccessToken(true)
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReviewAndSubmitViewModel.ReviewViewModelFactory(this, this)
        )[ReviewAndSubmitViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei
        viewModel.mobileNo_ = mobileNumber
    }

    fun openLinks(url: String) {
        val mBundle = Bundle()
        mBundle.putString(BaaSConstantsUI.USER_URL, getString(R.string.terms_condition_link))
        mBundle.putString(BaaSConstantsUI.USER_URL, url)
        callNextScreen(
            Intent(
                applicationContext,
                WebViewActivity::class.java
            ), mBundle
        )
    }

    @SuppressLint("StringFormatInvalid")
    private fun setTermsAndCondition() {
        val termsOfServicee = getString(R.string.user_terms_condition)
        val sbmTermsOfServicee = getString(R.string.sbm_terms_condition)
        val privacyPolicy = getString(R.string.privacy_policy)
        val completeString = getString(
            R.string.main_iske_dvaara_payu_ki,
            privacyPolicy,
            sbmTermsOfServicee,
            termsOfServicee
        )
        val startIndex = completeString.indexOf(privacyPolicy)
        val endIndex = startIndex + privacyPolicy.length
        val startIndex2 = completeString.indexOf(sbmTermsOfServicee)
        val endIndex2 = startIndex2 + sbmTermsOfServicee.length
        val startIndex3 = completeString.indexOf(termsOfServicee)
        val endIndex3 = startIndex3 + termsOfServicee.length
        val spannableStringBuilder = SpannableStringBuilder(completeString)
        val clickOnTerms: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openLinks(getString(R.string.terms_condition_link))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(activity!!, R.color.green_light)
                ds.isUnderlineText = true
                setGradientColorToLinks(ds, termsOfServicee)
            }
        }
        val clickOnPrivacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openLinks(getString(R.string.privacy_policy_link))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(activity!!, R.color.green_light)
                ds.isUnderlineText = true
                setGradientColorToLinks(ds, privacyPolicy)
            }
        }
        val clickOnsbm: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openLinks(getString(R.string.sbm_terms_condition_link))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(activity!!, R.color.green_light)
                ds.isUnderlineText = true
                setGradientColorToLinks(ds, sbmTermsOfServicee)
            }
        }
        spannableStringBuilder.setSpan(
            clickOnTerms,
            startIndex3,
            endIndex3,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            clickOnPrivacy,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            clickOnsbm,
            startIndex2,
            endIndex2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTerms1.setText(spannableStringBuilder)
        binding.tvTerms1.setMovementMethod(LinkMovementMethod.getInstance())
    }

    fun setGradientColorToLinks(ds: TextPaint, text: String) {
        val textShader: Shader = LinearGradient(
            0f, 0f, ds.measureText(text),
            text.length.toFloat(), intArrayOf(
                Color.parseColor("#B2CC25"), Color.parseColor("#0FC99C")
            ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )

        ds.setShader(textShader)
    }

    override fun onResume() {
        super.onResume()
        val userState = SessionManagerUI.getInstance(this).userStatusCode
        when (userState) {
            com.payu.baas.core.util.enums.UserState.KYC_CHECKS_PASSED.getValue(),
            com.payu.baas.core.util.enums.UserState.ONBOARDING_IN_PROGRESS.getValue() -> {
                viewModel.onBoardUserStatus()
            }
            com.payu.baas.core.util.enums.UserState.KYC_CHECKS_FAILED.getValue() -> {
                viewModel.verifyKycResults()
            }
//            else -> {
//                viewModel.gpsTracker = GPSTracker(this);
//
//                if (!viewModel.gpsTracker.getIsGPSTrackingEnabled()) {
//                    viewModel.gpsTracker.showSettingsAlert();
//                } else {
//                    if (viewModel.gpsTracker.location == null) {
//                        UtilsUI.showSnackbarOnSwitchAction(
//                            binding.rlParent,
//                            BaaSConstantsUI.FETCHING_LOCATION_MESSAGE,
//                            false
//                        )
//                        viewModel.gpsTracker = GPSTracker(this)
//                    }
//
//                }
//            }
        }
    }
}