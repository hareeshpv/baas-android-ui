package com.payu.baas.core.view.ui.activity.notemployee

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityNotEmployeeBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.entities.model.PanDetailsModelUI
import com.payu.baas.core.model.model.CardDeliveryAddressModel
import com.payu.baas.core.model.responseModels.VerifyEmployeeResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.util.enums.UserState
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.kyc.CardDeliveryAddressDetailActivity
import java.util.*

class NotEmployeeActivity : BaseActivity() {
    lateinit var binding: ActivityNotEmployeeBinding
    private lateinit var viewModel: NotEmpViewModel

    //    lateinit var notEmployeeBaseCallback: BaseCallback
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_not_employee)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
        var extra = intent.extras
        if (extra != null) {
//            if (extra.containsKey(BaaSConstantsUI.BS_KEY_NAME)) {
//                viewModel.strName.set(extra.get(BaaSConstantsUI.BS_KEY_NAME).toString())
//                binding.name.setText("Dear " + extra.get(BaaSConstantsUI.BS_KEY_NAME).toString())
//            }
            if (extra.containsKey(BaaSConstantsUI.BS_KEY_EMPLOYEE_ID)) {
                viewModel.strEmpId.set(
                    extra.get(BaaSConstantsUI.BS_KEY_EMPLOYEE_ID).toString()
                )
            }
            if (extra.containsKey(BaaSConstantsUI.BS_KEY_PAN_NUMBER)) {
                viewModel.strPanNumber.set(extra.get(BaaSConstantsUI.BS_KEY_PAN_NUMBER).toString())
            }
        }
//        onTextFilled(binding.etFullName)
        onTextFilled(binding.parentPanInput.etPanNumber)
//        onTextFilled(binding.parentEmpInput.etEmployeeId)
//        notEmployeeBaseCallback = this
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_EMPLOYMENT_CHECKING,
            BaaSConstantsUI.CL_USER_EMPLOYMENT_CHECKING_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )

    }

    private fun onTextFilled(edittext: EditText) {
        edittext.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!viewModel.strPanNumber.get().isNullOrEmpty()
                )
                    binding.btReloadPage.isEnabled = true
                else
                    binding.btReloadPage.isEnabled = false
                binding.parentPanInput.etPanNumber.setOnEditorActionListener { textView, i, keyEvent ->
                    if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                        if (validatePanNumber(viewModel.strPanNumber.get().toString()))
                            showOrHidePanErrorMessage(View.GONE)
                        else
                            showOrHidePanErrorMessage(View.VISIBLE)
                    }
                    false
                }
            }
        })
    }

    private fun setupObserver() {
        viewModel.getPanEmpResponseObs().observe(this, {
            parseResponse(it, ApiName.VERIFY_EMPLOYEE)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.VERIFY_EMPLOYEE -> {
                        it.data?.let {
                            if (it is VerifyEmployeeResponse) {
                                if ((it as VerifyEmployeeResponse).verificationStatus.equals("EMPLOYMENT_VERIFIED")) {
                                    SessionManagerUI.getInstance(this).emailId = it.email
                                    savePanDetails(
                                        it.firstName!!,
                                        it.lastName!!,
                                        it.addressLine1!!,
                                        it.addressLine2!!,
                                        it.city!!,
                                        it.stateId!!,
                                        it.pinCode!!,
                                        it.dob!!
                                    )
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
                    viewModel.reGenerateAccessToken(true)
                } else  if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ){
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
                        msg =it.message!!
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
                    ApiName.VERIFY_EMPLOYEE -> {
                        viewModel.verifyPanEmp()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            NotEmpViewModel.NotEmpViewModelFactory(this, this)
        )[NotEmpViewModel::class.java]
        binding.viewModel = viewModel
    }

    fun savePanDetails(
        firstName: String,
        lastName: String,
        addressLine1: String,
        addressLine2: String,
        city: String,
        state: String,
        pinCode: String,
        dob: String
    ) {
        var panDetails = PanDetailsModelUI()
        panDetails.firstName = firstName
        panDetails.lastName = lastName
        panDetails.employeeId = viewModel.strEmpId.get().toString().trim()
        panDetails.panNumber = viewModel.strPanNumber.get().toString().trim()
        panDetails.dob = dob
        SessionManagerUI.getInstance(this).userPanDetails = JsonUtil.toString(panDetails)
        SessionManagerUI.getInstance(this).userStatusCode = UserState.PAN_SAVED_LOCAL.getValue()
        /* Saving address details in local not passing as intenet extra
           as in case user opens the card address screen directly from Complete kyc screen
           rather than going  from pan screen */
        var cardDeliveryAddress = CardDeliveryAddressModel()
        cardDeliveryAddress.pinCode = pinCode
        cardDeliveryAddress.addressLine1 = addressLine1
        cardDeliveryAddress.addressLine2 = addressLine2
        cardDeliveryAddress.landmark = ""
        cardDeliveryAddress.city = city
        cardDeliveryAddress.state = state
        SessionManagerUI.getInstance(this).cardDeliveryAddress =
            JsonUtil.toString(cardDeliveryAddress)
        callNextScreen(Intent(this, CardDeliveryAddressDetailActivity::class.java), null)
        finish()
    }

    fun showOrHidePanErrorMessage(value: Int) {
        binding.parentPanInput.tvPanError.visibility = value
        binding.parentPanInput.errorIcon.visibility = value
    }


    fun openPanCardFormat(view: View) {
        binding.parentPanHelp.parentPan.visibility = View.VISIBLE
        binding.parentEmployeeIdHelp.parentIdCard.visibility = View.GONE
    }

    fun openIDCardFormat(view: View) {
        binding.parentPanHelp.parentPan.visibility = View.GONE
        binding.parentEmployeeIdHelp.parentIdCard.visibility = View.VISIBLE
    }

    fun closeHelp(view: View) {
        when (view) {
            binding.parentPanHelp.ivClosePanHelp -> {
                binding.parentPanHelp.parentPan.visibility = View.GONE
            }
            binding.parentEmployeeIdHelp.ivCloseIdCardHelp -> {
                binding.parentEmployeeIdHelp.parentIdCard.visibility = View.GONE
            }
        }
    }

    fun openPreviousScreen(view: android.view.View) {
        finish()
    }

}