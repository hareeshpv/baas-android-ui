package com.payu.baas.core.view.ui.activity.reset_passcode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityResetPasscodeBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.UpdatePasswordResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_PASSCODE_VARIABLE_LOGIC
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_BUTTON_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.CL_USER_RESET_PASSCODE_PROFILE_CLICK
import com.payu.baas.core.util.BaaSConstantsUI.PROFILE_SNACKBAR_FLAG_KEY
import com.payu.baas.core.util.BaaSConstantsUI.PROFILE_SNACKBAR_FLAG_VALUE
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.core.view.ui.activity.profile.UserProfileActivity
import java.util.*

class ResetPasscodeActivity : BaseActivity() {
    lateinit var binding: ActivityResetPasscodeBinding
    private lateinit var viewModel: ResetPasscodeViewModel
    private var oldPasscode: String? = null
    var mobileNumber: String? = null

    //string declaration for enter passcode
    private var enterPasscodeString1: String? = null
    private var enterPasscodeString2: String? = null
    private var enterPasscodeString3: String? = null
    private var enterPasscodeString4: String? = null

    //string declaration for re-enter passcode
    private var reEnterPasscodeString1: String? = null
    private var reEnterPasscodeString2: String? = null
    private var reEnterPasscodeString3: String? = null
    private var reEnterPasscodeString4: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_passcode)
        activity = this
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getResetPasscodeResponseObs().observe(this) {
            parseResponse(it, ApiName.UPDATE_PASSWORD)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ResetPasscodeViewModel.ResetPasscodeViewModelFactory(this, this)
        )[ResetPasscodeViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.oldPasscode = oldPasscode
        viewModel.imeiNumber = imei

    }

    private fun setupUI() {
        showKeyboard(this, binding.enterPasscode1, InputType.TYPE_CLASS_NUMBER)
        oldPasscode = SessionManagerUI.getInstance(applicationContext).oldPasscode
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        passcodeConvertAstric()
        applyFocusOnPasscodeField()
        CL_USER_PASSCODE_VARIABLE_LOGIC = "1"


        if (userFromMoneyTransferModule()) {
            CL_USER_RESET_PASSCODE_BUTTON_CLICK = "4"
        } else {
//            viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
//                BaaSConstantsUI.CL_USER_RESET_PASSCODE,
//                BaaSConstantsUI.CL_USER_RESET_PASSCODE_EVENT_ID,
//                SessionManager.getInstance(this).accessToken,
//                imei,
//                mobileNumber,
//                Date()
//            )
        }
    }

    private fun applyFocusOnPasscodeField() {
        binding.enterPasscode1.background =
            ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
        binding.enterPasscode1.requestFocus()
        binding.enterPasscode1.isCursorVisible = true
        binding.enterPasscode2.background =
            ContextCompat.getDrawable(this, R.drawable.passcode_background)
        binding.enterPasscode3.background =
            ContextCompat.getDrawable(this, R.drawable.passcode_background)
        binding.enterPasscode4.background =
            ContextCompat.getDrawable(this, R.drawable.passcode_background)

        binding.enterPasscode2.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterPasscode2.text.toString().isEmpty()) {
                    binding.enterPasscode1.requestFocus()
                    binding.enterPasscode1.isCursorVisible = true
                    binding.enterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.enterPasscode2.clearFocus()


                    binding.enterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)

                }
            }
            false
        }

        binding.enterPasscode3.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterPasscode3.text.toString().isEmpty()) {
                    binding.enterPasscode2.requestFocus()
                    binding.enterPasscode2.isCursorVisible = true
                    binding.enterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.enterPasscode3.clearFocus()

                    binding.enterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)

                }
            }
            false
        }

        binding.enterPasscode4.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.enterPasscode4.text.toString().isEmpty()) {
                    binding.enterPasscode3.requestFocus()
                    binding.enterPasscode3.isCursorVisible = true
                    binding.enterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.enterPasscode4.clearFocus()

                    binding.enterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.enterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                }
            }
            false
        }
