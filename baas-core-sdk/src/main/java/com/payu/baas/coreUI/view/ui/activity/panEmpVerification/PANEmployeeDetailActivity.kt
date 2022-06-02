package com.payu.baas.coreUI.view.ui.activity.panEmpVerification

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityPanemployeeDetailBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.coreUI.model.entities.model.PanDetailsModelUI
import com.payu.baas.core.model.model.CardDeliveryAddressModel
import com.payu.baas.core.model.responseModels.VerifyEmployeeResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.kyc.CardDeliveryAddressDetailActivity
import com.payu.baas.coreUI.view.ui.activity.notVerifiedEmp.NotVerifiedEmpActivity
import com.payu.baas.coreUI.view.ui.activity.notemployee.NotEmployeeActivity
import com.payu.baas.coreUI.view.ui.activity.relogin.ReloginActivity
import java.util.*

class PANEmployeeDetailActivity : BaseActivity() {
    lateinit var binding: ActivityPanemployeeDetailBinding
    private lateinit var viewModel: PanEmpVerificationViewModel

    //    var cal = Calendar.getInstance()
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_panemployee_detail)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
//        onTextFilled(binding.etDOB)
//        onTextFilled(binding.etFullName)
        onTextFilled(binding.parentPanInput.etPanNumber)
//        onTextFilled(binding.parentEmpInput.etEmployeeId)
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.strEmpId.set(SessionManagerUI.getInstance(applicationContext).userEmpId)
//        validatePANEmployeeBaseCallback = this
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
                                    Toast.makeText(this, it.verificationStatus, Toast.LENGTH_LONG)
                                        .show()
                                    SessionManagerUI.getInstance(this).emailId = it.email
                                    savePanAndCardDetails(
                                        it.firstName!!, it.lastName!!, it.dob!!,
                                        it.addressLine1!!,
                                        it.addressLine2!!, it.city!!, it.stateId!!, it.pinCode!!
                                    )
                                } else {
                                    Toast.makeText(this, it.userMessage, Toast.LENGTH_LONG)
                                        .show()
                                    var intent = Intent(this, NotEmployeeActivity::class.java)
//                                    intent.putExtra(
//                                        BaaSConstantsUI.BS_KEY_NAME,
//                                        binding.etFullName.text.toString()
//                                    )
                                    intent.putExtra(
                                        BaaSConstantsUI.BS_KEY_EMPLOYEE_ID,
                                        binding.parentEmpInput.etEmployeeId.text.toString()
                                    )
                                    intent.putExtra(
                                        BaaSConstantsUI.BS_KEY_PAN_NUMBER,
                                        binding.parentPanInput.etPanNumber.text.toString()
                                    )
                                    callNextScreen(
                                        intent, null
                                    )
                                    finish()
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
                }else  if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ){
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
//                    when (apiName) {
//                        ApiName.VERIFY_EMPLOYEE -> {
                    var errorRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                        it.message,
                        com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                    ) as com.payu.baas.coreUI.model.ErrorResponseUI
                    var userMessage = errorRes.userMessage
//                            var userName = errorRes.userName
                    if (userMessage!!.equals(
                            BaaSConstantsUI.EMP_NOT_VERIFIED,
                            false
                        )
                    ) {
                        // EMP_NOT_VERIFIED
                        var intent = Intent(this, NotVerifiedEmpActivity::class.java)
                        callNextScreen(
                            intent, null, true
                        )
                    } else if (userMessage!!.equals(BaaSConstantsUI.MOBILE_MISMATCH, true)) {
                        //MOBILE_MISMATCH
                        var intent = Intent(this, ReloginActivity::class.java)
                        intent.putExtra(
                            BaaSConstantsUI.MOBILE_MISMATCH_KEY,
                            userMessage
                        )
//                                intent.putExtra(
//                                    BaaSConstantsUI.USER_NAME,
//                                    viewModel.strName.get().toString().trim()
//                                )
                        callNextScreen(
                            intent, null, true
                        )
                    } else if (userMessage!!.equals(BaaSConstantsUI.PAN_MISMATCH, true)) {
                        //PAN_MISMATCH
                        var intent = Intent(this, NotEmployeeActivity::class.java)
//                                intent.putExtra(
//                                    BaaSConstantsUI.BS_KEY_NAME,
//                                   viewModel.strName.get().toString()
//                                )
                        intent.putExtra(
                            BaaSConstantsUI.BS_KEY_EMPLOYEE_ID,
                            viewModel.strEmpId.get().toString()
                        )
                        intent.putExtra(
                            BaaSConstantsUI.BS_KEY_PAN_NUMBER,
                            viewModel.strPanNumber.get().toString()
                        )
                        callNextScreen(
                            intent, null, true
                        )
                    } else {
                        UtilsUI.showSnackbarOnSwitchAction(
                            binding.rlParent,
                            userMessage,
                            false
                        )
                    }
