package com.payu.baas.coreUI.view.ui.activity.addressAddNew

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityAddNewAddressBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetAddressResponse
import com.payu.baas.core.model.responseModels.UpdateAddressResponse
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.address.AdressActivity
import com.payu.baas.coreUI.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*


class AddNewAddressActivity : BaseActivity() {
    lateinit var binding: ActivityAddNewAddressBinding
    private lateinit var viewModel: AddNewAddressViewModel
    var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_address)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        intent.extras?.let {
            if (it.containsKey(BaaSConstantsUI.ARGUMENTS_ADDRESS_DETAILS)) {
                val addressDetails =
                    it.getString(BaaSConstantsUI.ARGUMENTS_ADDRESS_DETAILS) as String
                if (addressDetails.isNotEmpty()) {
                    val addressModel =
                        Gson().fromJson(addressDetails, GetAddressResponse::class.java)

                    viewModel.strPinCode.set(addressModel.pinCode)
                    viewModel.strAddressLine1.set(addressModel.addressLine1)
                    viewModel.strAddressLine2.set(addressModel.addressLine2)
                    viewModel.strCity.set(addressModel.city)
                    viewModel.strState.set(addressModel.stateId)
                }
            }
        }
        onTextFilled(binding.etAddressLine1)
        onTextFilled(binding.etAddressLine2)
        onTextFilled(binding.etCity)
        onTextFilled(binding.etPinCode)
        onTextFilled(binding.etState)
    }

    private fun setupObserver() {
        viewModel.getAddressResponseObs().observe(this, {
            parseResponse(it, ApiName.UPDATE_USER_ADDRESS)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddNewAddressViewModel.AddNewAddressViewModelFactory(this, this)
        )[AddNewAddressViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei

    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_USER_ADDRESS -> {
                        it.data?.let {
                            if (it is UpdateAddressResponse) {
//
//                                var cardDeliveryAddress = CardDeliveryAddressModel()
//                                cardDeliveryAddress.pinCode =
//                                    binding.etPinCode.text.toString().trim()
//                                cardDeliveryAddress.addressLine1 =
//                                    binding.etAddressLine1.text.toString().trim()
//                                cardDeliveryAddress.addressLine2 =
//                                    binding.etAddressLine2.text.toString().trim()
////                                    cardDeliveryAddress.landmark = binding.etLandmark.text.toString().trim()
//                                cardDeliveryAddress.city = binding.etCity.text.toString().trim()
//                                cardDeliveryAddress.state = binding.etState.text.toString().trim()
//                                SessionManagerUI.getInstance(this).cardDeliveryAddress =
//                                    JsonUtil.toString(cardDeliveryAddress)
//                                SessionManagerUI.getInstance(this).isDeliveryAddressUpdated =
//                                    "1"  // 1 after updating the address
//
                                SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock =
                                    1
                                finish()
//                                if (intent!=null && userFromCardBlockModule()==true){
//                                    setResult(Activity.RESULT_OK, intent)
//                                    finish()
//                                    return
//                                }else{
//                                    finish()
//                                }
                            }
                        }
//                        }
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
                        binding.container,
                        msg,
                        false
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.UPDATE_USER_ADDRESS -> {
                        viewModel.addNewAddress()
                    }
                }
            }
        }
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
                    !viewModel.strCity.get().isNullOrEmpty() &&
                    (viewModel.strPinCode.get()!!.length == 6)
                ) {
                    binding.btSave.isEnabled = true
                } else
                    binding.btSave.isEnabled = false
            }
        })
    }

    fun openPreviousScreen(view: android.view.View) {

//        finish()
    }

    override fun onBackPressed() {
        if (userFromCardBlockModule()) {
            viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                BaaSConstantsUI.CL_USER_ADD_NEW_ADDRESS_CARD_BLOCK_GO_BACK,
                BaaSConstantsUI.CL_USER_ADD_NEW_ADDRESS_CARD_BLOCK_GO_BACK_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(),
                "",
                ""
            )
        } else {

        }
        callNextScreen(Intent(this, AdressActivity::class.java), null, true)
    }

    fun onUpdateAddress(view: View) {
        if (viewModel.strPinCode.get().isNullOrEmpty()
            || viewModel.strAddressLine1.get().isNullOrEmpty()
            || viewModel.strAddressLine2.get().isNullOrEmpty()
            || viewModel.strCity.get().isNullOrEmpty()
            || viewModel.strState.get().isNullOrEmpty()
        ) {
            SimpleCustomSnackbar.addressChange.make(
                binding.container,
                getString(R.string.enter_full_address),
                Snackbar.LENGTH_LONG
            )?.show()
            return
        }

        viewModel.addNewAddress()
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
}