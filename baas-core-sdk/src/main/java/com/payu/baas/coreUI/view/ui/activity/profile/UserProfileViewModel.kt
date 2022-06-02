package com.payu.baas.coreUI.view.ui.activity.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.ApiCall
import com.payu.baas.coreUI.util.ApiHelperUI
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import com.payu.baas.coreUI.view.ui.activity.splash.SplashScreenActivity
import kotlinx.coroutines.launch
import java.util.*

class UserProfileViewModel (
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val userProfileResponseObs = MutableLiveData<Resource<Any>>()
    private val accountDetailsObs = MutableLiveData<Resource<Any>>()
     var userTitleNameObs = MutableLiveData("")

    @SuppressLint("StaticFieldLeak")
    var mobileNo_: String? = null
    var imeiNumber: String? = null
  init {
    userDetailResponse()
    getAccountDetails()
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

//    fun Help() {
//        baseCallBack!!.cleverTapUserProfileEvent(
//            BaaSConstantsUI.CL_USER_HELP,
//            BaaSConstantsUI.CL_USER_HELP_EVENT_ID,
//            SessionManager.getInstance(context).accessToken, imeiNumber,
//            mobileNo_,
//            Date(),"Help"
//        )
//        if (baseCallBack!!.isInternetAvailable(true)) {
//            viewModelScope.launch {
//                userProfileResponseObs.postValue(Resource.loading(null))
//                try {
//                    val apiParams = ApiParams().apply {
//
//                    }
//
//                    ApiCall.callAPI(ApiName.HELP, apiParams, object : ApiHelperUI {
//                        override fun onSuccess(apiResponse: ApiResponse) {
//                            if (apiResponse is HelpResponse) {
//                                userProfileResponseObs.postValue(Resource.success(apiResponse))
//                            }
//                        }
//
//                        override fun onError(errorResponse: ErrorResponse) {
//                            userProfileResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
//                                errorResponse))
//                        }
//                    })
//                } catch (e: Exception) {
//                    userProfileResponseObs.postValue(Resource.error(e.toString(), null))
//                }
//            }
//        }
//
//    }


    fun FAQs() {
        baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_FAQs,
            BaaSConstantsUI.CL_USER_VIEW_FAQS_EVENT_ID,
            SessionManager.getInstance(context).accessToken, imeiNumber,
            mobileNo_,
            Date(),""
        )
        if (baseCallBack.isInternetAvailable(true)) {
            viewModelScope.launch {
                userProfileResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                    }
                    ApiCall.callAPI(ApiName.FAQS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is FAQsResponse) {
                                userProfileResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            userProfileResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    userProfileResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }

    fun logOut() {
        baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_LOGOUT,
            BaaSConstantsUI.CL_USER_LOGOUT_EVENT_ID,
            SessionManager.getInstance(context).accessToken, imeiNumber,
            mobileNo_,
            Date(),""
        )
        if (baseCallBack!!.isInternetAvailable(true)) {
            viewModelScope.launch {
                userProfileResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                    }
                    ApiCall.callAPI(ApiName.LOGOUT, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is LogoutResponse) {
                                CL_USER_RESET_PASSCODE_BUTTON_CLICK = "0"
                                userProfileResponseObs.postValue(Resource.success(apiResponse))
                                SessionManagerUI.getInstance(context).userStatusCode = UserState.LOGGED_OUT.getValue()
                                baseCallBack!!.callNextScreen(Intent(context, SplashScreenActivity::class.java), null)

                            }
                        }
                        override fun onError(errorResponse: ErrorResponse) {
                            userProfileResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))

                        }
                    })
                } catch (e: Exception) {
                    userProfileResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }

    fun userDetailResponse() {
        if (baseCallBack!!.isInternetAvailable(true)) {
            viewModelScope.launch {
                userProfileResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {

                    }
                    ApiCall.callAPI(ApiName.GET_USER_DETAILS, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is GetUserDetailsResponse) {
                                userTitleNameObs.value = (apiResponse as GetUserDetailsResponse).name

                                userProfileResponseObs.postValue(Resource.success(apiResponse))

                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            userProfileResponseObs.postValue(Resource.error(errorResponse.errorMessage!!,
                                errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    userProfileResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }
        fun userProfileResponseObs(): LiveData<Resource<Any>> {
        return userProfileResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class UserProfileViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserProfileViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}