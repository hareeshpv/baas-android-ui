package com.payu.baas.core.view.ui.activity.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityUserProfileBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.util.BaaSConstantsUI.ARGUMENTS_IMEI
import com.payu.baas.core.util.BaaSConstantsUI.ARGUMENTS_MOBILE_NUMBER
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TEXT_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_PROFILE_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_VERIFY_PAN_BUTTON_CLICK
import com.payu.baas.core.util.enums.ProfileWebViewEnum
import com.payu.baas.core.view.callback.AlertDialogMultipleCallback
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.core.view.ui.activity.moneyTransfer.BeneficiaryListActivity
import com.payu.baas.core.view.ui.activity.myDetails.MyDetailsActivity
import com.payu.baas.core.view.ui.activity.webView.WebViewActivity
import com.payu.baas.core.view.ui.common.AddMoneyBottomSheetDialogFragment
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*

class UserProfileActivity : BaseActivity() {
    lateinit var binding: ActivityUserProfileBinding
    private lateinit var viewModel: UserProfileViewModel
    private var accountDetails: GetAccountDetailsResponse? = null
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        activity = this
        setupViewModel()
        setupObserver()
        setUpUI()
    }

    private fun setUpUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.mobileNo_ = mobileNumber
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_PROFILE_PAGE_LOAD,
            BaaSConstantsUI.CL_USER_PROFILE_PAGE_LOAD_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), ""
        )
        var extra = intent.extras
        if (extra != null && extra.get(BaaSConstantsUI.PROFILE_SNACKBAR_FLAG_KEY) == "1") {
            SimpleCustomSnackbar.passcodeChange.make(
                binding.container,
                getString(R.string.reset_passcode_snackbar_message),
                Snackbar.LENGTH_LONG
            )?.show()
        }

    }

    private fun setupObserver() {
        viewModel.getAccountDetailsObs().observe(this) {
            parseResponse(it, ApiName.GET_ACCOUNT_DETAILS)
        }

        viewModel.userProfileResponseObs().observe(this) {
            parseResponse(it, ApiName.HELP)
        }

        viewModel.userProfileResponseObs().observe(this) {
            parseResponse(it, ApiName.FAQS)
        }

        viewModel.userProfileResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_USER_DETAILS)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            UserProfileViewModel.UserProfileViewModelFactory(this, this)
        )[UserProfileViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.imeiNumber = imei

//        mZendeskViewModel = ViewModelProvider(
//            this,
//            ZendeskViewModel.ZendeskViewModelFactory(this,this)
//        )[ZendeskViewModel::class.java]

    }

    fun userProfilePreviousScreen(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_GO_BACK_HOME_PROFILE,
            BaaSConstantsUI.CL_USER_GO_BACK_HOME_PROFILE_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), ""
        )
        finish()
    }

    fun openMyDetails(view: View) {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_ACCESS_USER_DETAILS,
            BaaSConstantsUI.CL_USER_ACCESS_USER_DETAILS_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), ""
        )
        callNextScreen(Intent(this, MyDetailsActivity::class.java), null)
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {

                    ApiName.HELP -> {
                        it.data?.let {
                            if (it is HelpResponse) {
                                val mBundle = Bundle()
                                mBundle.putString(
                                    BaaSConstantsUI.USER_PROFILE_FLAG,
                                    ProfileWebViewEnum.HELP.getValue()
                                )
                                mBundle.putString(
                                    BaaSConstantsUI.USER_URL,
                                    (it as HelpResponse).value
                                )
                                callNextScreen(
                                    Intent(applicationContext, WebViewActivity::class.java), mBundle
                                )
                            }
                        }
                    }

                    ApiName.GET_ACCOUNT_DETAILS -> {
                        it.data?.let {
                            if (it is GetAccountDetailsResponse) {
                                invalidateAccountDetailsUI(it)
                            }
                        }
                    }
                    ApiName.FAQS -> {
                        it.data?.let {
                            if (it is FAQsResponse) {
                                val mBundle = Bundle()
                                mBundle.putString(
                                    BaaSConstantsUI.USER_PROFILE_FLAG,
                                    ProfileWebViewEnum.FAQS.getValue()
                                )
                                mBundle.putString(
                                    BaaSConstantsUI.USER_URL,
                                    (it as FAQsResponse).value
                                )
                                callNextScreen(
                                    Intent(
                                        applicationContext,
                                        WebViewActivity::class.java
                                    ), mBundle
                                )
                            }
                        }
                    }
                    ApiName.GET_USER_DETAILS -> {
                        it.data?.let {
                            if (it is GetUserDetailsResponse) {
                                Glide.with(applicationContext)
                                    .load((it as GetUserDetailsResponse).selfieLink)
                                    .error(R.drawable.default_profile)
                                    .into(binding.profilePic)
                            }
                        }
                    }

                    ApiName.LOGOUT -> {
                        it.data?.let {
                            if (it is LogoutResponse) {
                                if ((it as LogoutResponse).msg.equals("SuccessFully logged out!")) {

                                }

                            }
                        }
                    }
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
                    viewModel.reGenerateAccessToken(false)
                } else if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    var msg = ""
                    try {
                        var errorUiRes = JsonUtil.toObject(
                            it.message,
                            ErrorResponseUI::class.java
                        ) as ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.container,
                        msg,
                        false
                    )
                    when (apiName) {
                        ApiName.GET_USER_DETAILS -> {
                            viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                                BaaSConstantsUI.CL_USER_PROFILE_PAGE_LOAD_ERROR,
                                BaaSConstantsUI.CL_USER_PROFILE_PAGE_LOAD_ERROR_EVENT_ID,
                                SessionManager.getInstance(this).accessToken, imei,
                                mobileNumber,
                                Date(), ""
                            )
                        }
                    }
                }

            }
            Status.RETRY -> {
                hideProgress()
            }
        }
    }

    fun changePasscode(view: View) {
        CL_USER_FORGOT_PASSCODE_TEXT_CLICK = "1"
        CL_USER_VERIFY_PAN_BUTTON_CLICK = "1"
        CL_USER_RESET_PASSCODE_BUTTON_CLICK = "1"
        CL_USER_RESET_PASSCODE_PROFILE_CLICK = "1"
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_PASSCODE_RESET,
            BaaSConstantsUI.CL_USER_CHANGE_PASSCODE_PROFILE_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), ""
        )
        callNextScreen(
            Intent(applicationContext, PasscodeActivity::class.java), null
        )
    }

    fun help(view: View) {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_HELP,
            BaaSConstantsUI.CL_USER_HELP_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), "Help"
        )
        launchZendeskSDK()
    }

    fun logout(view: View) {
        binding.container.visibility = View.GONE
        showAlertWithMultipleCallBack(
            getString(R.string.log_out),
            getString(R.string.logout_message),
            getString(R.string.ha),
            getString(R.string.nahi),
            object : AlertDialogMultipleCallback {
                override fun onPositiveActionButtonClick() {
                    viewModel.logOut()
                }

                override fun onNegativeActionButtonClick() {
                    callNextScreen(
                        Intent(applicationContext, UserProfileActivity::class.java),
                        null
                    )
                }
            }
        )
    }

    fun addMoney(view: View) {
        var bundle = Bundle()
        bundle.putString(ARGUMENTS_IMEI, imei)
        bundle.putString(ARGUMENTS_MOBILE_NUMBER, mobileNumber)
        bundle.putString(BaaSConstantsUI.ARGUMENTS_TITLE, getString(R.string.my_account_details))
        if (accountDetails != null) {
            bundle.putString(BaaSConstantsUI.ARGUMENTS_ACCOUNT_DETAILS, Gson().toJson(accountDetails))
        }

        val fragment = AddMoneyBottomSheetDialogFragment.newInstance(bundle)
        fragment.show(supportFragmentManager, "add_money")
        fragment.isCancelable = false
    }

    fun managePayees(view: View) {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_MANAGE_PAYEES,
            BaaSConstantsUI.CL_USER_MANAGE_PAYEES_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            mobileNumber,
            Date(), ""
        )
        SessionManagerUI.getInstance(this).isFromProfileToBeneficiaryListActivity = 1
        callNextScreen(Intent(applicationContext, BeneficiaryListActivity::class.java), null)
    }

    private fun invalidateAccountDetailsUI(accountDetails: GetAccountDetailsResponse?) {
        this.accountDetails = accountDetails
    }

    fun ratesCharges(view: View) {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_RATES_CHARGES,
            BaaSConstantsUI.CL_USER_VIEW_RATES_CHARGES_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(), "Rates & Charges"
        )
        callNextScreen(Intent(applicationContext, RatesChargesActivity::class.java), null)
    }


    override fun onResume() {
        super.onResume()
        viewModel.userDetailResponse()
    }
}
