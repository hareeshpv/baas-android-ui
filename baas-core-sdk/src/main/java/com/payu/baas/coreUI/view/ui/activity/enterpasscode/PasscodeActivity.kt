package com.payu.baas.coreUI.view.ui.activity.enterpasscode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityPasscodeBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.responseModels.LoginResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.dashboard.DashboardActivity
import com.payu.baas.coreUI.view.ui.activity.panvalidate.VerifyPANCardNumberActivity
import java.util.*

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TEXT_CLICK
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.coreUI.util.Resource
import com.payu.baas.coreUI.util.Status
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.view.ui.activity.reset_passcode.ResetPasscodeActivity


class PasscodeActivity : BaseActivity() {
    lateinit var binding: ActivityPasscodeBinding
    private lateinit var viewModel: EnterPasscodeViewModel
    var mobileNumber: String? = null
    var pincode: String? = null

    //    lateinit var enterPasscodeBaseCallback: BaseCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passcode)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showKeyboard()
    }

    private fun setupObserver() {
        viewModel.getEnterPasscodeResponseObs().observe(this) {
            parseResponse(it, ApiName.LOGIN)
        }
//        viewModel.getBlockResponseObs().observe(this) {
//            parseResponse(it, ApiName.BLOCK_CARD)
//        }
//        viewModel.getLimitResponseObs().observe(this) {
//            parseResponse(it, ApiName.SET_LIMITS)
//        }
        viewModel.getAccessTokenResponseObs().observe(this) {
            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            EnterPasscodeViewModel.PasscodeViewModelFactory(this, this)
        )[EnterPasscodeViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.mobile_num = mobileNumber
        pincode = binding.editTextEnterMpin.text.toString()
        if (pincode!!.length == 4) {
            viewModel.passcode()
        }
        /*binding.editTextEnterMpin.requestFocus()
        binding.editTextEnterMpin.isFocusable = true
        binding.editTextEnterMpin.isFocusableInTouchMode = true*/
        enterPasscodeLogic()

    }


    //For entering pin
    private fun enterPasscodeLogic() {
        binding.editTextEnterMpin.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                viewModel.passcode()
            }
            false
        }
        binding.editTextEnterMpin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                when (s!!.length) {
                    1 -> {
                        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                        binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
                        clearPin(2)
                    }
                    2 -> {
                        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                        binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
                        clearPin(3)
                    }
                    3 -> {
                        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                        binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
                        clearPin(4)
                    }
                    4 -> {
                        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                        binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                    }
                    else -> {
                        binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
                        clearPin(1)
                    }
                }
            }
        })

    }

    /**
     * Here we clear pin when user click on keyboard backpress button
     */
    private fun clearPin(start: Int) {
        for (i in start..4) {
            when (i) {
                1 -> {
                    binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                }
                2 -> {
                    binding.imageviewCircle2.setImageResource(R.drawable.ring_circle)
                }
                3 -> {
                    binding.imageviewCircle3.setImageResource(R.drawable.ring_circle)
                }
                4 -> {
                    binding.imageviewCircle4.setImageResource(R.drawable.ring_circle)
                }
                else -> {
                    binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                }
            }
        }
    }


    private fun clearPin() {
        binding.editTextEnterMpin.text?.clear()
        binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
        binding.imageviewCircle2.setImageResource(R.drawable.ring_circle)
        binding.imageviewCircle3.setImageResource(R.drawable.ring_circle)
        binding.imageviewCircle4.setImageResource(R.drawable.ring_circle)
        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
    }

    //for wrong pin
    private fun wrongPin() {
        binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji7)
        binding.passcodeError.setText(R.string.enter_correct_passcode)
        clearPin()
    }

    // for correct pin
    private fun correctPin() {
        val timer = Timer()
        val t: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji6) }
            }
        }
        timer.schedule(t, 2000)

        //For Money Transfer module we just have to verify passcode and
        //we just have to go back to parent activity when passcode verified successfully
        if (userFromMoneyTransferModule() || userFromCardBlockModule()
            || userFromChangeTransactionLimitModule() || userFromAdvanceModule()
        ) {
            setResult(Activity.RESULT_OK, intent)
            finish()
            return
        }
        SessionManagerUI.getInstance(this).userStatusCode =
            UserState.LOGIN_DONE.getValue()
        if (CL_USER_RESET_PASSCODE_BUTTON_CLICK == "1") {
            callNextScreen(Intent(applicationContext, ResetPasscodeActivity::class.java), null)
            finish()
        } else {
            callNextScreen(Intent(applicationContext, DashboardActivity::class.java), null)
            finish()
        }


    }

    fun forgot_passcode_backarrow(view: View) {
        onBackPressed()

    }

    fun forgot_passcode(view: View) {
        var bundle : Bundle? = null

        when (CL_USER_FORGOT_PASSCODE_TEXT_CLICK) {
            //Profile
            "1" -> {
                viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                    BaaSConstantsUI.CL_USER_PASSCODE_FROM_PROFILE,
                    BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_PROFILE_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    ""
                )
            }
            //Block
            "2" -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_BLOCK_CARD,
                    BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_BLOCK_CARD_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    ""
                )
                bundle = intent.extras!!
            }
            //Change limits
            "3" -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TRANSACTION_LIMIT,
                    BaaSConstantsUI.CL_USER_FORGOT_PASSCODE_TRANSACTION_LIMIT_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    "",
                    ""
                )
                bundle = intent.extras!!
            }
            else -> {
                if (userFromMoneyTransferModule()==true) {
                    cleverTapUserMoneyTransferEvent(
                        BaaSConstantsUI.CL_FORGOT_PASSCODE_MONEY_TRANSFER,
                        BaaSConstantsUI.CL_FORGOT_PASSCODE_MONEY_TRANSFER_EVENT_ID,
                        SessionManager.getInstance(this).accessToken,
                        imei,
                        SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                        Date(),
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                    bundle = intent.extras!!
                }else {
                    viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                        BaaSConstantsUI.CL_USER_FORGOT_PASSCODE,
                        BaaSConstantsUI.CL_USER_ONBOARDING_FORGOT_PASSCODE_EVENT_ID,
                        SessionManager.getInstance(this).accessToken,
                        imei,
                        mobileNumber,
                        Date()
                    )
                }
            }
        }

        callNextScreen(
            Intent(applicationContext, VerifyPANCardNumberActivity::class.java),
            bundle
        )
    }

    fun openKeyboard(view: View) {
        showKeyboard()
    }

    /*   fun clickOne(view: View) {
           if(pincode!!.length!=4){
               pincode += "1"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickTwo(view: View) {
           if(pincode!!.length!=4){
               pincode += "2"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickThree(view: View) {
           if(pincode!!.length!=4){
               pincode += "3"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickFour(view: View) {
           if(pincode!!.length!=4){
               pincode += "4"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickFive(view: View) {
           if(pincode!!.length!=4){
               pincode += "5"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickSix(view: View) {
           if(pincode!!.length!=4){
               pincode += "6"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickSeven(view: View) {
           if(pincode!!.length!=4){
               pincode += "7"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickEight(view: View) {
           if(pincode!!.length!=4){
               pincode += "8"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }
       fun clickNine(view: View) {
           if(pincode!!.length!=4){
               pincode += "9"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }

       fun clickZero(view: View) {
           if(pincode!!.length!=4){
               pincode += "0"
               when (pincode!!.length) {
                   1 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji2)
                       binding.imageviewCircle1.setImageResource(R.drawable.solid_circle)
   //                    clearPin(2)
                   }
                   2 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji3)
                       binding.imageviewCircle2.setImageResource(R.drawable.solid_circle)
   //                    clearPin(3)
                   }
                   3 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji4)
                       binding.imageviewCircle3.setImageResource(R.drawable.solid_circle)
   //                    clearPin(4)
                   }
                   4 -> {
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji5)
                       binding.imageviewCircle4.setImageResource(R.drawable.solid_circle)
                   }
                   else -> {
                       binding.imageviewCircle1.setImageResource(R.drawable.ring_circle)
                       binding.passcodeSmillyIcon.setImageResource(R.drawable.ic_emoji1)
   //                    clearPin(1)
                   }
               }
           }

       }*/
    fun clickDelete(view: View) {
        clearPin()
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.LOGIN -> {
                        it.data?.let {
                            if (it is LoginResponse) {
                                if (BaaSConstantsUI.CL_USER_PASSCODE_VARIABLE_LOGIC == "1") {
                                    viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                                        BaaSConstantsUI.CL_USER_LOGIN_AFTER_RESET_PASSCODE,
                                        BaaSConstantsUI.CL_USER_RESET_PASSCODE_EVENT_ID,
                                        SessionManager.getInstance(this).accessToken,
                                        imei,
                                        mobileNumber,
                                        Date()
                                    )
                                } else {
                                    viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                                        BaaSConstantsUI.CL_USER_LOGIN_POST_MOBILE_VERIFICATION,
                                        BaaSConstantsUI.CL_USER_EXISTING_USER_LOGIN_EVENT_ID,
                                        SessionManager.getInstance(this).accessToken,
                                        imei,
                                        mobileNumber,
                                        Date()
                                    )
                                }
                                correctPin()
                            }
                        }
                    }

