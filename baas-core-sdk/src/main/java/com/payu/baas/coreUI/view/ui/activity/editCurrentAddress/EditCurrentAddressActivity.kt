package com.payu.baas.coreUI.view.ui.activity.editCurrentAddress

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityEditCurrentAddressBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetAddressResponse
import com.payu.baas.core.model.responseModels.UpdateAddressResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.BaaSConstantsUI.EDIT_ADDRESS_SNACKBAR_FLAG_KEY
import com.payu.baas.coreUI.util.BaaSConstantsUI.EDIT_ADDRESS_SNACKBAR_FLAG_VALUE
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.myDetails.MyDetailsActivity
import com.payu.baas.coreUI.view.callback.AlertDialogMultipleCallback
import java.util.*

class EditCurrentAddressActivity : BaseActivity() {
    lateinit var binding: ActivityEditCurrentAddressBinding
    private lateinit var viewModel: EditCurrentAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_current_address)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupObserver() {
        viewModel.getAddressResponseObs().observe(this, {
            parseResponse(it, ApiName.UPDATE_USER_ADDRESS)
        })
        viewModel.getAddressResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_ADDRESS)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            EditCurrentAddressViewModel.EditCurrentAddressViewModelFactory(this, this)
        )[EditCurrentAddressViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setupUI() {
        onTextFilled(binding.etAddressLine1)
        onTextFilled(binding.etAddressLine2)
        onTextFilled(binding.etCity)
        onTextFilled(binding.etPinCode)
        onTextFilled(binding.etState)

    }

    fun openCurrentAddressPrevious(view: View) {
        finish()
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_GO_BACK_ENTER_ADDRESS_PROFILE,
            BaaSConstantsUI.CL_USER_GO_BACK_ENTER_ADDRESS_PROFILE_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei,
            "",
            Date(), ""
        )
    }

    fun resetCurrentAddress(view: View) {
        binding.etPinCode.setText("")
        binding.etAddressLine1.setText("")
        binding.etAddressLine2.setText("")
        binding.etCity.setText("")
        binding.etState.setText("")
        binding.btSave.isEnabled = false
    }

    private fun onTextFilled(edittext: EditText) {
        edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (!viewModel.strAddressLine1.get().isNullOrEmpty() &&
                    !viewModel.strAddressLine2.get().isNullOrEmpty() &&
                    !viewModel.strPinCode.get().isNullOrEmpty() &&
                    !viewModel.strState.get().isNullOrEmpty() &&
                    !viewModel.strCity.get().isNullOrEmpty()
                ){
                    if (viewModel.strPinCode.get()!!.length == 6)
                        binding.btSave.isEnabled = true
                    else
                        binding.btSave.isEnabled = false
                } else
                    binding.btSave.isEnabled = false
            }
//                binding.btSave.isEnabled = binding.etPinCode.hasFocus()||binding.etAddressLine1.hasFocus()||binding.etAddressLine2.hasFocus()||binding.etState.hasFocus()||binding.etCity.hasFocus()
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_USER_ADDRESS -> {
                        it.data?.let {
                            if (it is UpdateAddressResponse) {
                                viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                                    BaaSConstantsUI.CL_USER_CHANGE_ADDRESS,
                                    BaaSConstantsUI.CL_USER_CHANGE_ADDRESS_EVENT_ID,
                                    SessionManager.getInstance(applicationContext).accessToken, imei,
                                    SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                                    Date(),""
                                )
                                EDIT_ADDRESS_SNACKBAR_FLAG_VALUE = "1"
                                var bundle = Bundle()
                                bundle.putString(EDIT_ADDRESS_SNACKBAR_FLAG_KEY, EDIT_ADDRESS_SNACKBAR_FLAG_VALUE)
                                var intent = Intent(applicationContext, MyDetailsActivity::class.java)
                                intent.putExtras(bundle)
                                callNextScreen(intent, null,true)
                            }
                        }
                    }
                    ApiName.GET_ADDRESS -> {
                        it.data?.let {
                            if (it is GetAddressResponse) {
                                viewModel.strPinCode.set((it as GetAddressResponse).pinCode)
                                viewModel.strAddressLine1.set((it as GetAddressResponse).addressLine1)
                                viewModel.strAddressLine2.set((it as GetAddressResponse).addressLine2)
                                viewModel.strCity.set((it as GetAddressResponse).city)
                                viewModel.strState.set((it as GetAddressResponse).stateId)
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
                } else  if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ){
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    var errorUiRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                        it.message,
                        com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                    ) as com.payu.baas.coreUI.model.ErrorResponseUI
                    UtilsUI.showSnackbarOnSwitchAction(
                        binding.container,
                        errorUiRes.userMessage!!,
                        false
                    )
                }


            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_USER_ADDRESS -> {
                        viewModel.updateAddress()
                    }
                }
            }
        }
    }

    fun saveAddress(view: View) {
        binding.container.visibility = View.GONE
        showAlertWithMultipleCallBack(
            getString(R.string.save_address),
            getString(R.string.save_address_message),
            getString(R.string.yes_agreed),
            getString(R.string.nahi),
            object : AlertDialogMultipleCallback {
                override fun onPositiveActionButtonClick() {
                    viewModel.updateAddress()
                }
                override fun onNegativeActionButtonClick() {
                    viewModel.baseCallBack!!.cleverTapUserProfileEvent(
                        BaaSConstantsUI.CL_USER_GO_BACK_SAVE_ADDRESS_POPUP,
                        BaaSConstantsUI.CL_USER_GO_BACK_SAVE_ADDRESS_POPUP_EVENT_ID,
                        SessionManager.getInstance(activity!!).accessToken, imei,
                        SessionManagerUI.getInstance(applicationContext).userMobileNumber,
                        Date(),""
                    )
//                    callNextScreen(
//                        Intent(
//                            applicationContext,
//                            EditCurrentAddressActivity::class.java
//                        ), null
//                    )
                }
            }
        )
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserProfileEvent(
            BaaSConstantsUI.CL_USER_GO_BACK_SAVE_ADDRESS_POPUP,
            BaaSConstantsUI.CL_USER_GO_BACK_SAVE_ADDRESS_POPUP_EVENT_ID,
            SessionManager.getInstance(activity!!).accessToken, imei,
            SessionManagerUI.getInstance(applicationContext).userMobileNumber,
            Date(),""
        )
        super.onBackPressed()
    }
}