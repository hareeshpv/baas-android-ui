package com.payu.baas.core.view.ui.activity.reset_passcode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.UpdatePasswordResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.ApiCall
import com.payu.baas.core.util.ApiHelperUI
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.enums.ErrorType
import com.payu.baas.core.view.callback.BaseCallback
import com.payu.baas.core.view.ui.BaseViewModel
import com.payu.baas.core.view.ui.activity.onboarding.ErrorActivity
import kotlinx.coroutines.launch
import java.util.*

class ResetPasscodeViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {
    private val resetPasscodeResponseObs = MutableLiveData<Resource<Any>>()
     val resetcode1: ObservableField<String> = ObservableField("")
     val resetcode2: ObservableField<String> = ObservableField("")
     val resetcode3: ObservableField<String> = ObservableField("")
      val resetcode4: ObservableField<String> = ObservableField("")
      var oldPasscode : String? = null
    var mobileNo_: String? = null
    var imeiNumber: String? = null
   init {
       mobileNo_= SessionManagerUI.getInstance(context).userMobileNumber

   }
    @SuppressLint("StaticFieldLeak")
    fun resetPasscode() {
         if(CL_USER_RESET_PASSCODE_BUTTON_CLICK == "2"){ // Block
            baseCallBack!!.cleverTapUserCardManagementEvent(
                BaaSConstantsUI.CL_USER_RESET_FORGOT_PASSCODE_BLOCK_CARD,
                BaaSConstantsUI.CL_USER_RESET_FORGOT_PASSCODE_BLOCK_CARD_EVENT_ID,
                SessionManager.getInstance(context).accessToken,
                imeiNumber,
                mobileNo_,
                Date(),
                "",
                ""
            )
        }else if(CL_USER_RESET_PASSCODE_BUTTON_CLICK == "3"){ // Change limits
             baseCallBack!!.cleverTapUserCardManagementEvent(
                 BaaSConstantsUI.CL_RESET_FORGOT_PASSCODE_CHANGE_CARD_LIMITS,
                 BaaSConstantsUI.CL_USER_RESET_FORGOT_PASSCODE_TRANSACTION_LIMIT_EVENT_ID,
                 "",
                 imeiNumber,
                 mobileNo_,
                 Date(),
                 "",
                 ""
             )
         }else if(CL_USER_RESET_PASSCODE_BUTTON_CLICK == "4"){
             baseCallBack!!.cleverTapUserMoneyTransferEvent(
                 BaaSConstantsUI.CL_RESET_FORGOT_PASSCODE_MONEY_TRANSFER,
                 BaaSConstantsUI.CL_RESET_FORGOT_PASSCODE_MONEY_TRANSFER_EVENT_ID,
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
        if(!baseCallBack!!.isInternetAvailable(false)){
            val mBundle = Bundle()
            mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
            baseCallBack.callNextScreen(Intent(context, ErrorActivity::class.java),mBundle)
        }else {
            viewModelScope.launch {
                resetPasscodeResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        oldPassword = oldPasscode?:""
                        newPassword = """${resetcode1.get().toString().trim()}${resetcode2.get().toString().trim()}${
                            resetcode3.get().toString().trim()
                        }${resetcode4.get().toString().trim()}"""
                    }

                    ApiCall.callAPI(ApiName.UPDATE_PASSWORD, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is UpdatePasswordResponse) {
                                resetPasscodeResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            resetPasscodeResponseObs.postValue(Resource.error(errorResponse.errorMessage!!, errorResponse))
                        }
                    })
                } catch (e: Exception) {
                    resetPasscodeResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }

    }
    fun getResetPasscodeResponseObs(): LiveData<Resource<Any>> {
        return resetPasscodeResponseObs
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class ResetPasscodeViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ResetPasscodeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ResetPasscodeViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}