// deleting re-enter field
        binding.reEnterPasscode2.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.reEnterPasscode2.text.toString().isEmpty()) {
                    binding.reEnterPasscode1.requestFocus()
                    binding.reEnterPasscode1.isCursorVisible = true
                    binding.reEnterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.reEnterPasscode2.clearFocus()
                    binding.reEnterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)

                }
            }
            false
        }

        binding.reEnterPasscode3.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.reEnterPasscode3.text.toString().isEmpty()) {
                    binding.reEnterPasscode2.requestFocus()
                    binding.reEnterPasscode2.isCursorVisible = true
                    binding.reEnterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.reEnterPasscode3.clearFocus()

                    binding.reEnterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)

                }
            }
            false
        }

        binding.reEnterPasscode4.setOnKeyListener { _, keyCode, event -> //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                //this is for backspace
                if (binding.reEnterPasscode4.text.toString().isEmpty()) {
                    binding.reEnterPasscode3.requestFocus()
                    binding.reEnterPasscode3.isCursorVisible = true
                    binding.reEnterPasscode3.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                    binding.reEnterPasscode4.clearFocus()

                    binding.reEnterPasscode1.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode2.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)
                    binding.reEnterPasscode4.background =
                        ContextCompat.getDrawable(this, R.drawable.passcode_background)


                }
            }
            false
        }



        binding.enterPasscode1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterPasscodeString1 = binding.enterPasscode1.text.toString().trim()
                if (enterPasscodeString1?.length == 1) {
                    moveToNext()
                }
            }
        })
        binding.enterPasscode2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                enterPasscodeString2 = binding.enterPasscode2.text.toString().trim()
                if (enterPasscodeString2?.length == 1) {
                    moveToNext()
                }

            }
        })
        binding.enterPasscode3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterPasscodeString3 = binding.enterPasscode3.text.toString().trim()
                if (enterPasscodeString3?.length == 1) {
                    moveToNext()
                }
            }
        })
        binding.enterPasscode4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enterPasscodeString4 = binding.enterPasscode4.text.toString().trim()
                if (enterPasscodeString4?.length == 1) {
                    moveToNext()
                    binding.reEnterPasscode1.background =
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.passcode_white_border_view
                        )
                    binding.reEnterPasscode1.requestFocus()
                    binding.reEnterPasscode1.isCursorVisible = true

                    binding.reEnterPasscode2.background =
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.passcode_background
                        )
                    binding.reEnterPasscode3.background =
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.passcode_background
                        )
                    binding.reEnterPasscode4.background =
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.passcode_background
                        )
                }

                oldPasscode =
                    """$enterPasscodeString1$enterPasscodeString2$enterPasscodeString3$enterPasscodeString4"""

            }
        })

        binding.reEnterPasscode1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                reEnterPasscodeString1 = binding.reEnterPasscode1.text.toString().trim()
                if (reEnterPasscodeString1?.length == 1) {
                    moveToNext()
                }

            }
        })
        binding.reEnterPasscode2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                reEnterPasscodeString2 = binding.reEnterPasscode2.text.toString().trim()
                if (reEnterPasscodeString2?.length == 1) {
                    moveToNext()
                }


            }
        })
        binding.reEnterPasscode3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                reEnterPasscodeString3 = binding.reEnterPasscode3.text.toString().trim()
                if (reEnterPasscodeString3?.length == 1) {
                    moveToNext()

                }
            }
        })
        binding.reEnterPasscode4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                reEnterPasscodeString4 = binding.reEnterPasscode4.text.toString().trim()
                if (reEnterPasscodeString4?.length == 1) {
                    moveToNext()

                }


                if (!enterPasscodeString1.isNullOrEmpty() && !enterPasscodeString2.isNullOrEmpty() && !enterPasscodeString3.isNullOrEmpty() && !enterPasscodeString4.isNullOrEmpty() && !reEnterPasscodeString1.isNullOrEmpty() && !reEnterPasscodeString2.isNullOrEmpty() && !reEnterPasscodeString3.isNullOrEmpty() && !reEnterPasscodeString4.isNullOrEmpty()) {
                    checkMatchingEnterRenterReSetPasscode()
                } else if (!reEnterPasscodeString1.isNullOrEmpty() && !reEnterPasscodeString2.isNullOrEmpty() && !reEnterPasscodeString3.isNullOrEmpty() && !reEnterPasscodeString4.isNullOrEmpty()) {
                    binding.resetPasscode.isEnabled = true
                }
            }
        })
    }

    private fun passcodeConvertAstric() {
        //fro enter passcode
        binding.enterPasscode1.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterPasscode2.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterPasscode3.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.enterPasscode4.transformationMethod = AsteriskPasswordTransformationMethod()
        // for re-enter passcode
        binding.reEnterPasscode1.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.reEnterPasscode2.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.reEnterPasscode3.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.reEnterPasscode4.transformationMethod = AsteriskPasswordTransformationMethod()
    }

    fun re_setpasscode_backarrow(view: View) {
        onBackPressed()
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_PASSWORD -> {
                        it.data?.let {
                            if (it is UpdatePasswordResponse) {

                                if (userFromMoneyTransferModule()) {
                                    //From Money Transfer Module
                                    finish()
                                }
                                if (CL_USER_RESET_PASSCODE_BUTTON_CLICK == "1") {
                                    viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                                        BaaSConstantsUI.CL_USER_RESET_PASSCODE_FROM_PROFILE,
                                        BaaSConstantsUI.CL_USER_PASSCODE_RESET_SUCCESS_PROFILE_EVENT_ID,
                                        SessionManager.getInstance(this).accessToken,
                                        imei,
                                        mobileNumber,
                                        Date(),
                                        ""
                                    )
                                }


                                if (userFromChangeTransactionLimitModule()) {
                                    setResult(Activity.RESULT_CANCELED, intent)
                                    finish()
                                } else if (userFromCardBlockModule()) {
                                    finish()
                                }else{
                                    if (CL_USER_RESET_PASSCODE_PROFILE_CLICK == "1") {
                                        PROFILE_SNACKBAR_FLAG_VALUE = "1"
                                        var bundle = Bundle()
                                        bundle.putString(
                                            PROFILE_SNACKBAR_FLAG_KEY,
                                            PROFILE_SNACKBAR_FLAG_VALUE
                                        )
                                        var intent = Intent(
                                            applicationContext,
                                            UserProfileActivity::class.java
                                        )
                                        intent.putExtras(bundle)
                                        callNextScreen(intent, null,true)
                                    } else {
                                        callNextScreen(
                                            Intent(this, PasscodeActivity::class.java),
                                            null,true
                                        )
                                    }
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
                        binding.rlParent,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_PASSWORD -> {
                        viewModel.resetPasscode()
                    }
                }
            }
        }
    }

    // matching entered code with re-entered
    private fun checkMatchingEnterRenterReSetPasscode() {
        if (enterPasscodeString1 != reEnterPasscodeString1 || enterPasscodeString2 != reEnterPasscodeString2 || enterPasscodeString3 != reEnterPasscodeString3 || enterPasscodeString4 != reEnterPasscodeString4) {
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = getString(R.string.set_passcode_matching_error)
            binding.resetPasscode.isEnabled = false
            binding.reEnterPasscode1.requestFocus()
            binding.reEnterPasscode1.isCursorVisible = true
            binding.reEnterPasscode1.text = null
            binding.reEnterPasscode2.text = null
            binding.reEnterPasscode3.text = null
            binding.reEnterPasscode4.text = null
            binding.reEnterPasscode4.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
            binding.reEnterPasscode3.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
            binding.reEnterPasscode2.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
            binding.reEnterPasscode1.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.passcode_white_border_view)
        } else {
            binding.resetPasscode.isEnabled = true
            binding.errorMessage.visibility = View.GONE
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

    private fun moveToNext() {
        when {

            binding.enterPasscode2.text.toString().isEmpty() -> {
                binding.enterPasscode1.clearFocus()
                binding.enterPasscode2.requestFocus()
                binding.enterPasscode2.isCursorVisible = true
                binding.enterPasscode2.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.passcode_white_border_view
                )
                binding.enterPasscode4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
                binding.enterPasscode3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
                binding.enterPasscode1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
            }
            binding.enterPasscode3.text.toString().isEmpty() -> {
                binding.enterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                binding.enterPasscode3.requestFocus()
                binding.enterPasscode2.clearFocus()

                binding.enterPasscode3.isCursorVisible = true

                binding.enterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)

            }
            binding.enterPasscode4.text.toString().isEmpty() -> {
                binding.enterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode4.requestFocus()
                binding.enterPasscode3.clearFocus()

                binding.enterPasscode4.isCursorVisible = true

                binding.enterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
            }
            //For ReEnter Passcode

            binding.reEnterPasscode2.text.toString().isEmpty() -> {
                binding.reEnterPasscode1.clearFocus()
                binding.reEnterPasscode2.requestFocus()
                binding.reEnterPasscode2.isCursorVisible = true
                binding.reEnterPasscode2.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.passcode_white_border_view
                )
                binding.reEnterPasscode4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
                binding.reEnterPasscode3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
                binding.reEnterPasscode1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.passcode_background)
            }
            binding.reEnterPasscode3.text.toString().isEmpty() -> {
                binding.reEnterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                binding.reEnterPasscode3.requestFocus()
                binding.reEnterPasscode2.clearFocus()

                binding.reEnterPasscode3.isCursorVisible = true

                binding.reEnterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)

            }
            binding.reEnterPasscode4.text.toString().isEmpty() -> {
                binding.reEnterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_white_border_view)
                binding.reEnterPasscode4.requestFocus()
                binding.reEnterPasscode3.clearFocus()

                binding.reEnterPasscode4.isCursorVisible = true

                binding.reEnterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
            }
            else -> {
                binding.enterPasscode1.clearFocus()
                binding.enterPasscode1.clearFocus()
                binding.enterPasscode3.clearFocus()
                binding.enterPasscode4.clearFocus()

                binding.enterPasscode1.isCursorVisible = false
                binding.enterPasscode2.isCursorVisible = false
                binding.enterPasscode3.isCursorVisible = false
                binding.enterPasscode4.isCursorVisible = true

                binding.enterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.enterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode1.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode2.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode3.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
                binding.reEnterPasscode4.background =
                    ContextCompat.getDrawable(this, R.drawable.passcode_background)
            }
        }
    }

    override fun onBackPressed() {
        if (userFromMoneyTransferModule()) {
            cleverTapUserMoneyTransferEvent(
                BaaSConstantsUI.CL_GO_BACK_RESET_FP_MONEY_TRANSFER,
                BaaSConstantsUI.CL_GO_BACK_RESET_FP_MONEY_TRANSFER_EVENT_ID,
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
            viewModel.baseCallBack!!.cleverTapUserOnBoardingEvent(
                BaaSConstantsUI.CL_USER_BACK_RESET_PASSCODE,
                BaaSConstantsUI.CL_USER_BACK_RESET_PASSCODE_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date()
            )
        }
        super.onBackPressed()
    }
}