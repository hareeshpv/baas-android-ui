package com.payu.baas.coreUI.view.ui.activity.cardBlockReason

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityCardBlockReasonBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.BlockCardResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TEXT_CLICK
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_VERIFY_PAN_BUTTON_CLICK
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.address.AdressActivity
import com.payu.baas.coreUI.view.ui.activity.enterpasscode.PasscodeActivity
import java.util.*

class CardBlockReasonActivity : BaseActivity() {
    lateinit var binding: ActivityCardBlockReasonBinding
    private lateinit var viewModel: CardBlockReasonViewModel
    var mobileNumber: String? = null

    //    private var isAddressSelected = false
    var CODE = 102
    var REASON_CODE = 103
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_block_reason)
        activity = this
        SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock = 0
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        setupViewModel()
        setupObserver()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            CardBlockReasonViewModel.CardBlockViewModelFactory(this, this)
        )[CardBlockReasonViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getBlockResponseObs().observe(this, {
            parseResponse(it, ApiName.BLOCK_CARD)
        })
        viewModel.getAccessTokenResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.BLOCK_CARD -> {
                        it.data?.let {
                            if (it is BlockCardResponse) {
                                SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock =
                                    0
                                SessionManagerUI.getInstance(this).isCardBlocked = it.status
                                //on success clevertap block captured
                                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                                    BaaSConstantsUI.CL_USER_BLOCK_CARD,
                                    BaaSConstantsUI.CL_USER_BLOCK_CARD_EVENT_ID,
                                    SessionManager.getInstance(this).accessToken,
                                    imei,
                                    mobileNumber,
                                    Date(),
                                    it.status!!,
                                    viewModel.getBlockCardReason()
                                )
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                                return
//                                SimpleCustomSnackbar.showSwitchMsg.make(
//                                    binding.nested, BaaSConstantsUI.CARD_BLOCKED_SUCCESS_MSG,
//                                    Snackbar.LENGTH_LONG, true
//                                )
//                                finish()
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
                        var errorUiRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                            it.message,
                            com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                        ) as com.payu.baas.coreUI.model.ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.nested,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
//                when (apiName) {
//                    ApiName.BLOCK_CARD -> {
//                        viewModel.blockCard()
//                    }
//                }
            }
        }
    }

    fun onSelectingReason(view: View) {
        when (view) {
            binding.ivLost -> {
                viewModel.setLostSelected()
            }
            binding.ivDamage -> {
                viewModel.setDamsgeSelected()
            }
            binding.ivChori -> {
                viewModel.setChoriSelected()
            }
            binding.ivOther -> {
                viewModel.setOtherSelected()
            }
        }
        binding.btSubmit.isEnabled = true
    }

    private fun goToPassCodeVerificationActivity() {
        SessionManagerUI.getInstance(this).otherCardBlockReason = viewModel.strOtherReason.get()
        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
            BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON
        )
        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, CODE)
    }

    fun clickActions(view: View) {

        when (view) {
            binding.lWarning.vOutsideWarning,
            binding.lWarning.ivCloseWarning -> {
                binding.lWarning.parent.visibility = View.GONE
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_BLOCK_CARD_DIALOG_CLOSED,
                    BaaSConstantsUI.CL_USER_BLOCK_CARD_DIALOG_CLOSED_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    ""
                )
            }
            binding.lWarning.btNo -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_BLOCK_CARD_CANCELLED,
                    BaaSConstantsUI.CL_USER_BLOCK_CARD_CANCELLED_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    ""
                )
                finish()
            }
            binding.lWarning.btBlock -> {
//                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
//                    BaaSConstantsUI.CL_USER_BLOCK_CARD,
//                    BaaSConstantsUI.CL_USER_BLOCK_CARD_EVENT_ID,
//                    SessionManager.getInstance(this).accessToken,
//                    imei,
//                    mobileNumber,
//                    Date(),
//                    "",
//                    ""
//                )
                CL_USER_FORGOT_PASSCODE_TEXT_CLICK = "2"
                CL_USER_VERIFY_PAN_BUTTON_CLICK = "2"
                CL_USER_RESET_PASSCODE_BUTTON_CLICK = "2"
//                binding.lWarning.parent.visibility = View.GONE
                goToPassCodeVerificationActivity()

//                SessionManagerUI.getInstance(this).otherCardBlockReason = viewModel.strOtherReason.get()
//                callNextScreen(Intent(this, PasscodeActivity::class.java), null)
//                finish()
            }
            binding.btSubmit -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_CARD_BLOCK_REASON,
                    BaaSConstantsUI.CL_USER_CARD_BLOCK_REASON_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    viewModel.getBlockCardReason()
                )
//                callNextScreen(Intent(this, AdressActivity::class.java), null)
//                var isAddressConfirmedBeforeCardBlock =
//                    SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock
//                if (isAddressConfirmedBeforeCardBlock == 1) {
//                    binding.lWarning.parent.visibility = View.VISIBLE
//                } else {
//                    binding.lWarning.parent.visibility = View.GONE
////                    isAddressSelected = true
//                    callNextScreen(Intent(this, AdressActivity::class.java), null)
//                }


                goToAddressActivity()
            }
            binding.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_CARD_BLOCK_REASON_GO_BACK,
            BaaSConstantsUI.CL_USER_CARD_BLOCK_REASON_GO_BACK_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            viewModel.getBlockCardReason()
        )
        finish()
    }

    private fun goToAddressActivity() {
        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
            BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON
        )
        val intent = Intent(this, AdressActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, REASON_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE) {
                viewModel.blockCard()
            } else if (requestCode == REASON_CODE) {
                binding.lWarning.parent.visibility = View.VISIBLE
            } else {
                binding.lWarning.parent.visibility = View.GONE
            }
//            else{
//                var isAddressConfirmedBeforeCardBlock =
//                    SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock
//                if (isAddressConfirmedBeforeCardBlock == 1) {
//                    binding.lWarning.parent.visibility = View.VISIBLE
//                } else {
//                    binding.lWarning.parent.visibility = View.GONE
//                }
//            }
        }
    }

//    override fun onResume() {
//        super.onResume()
////        var isAddressConfirmedBeforeCardBlock =
////            SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock
////        if (isAddressConfirmedBeforeCardBlock == 1) {
////            binding.lWarning.parent.visibility = View.VISIBLE
////        } else {
////            binding.lWarning.parent.visibility = View.GONE
////
////        }
//    }
}

