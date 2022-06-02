package com.payu.baas.coreUI.view.ui.activity.kyc

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import com.payu.baas.coreUI.view.ui.activity.accountopeningfailure.AccountOpeningFailureScreenActivity
import com.payu.baas.coreUI.view.ui.activity.dashboard.DashboardActivity
import com.payu.baas.coreUI.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.coreUI.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.coreUI.view.ui.activity.onboarding.WelcomeScreenActivity
import com.payu.baas.coreUI.view.ui.activity.panEmpVerification.PANEmployeeDetailActivity
import com.payu.baas.coreUI.view.ui.activity.reviewAndSubmit.ReviewSubmitDetailActivity
import com.payu.baas.coreUI.view.ui.activity.set_passcode.SetPasscodeActivity
import kotlinx.coroutines.launch

class CompleteKycViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val userStateResponseObs = MutableLiveData<Resource<Any>>()
    private val applicationResponseObs = MutableLiveData<Resource<Any>>()

    //    private val karzaKeyResponseObs = MutableLiveData<Resource<Any>>()
//    private val karzaTokenResponseObs = MutableLiveData<Resource<Any>>()
//    private val karzaUserTokenResponseObs = MutableLiveData<Resource<Any>>()
//    private val transactionIdResponseObs = MutableLiveData<Resource<Any>>()
    val visTickPan: ObservableField<Boolean> = ObservableField(true);
    val visTickCardDelivery: ObservableField<Boolean> = ObservableField(true);
    val visTickSelfie: ObservableField<Boolean> = ObservableField(true);
    val visTickAadhaar: ObservableField<Boolean> = ObservableField(true);
    val visTickSubmit: ObservableField<Boolean> = ObservableField(true)

    val ivTickPan: ObservableField<Drawable> = ObservableField()
    val ivTickCardDelivery: ObservableField<Drawable> = ObservableField()
    val ivTickSelfie: ObservableField<Drawable> = ObservableField()
    val ivTickAadhaar: ObservableField<Drawable> = ObservableField()
    val ivTickSubmit: ObservableField<Drawable> = ObservableField()

    val visStartPanLayout: ObservableField<Boolean> = ObservableField(false)
    val visStartCardDeliveryLayout: ObservableField<Boolean> = ObservableField(false)
    val visStartSelfieLayout: ObservableField<Boolean> = ObservableField(false)
    val visStartAadhaarLayout: ObservableField<Boolean> = ObservableField(false)
    val visStartSubmitLayout: ObservableField<Boolean> = ObservableField(false)
    val strStartPanText: ObservableField<String> = ObservableField("")
    val strStartCardDeliveryText: ObservableField<String> = ObservableField("")
    val strStartSelfieText: ObservableField<String> = ObservableField("")
    val strStartAadhaarText: ObservableField<String> = ObservableField("")
    val strStartSubmitText: ObservableField<String> = ObservableField("")

    val bgRlPan: ObservableField<Drawable> = ObservableField()
    val bgRlCardDelivery: ObservableField<Drawable> = ObservableField()
    val bgRlSelfie: ObservableField<Drawable> = ObservableField()
    val bgRlAadhaar: ObservableField<Drawable> = ObservableField()
    val bgRlSubmit: ObservableField<Drawable> = ObservableField()
    val bgRlStep1: ObservableField<Drawable> = ObservableField()
    val bgRlStep2: ObservableField<Drawable> = ObservableField()
    val bgRlStep3: ObservableField<Drawable> = ObservableField()
    val bgRlStep4: ObservableField<Drawable> = ObservableField()
    val bgRlStep5: ObservableField<Drawable> = ObservableField()

    init {

    }

    fun openPanScreen() {
        baseCallBack?.callNextScreen(Intent(context, PANEmployeeDetailActivity::class.java), null)
    }

    fun openCardDelivery() {
        baseCallBack?.callNextScreen(
            Intent(context, CardDeliveryAddressDetailActivity::class.java),
            null
        )
    }

    fun getUserState() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                userStateResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        mobileNumber =
                            SessionManagerUI.getInstance(context).registeredMobileNumber?.replace(
                                "+91 ",
                                ""
                            )
                    }

                    ApiCall.callAPI(ApiName.GET_USER_STATE, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetUserStateResponse) {
                                userStateResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE) {
                                baseCallBack.showTechinicalError()
                            } else
                                userStateResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                        }
                    })
                } catch (e: Exception) {
                    userStateResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getUserStateResponseObs(): LiveData<Resource<Any>> {
        return userStateResponseObs
    }

    fun getMyViewVisibility(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }


    fun getApplicationId() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                applicationResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                    }

                    ApiCall.callAPI(ApiName.GET_APPLICATION_ID, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetApplicationIdResultsResponse) {
                                applicationResponseObs.postValue(Resource.success(apiResponse))
//                                generateNewTranasactionId()
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
//                            if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                            ){
//                                baseCallBack.showTechinicalError()
//                            } else
                            applicationResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    applicationResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getApplicationResponseObs(): LiveData<Resource<Any>> {
        return applicationResponseObs
    }

    fun reviewAndSubmit() {
        SessionManagerUI.getInstance(context).userStatusCode =
            UserState.KYC_SCREEN_PASSED.getValue()
        baseCallBack?.callNextScreen(
            Intent(context, ReviewSubmitDetailActivity::class.java),
            null,
            true
        )
    }

    fun showViewsAsPerUserState(userStatusCode: String) {
        SessionManagerUI.getInstance(context).userStatusCode = userStatusCode
        when (userStatusCode) {
            UserState.MOBILE_NOT_SUBMITTED.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent( 
                        context,
                        MobileVerificationActivity::class.java
                    ), null, true
                )
            }
            UserState.MOBILE_VERIFIED.getValue() -> {
                visTickPan.set(false)
                visTickCardDelivery.set(true)
                visTickAadhaar.set(true)
                visTickSelfie.set(true)
                visTickSubmit.set(true)
                setPanSelected()
                ivTickCardDelivery.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSelfie.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickAadhaar.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSubmit.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                visStartPanLayout.set(true)
                visStartCardDeliveryLayout.set(false)
                visStartSelfieLayout.set(false)
                visStartAadhaarLayout.set(false)
                visStartSubmitLayout.set(false)
                strStartPanText.set(context.resources.getString(R.string.start_text))
            }
            UserState.PAN_SAVED_LOCAL.getValue() -> {
                visTickPan.set(true)
                visTickCardDelivery.set(false)
                visTickSelfie.set(true)
                visTickAadhaar.set(true)
                visTickSubmit.set(true)
                setCardSelected()
                ivTickPan.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickSelfie.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickAadhaar.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSubmit.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                visStartPanLayout.set(false)
                visStartCardDeliveryLayout.set(true)
                visStartSelfieLayout.set(false)
                visStartAadhaarLayout.set(false)
                visStartSubmitLayout.set(false)
                strStartCardDeliveryText.set(context.resources.getString(R.string.continue_text))
            }
            UserState.CARD_DELIVERY_ADDRESS_SAVED_LOCAL.getValue() -> {
                visTickPan.set(true)
                visTickCardDelivery.set(true)
                visTickSelfie.set(false)
                visTickAadhaar.set(true)
                visTickSubmit.set(true)
                setSelfieSelected()
                ivTickPan.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickCardDelivery.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickAadhaar.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSubmit.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                visStartPanLayout.set(false)
                visStartCardDeliveryLayout.set(false)
                visStartSelfieLayout.set(true)
                visStartAadhaarLayout.set(false)
                visStartSubmitLayout.set(false)
                strStartSelfieText.set(context.resources.getString(R.string.continue_text))
//                visStartPanLayout.set(View.GONE)
//                visStartCardDeliveryLayout.set(View.GONE)
//                visStartSelfieLayout.set(View.VISIBLE)
//                visStartAadhaarLayout.set(View.GONE)
//                visStartSubmitLayout.set(View.GONE)
            }
            UserState.SELFIE_SAVED_LOCAL.getValue() -> {
                visTickPan.set(true)
                visTickCardDelivery.set(true)
                visTickSelfie.set(true)
                visTickAadhaar.set(false)
                visTickSubmit.set(true)
                setAadhaarSelected()
                ivTickPan.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickCardDelivery.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickSelfie.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickSubmit.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                visStartPanLayout.set(false)
                visStartCardDeliveryLayout.set(false)
                visStartSelfieLayout.set(false)
                visStartAadhaarLayout.set(true)
                visStartSubmitLayout.set(false)
                strStartAadhaarText.set(context.resources.getString(R.string.continue_text))
            }
            UserState.AADHARXML_SAVED_LOCAL.getValue() -> {
                visTickPan.set(true)
                visTickCardDelivery.set(true)
                visTickSelfie.set(true)
                visTickAadhaar.set(true)
                visTickSubmit.set(false)
                setSubmitSelected()
                ivTickPan.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickCardDelivery.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickSelfie.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                ivTickAadhaar.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_tick_green
                    )
                )
                visStartPanLayout.set(false)
                visStartCardDeliveryLayout.set(false)
                visStartSelfieLayout.set(false)
                visStartAadhaarLayout.set(false)
                visStartSubmitLayout.set(true)
                strStartSubmitText.set(context.resources.getString(R.string.finish_text))
            }
            UserState.KYC_SCREEN_PASSED.getValue(),
            UserState.SELFIE_SAVED.getValue(),
            UserState.AADHARXML_SAVED.getValue(),
            UserState.LAT_LONG_IP_SAVED.getValue(),
            UserState.KYC_RESULT_SAVED.getValue(),
            UserState.KYC_CHECKS_PASSED.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_1.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_2.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_3.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_4.getValue(),
            UserState.ONBOARDING_IN_PROGRESS.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(
                        context,
                        ReviewSubmitDetailActivity::class.java
                    ), null, true
                )
            }
            UserState.KYC_CHECKS_FAILED.getValue(),
            UserState.ONBOARDING_FAILED_1.getValue(),
            UserState.ONBOARDING_FAILED_2.getValue(),
            UserState.ONBOARDING_FAILED.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(
                        context,
                        AccountOpeningFailureScreenActivity::class.java
                    ), null, true
                )
            }

            UserState.ONBOARDED.getValue(),
            UserState.ONBOARDING_SUCCESS.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(context, WelcomeScreenActivity::class.java),
                    null, true
                )
            }
            UserState.WELCOM_SCREEN_REACHED.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(context, SetPasscodeActivity::class.java),
                    null,
                    true
                )
            }
            UserState.PASSCODE_SET.getValue(),
            UserState.LOGIN_DONE.getValue(),
            UserState.LOGGED_OUT.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(context, PasscodeActivity::class.java),
                    null,
                    true
                )
            }
            else -> { // Note the block
                visTickPan.set(false)
                visTickCardDelivery.set(true)
                visTickAadhaar.set(true)
                visTickSelfie.set(true)
                visTickSubmit.set(true)
                ivTickCardDelivery.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSelfie.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickAadhaar.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                ivTickSubmit.set(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_svg_lock
                    )
                )
                visStartPanLayout.set(true)
                visStartCardDeliveryLayout.set(false)
                visStartSelfieLayout.set(false)
                visStartAadhaarLayout.set(false)
                visStartSubmitLayout.set(false)
                strStartPanText.set(context.resources.getString(R.string.start_text))
            }
        }
    }

    fun setPanSelected() {
        bgRlPan.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_selected_background
            )
        )
        bgRlCardDelivery.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSelfie.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlAadhaar.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSubmit.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        //step1 selected
        bgRlStep1.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_selected_bg
            )
        )
        bgRlStep2.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep3.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep4.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep5.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
    }

    fun setCardSelected() {
        bgRlPan.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlCardDelivery.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_selected_background
            )
        )
        bgRlSelfie.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlAadhaar.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSubmit.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        //step2 selected
        bgRlStep1.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep2.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_selected_bg
            )
        )
        bgRlStep3.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep4.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep5.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
    }

    fun setSelfieSelected() {
        bgRlPan.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlCardDelivery.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSelfie.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_selected_background
            )
        )
        bgRlAadhaar.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSubmit.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        //step3 selected
        bgRlStep1.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep2.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep3.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_selected_bg
            )
        )
        bgRlStep4.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep5.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
    }

    fun setAadhaarSelected() {
        bgRlPan.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlCardDelivery.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSelfie.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlAadhaar.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_selected_background
            )
        )
        bgRlSubmit.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        //step4 selected
        bgRlStep1.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep2.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep3.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep4.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_selected_bg
            )
        )
        bgRlStep5.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
    }

    fun setSubmitSelected() {
        bgRlPan.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlCardDelivery.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSelfie.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlAadhaar.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_background
            )
        )
        bgRlSubmit.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.kyc_step_selected_background
            )
        )
        //step5 selected
        bgRlStep1.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep2.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep3.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep4.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_bg
            )
        )
        bgRlStep5.set(
            AppCompatResources.getDrawable(
                context,
                R.drawable.black_circle_selected_bg
            )
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class CompleteKycViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CompleteKycViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CompleteKycViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}