//                    ApiName.BLOCK_CARD -> {
//                        it.data?.let {
//                            if (it is BlockCardResponse) {
//                                if (it.code.equals(BaaSConstantsUI.SUCCESS_CODE)) {
//                                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                                    val intent = Intent()
//                                    intent.putExtra(
//                                        BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON,
//                                        true
//                                    )
//                                    setResult(Activity.RESULT_OK, intent)
//                                    finish()
////                                this@PasscodeActivity.getParent().finish();
//                                }
//                            }
//                        }
//                    }
//                    ApiName.SET_LIMITS -> {
//                        it.data?.let {
//                            if (it is SetLimitResponse) {
//                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                                val intent = Intent()
//                                intent.putExtra(
//                                    BaaSConstantsUI.BS_KEY_IS_FROM_CHANGED_TRANSACTION_LIMIT_SCREEN,
//                                    BaaSConstantsUI.TRANSACTION_LIMIT_CHANGED_SUCCESS_MSG
//                                )
//                                setResult(Activity.RESULT_OK, intent)
//                                finish()
//                                this@PasscodeActivity.parent.finish()
//                            }
//                        }
//                    }
                    else -> {

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
                    viewModel.reGenerateAccessToken(true)
                } else  if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
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
                        if (msg.contains("Password you provided doesnt match with the records")) {
                            wrongPin()
                            return
                        }
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.rlParent,
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
//                    when (apiName) {
//                        ApiName.LOGIN -> {
//                            wrongPin()
//                        }
//                    }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.LOGIN -> {
                        viewModel.passcode()
                    }
                }
            }
        }
    }

    private fun userFromMoneyTransferModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == "MoneyTransfer"
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun userFromCardBlockModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun userFromChangeTransactionLimitModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == BaaSConstantsUI.BS_KEY_IS_FROM_CHANGED_TRANSACTION_LIMIT_SCREEN
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun userFromAdvanceModule(): Boolean {
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)) {
                if (it.getString(BaaSConstantsUI.ARGUMENTS_FROM_MODULE)
                    == BaaSConstantsUI.ARGUMENTS_ADVANCE_MODEL
                ) {
                    return true

                }
            }
        }
        return false
    }

    private fun showKeyboard() {
        showKeyboard(this, binding.editTextEnterMpin, InputType.TYPE_CLASS_NUMBER)
    }

    override fun onBackPressed() {
        if (userFromMoneyTransferModule()) {

            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_GO_BACK_ENTER_PASSCODE_MONEY_TRANSFER,
                BaaSConstantsUI.CL_GO_BACK_ENTER_PASSCODE_MONEY_TRANSFER_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                Date(),
                "",
                "",
                "",
                "",
                ""
            )
            finish()
        }else {
            finish()
            viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
                BaaSConstantsUI.CL_USER_BACK_ENTER_PASSCODE,
                BaaSConstantsUI.CL_USER_BACK_ENTER_PASSCODE_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date()
            )
        }
        super.onBackPressed()
    }


}