//                        }
//                    }
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
            PanEmpVerificationViewModel.PanViewModelFactory(this, this)
        )[PanEmpVerificationViewModel::class.java]

        binding.viewModel = viewModel
    }

    fun openPreviousScreen(view: View) {
        finish()
        viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_BACK_PAN_EMPLOYEE,
            BaaSConstantsUI.CL_USER_BACK_PAN_EMPLOYEE_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )
    }

    fun savePanAndCardDetails(
        firstName: String,
        lastName: String,
        dob: String,
        addressLine1: String,
        addressLine2: String,
        city: String,
        state: String,
        pinCode: String
    ) {
        hideKeyboard(this)
        var panDetails = com.payu.baas.coreUI.model.entities.model.PanDetailsModelUI()
        panDetails.dob = dob
        panDetails.firstName = firstName
        panDetails.lastName = lastName
        panDetails.employeeId = viewModel.strEmpId.get().toString().trim()
        panDetails.panNumber = viewModel.strPanNumber.get().toString().trim()
        SessionManagerUI.getInstance(this).userPanDetails = com.payu.baas.coreUI.util.JsonUtil.toString(panDetails)
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
            com.payu.baas.coreUI.util.JsonUtil.toString(cardDeliveryAddress)
        callNextScreen(Intent(this, CardDeliveryAddressDetailActivity::class.java), null)
        finish()
    }

    fun showOrHidePanErrorMessage(value: Int) {
        binding.parentPanInput.tvPanError.visibility = value
        binding.parentPanInput.errorIcon.visibility = value
    }


    fun openPanCardFormat(view: View) {
        hideKeyboard(this)
        binding.parentPanHelp.parentPan.visibility = View.VISIBLE
        binding.parentEmployeeIdHelp.parentIdCard.visibility = View.GONE
    }

    fun openIDCardFormat(view: View) {
        hideKeyboard(this)
        binding.parentPanHelp.parentPan.visibility = View.GONE
        binding.parentEmployeeIdHelp.parentIdCard.visibility = View.VISIBLE
    }

    fun closeHelp(view: View) {
        when (view) {
            binding.parentPanHelp.ivClosePanHelp,
            binding.parentPanHelp.vOutsidePan -> {
                binding.parentPanHelp.parentPan.visibility = View.GONE
            }
            binding.parentEmployeeIdHelp.ivCloseIdCardHelp,
            binding.parentEmployeeIdHelp.vOutsideId -> {
                binding.parentEmployeeIdHelp.parentIdCard.visibility = View.GONE
            }
        }
    }

//    fun openDatePicker(view: View) {
//        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
//            override fun onDateSet(
//                view: DatePicker, year: Int, monthOfYear: Int,
//                dayOfMonth: Int
//            ) {
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                updateDateInView()
//            }
//        }
//        DatePickerDialog(
//            this@PANEmployeeDetailActivity, R.style.datepicker,
//            dateSetListener,
//            // setting DatePickerDialog to point to today's date when it loads up
//            cal.get(Calendar.YEAR),
//            cal.get(Calendar.MONTH),
//            cal.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }
//
//    private fun updateDateInView() {
//        val sdf = BaaSConstantsUI.DD_MM_YYYY
//        binding.etDOB.setText(sdf.format(cal.time))
//    }

    private fun onTextFilled(edittext: EditText) {
        edittext.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (edittext == binding.parentPanInput.etPanNumber) {
                    if (validatePanNumber(viewModel.strPanNumber.get().toString())) {
                        showOrHidePanErrorMessage(View.GONE)
                        binding.btNext.isEnabled = true
                    } else {
                        showOrHidePanErrorMessage(View.VISIBLE)
                        binding.btNext.isEnabled = false
                    }
                    return
                }
                if (!viewModel.strPanNumber.get().isNullOrEmpty()
                )
                    binding.btNext.isEnabled = true
                else
                    binding.btNext.isEnabled = false

//                binding.parentPanInput.etPanNumber.setOnEditorActionListener { textView, i, keyEvent ->
//                    if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
//                        if (validatePanNumber(viewModel.strPanNumber.get().toString())) {
//                            showOrHidePanErrorMessage(View.GONE)
//                            binding.btNext.isEnabled = true
//                        }else {
//                            showOrHidePanErrorMessage(View.VISIBLE)
//                            binding.btNext.isEnabled = false
//                        }
//                    }
//                    false
//                }
            }
        })
    }

}