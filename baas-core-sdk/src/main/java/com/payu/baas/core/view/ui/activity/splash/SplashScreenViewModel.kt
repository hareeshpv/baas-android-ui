package com.payu.baas.core.view.ui.activity.splash

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.GetUserStateResponse
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.UserState
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.accountopeningfailure.AccountOpeningFailureScreenActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.core.view.ui.activity.intro.IntroActivity
import com.payu.baas.core.view.ui.activity.kyc.CompleteKYCActivity
import com.payu.baas.core.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.core.view.ui.activity.onboarding.WelcomeScreenActivity
import com.payu.baas.core.view.ui.activity.reviewAndSubmit.ReviewSubmitDetailActivity
import com.payu.baas.core.view.ui.activity.set_passcode.SetPasscodeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.*


class SplashScreenViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    //    private val context = getApplication<Application>().applicationContext
    private val userStateResponseObs = MutableLiveData<Resource<Any>>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val ipv4: String? = baseCallBack?.getPublicIP()
            withContext(Dispatchers.Main) {
                ipv4?.let {
                }
            }
        }
    }

    fun getUserState(userRegisteredNumberWithCode: String) {
        viewModelScope.launch {
            userStateResponseObs.postValue(Resource.loading(null))
            try {
                val apiParams = ApiParams().apply {
                    mobileNumber =
                        userRegisteredNumberWithCode?.replace(
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

    fun getUserStateResponseObs(): LiveData<Resource<Any>> {
        return userStateResponseObs
    }

    fun showScreenAsPerUserState(userStatusCode: String) {
        when (userStatusCode) {
            UserState.MOBILE_NOT_SUBMITTED.getValue() -> {
                baseCallBack?.callNextScreen(Intent(context, IntroActivity::class.java), null, true)
            }
            UserState.MOBILE_SUBMITTED.getValue(),
            UserState.PERMISSION_ASSIGNED.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(
                        context,
                        MobileVerificationActivity::class.java
                    ), null, true
                )
            }
            UserState.MOBILE_VERIFIED.getValue(),
            UserState.KARZA_APPLICATION_GENERATED.getValue(),
            UserState.PAN_SAVED_LOCAL.getValue(),
            UserState.CARD_DELIVERY_ADDRESS_SAVED_LOCAL.getValue(),
            UserState.SELFIE_SAVED_LOCAL.getValue(),
            UserState.AADHARXML_SAVED_LOCAL.getValue() -> {
                baseCallBack?.callNextScreen(
                    Intent(context, CompleteKYCActivity::class.java),
                    null,
                    true
                )
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
        }
    }

//    fun callNextScreen(intent: Intent, bundle: Bundle?) {
//        if (bundle != null)
//            intent.putExtras(bundle)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class SplashViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashScreenViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


class MyAssyn : AsyncTask<String?, String?, String>() {

    override fun doInBackground(vararg params: String?): String {
        var publicIP = ""
        try {
            val s = Scanner(
                URL(
                    "https://api.ipify.org"
                )
                    .openStream(), "UTF-8"
            )
                .useDelimiter("\\A")
            publicIP = s.next()
        } catch (e: IOException) {
        }
        return publicIP
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}