package com.payu.baas.core.view.ui.activity.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityAdressBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.GetAddressResponse
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.addressAddNew.AddNewAddressActivity
import java.util.*

class AdressActivity : BaseActivity() {
    lateinit var binding: ActivityAdressBinding
    private lateinit var viewModel: AddressViewModel
    var mobileNumber: String? = null

    private var addressResponseDetails: GetAddressResponse? = null
    var REASON_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_adress)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()

    }

    private fun setupUI() {
//        viewModel.getAddress()
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
    }

    private fun setupObserver() {
        viewModel.getAddressResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_ADDRESS)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddressViewModel.AddressViewModelFactory(this, this)
        )[AddressViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_ADDRESS -> {
                        it.data?.let {
                            if (it is GetAddressResponse) {
//                                if (!(it as GetAddressResponse).message.isNullOrEmpty()) {
//                                    Toast.makeText(this, it.message, Toast.LENGTH_LONG)
//                                        .show()
                                addressResponseDetails = it
                                viewModel.strAddress.set(
                                    it.addressLine1 + ", " + it.addressLine2 + ", " +
                                            it.city + ", " + it.stateId + ", " + it.pinCode
                                )
//                                }
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
                    ApiName.GET_ADDRESS -> {
                        viewModel.getAddress()
                    }
                }
            }
        }
    }

    fun addNewAddress(view: android.view.View) {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_ADD_NEW_ADDRESS,
            BaaSConstantsUI.CL_USER_ADD_NEW_ADDRESS_CARD_BLOCK_EVENT_ID,
           SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )

//        callNextScreen(Intent(this, AddNewAddressActivity::class.java), null,true)
        goToNewAddressActivity()
//        val intent = Intent(this, AddNewAddressActivity::class.java)
//        val bundle = Bundle()
//        bundle.putString(
//            BaaSConstantsUI.ARGUMENTS_ADDRESS_DETAILS,
//            Gson().toJson(addressResponseDetails)
//        )
//        callNextScreen(intent, bundle, true)
    }

    fun confirmAddress(view: android.view.View) {
        if (intent != null) {
            if (intent.hasExtra(BaaSConstantsUI.ARGUMENTS_CARD_RE_ORDER)) {
                val isFrom = intent.getStringExtra(BaaSConstantsUI.ARGUMENTS_CARD_RE_ORDER) as String
                if (isFrom == BaaSConstantsUI.ARGUMENTS_CARD_RE_ORDER) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } else if (userFromCardBlockModule() == true) {
//                SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock = 1
//                finish()

                setResult(Activity.RESULT_OK, intent)
                finish()
                return
            }
        }
//        else {
//            SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock = 1
////            finish()
//
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//            return
//        }
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

    fun openPreviousScreen(view: android.view.View) {
        onBackPressed()
    }
    override fun onBackPressed() {
        if(userFromCardBlockModule()) {
            viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                BaaSConstantsUI.CL_USER_CONFIRM_ADDRESS_TO_CARD_BLOCK_REASON_GO_BACK,
                BaaSConstantsUI.CL_USER_CONFIRM_ADDRESS_TO_CARD_BLOCK_REASON_GO_BACK_EVENT_ID,
                SessionManager.getInstance(this).accessToken,
                imei,
                mobileNumber,
                Date(),
                "",
                ""
            )
        }else{

        }
        finish()
    }

    private fun goToNewAddressActivity() {
        val bundle = Bundle()
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
            BaaSConstantsUI.BS_KEY_IS_FROM_CARD_BLOCK_REASON
        )
        bundle.putString(
            BaaSConstantsUI.ARGUMENTS_ADDRESS_DETAILS,
            Gson().toJson(addressResponseDetails)
        )
        val intent = Intent(this, AddNewAddressActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, REASON_CODE)
    }

    override fun onResume() {
        super.onResume()

        var isAddressUpdated = SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock
        if (isAddressUpdated == 1) {
            SessionManagerUI.getInstance(this).isAddressConfirmedBeforeCardBlock = 0
            setResult(Activity.RESULT_OK, intent)
            finish()
            return
        } else
            viewModel.getAddress()
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//        }
//    }

//    fun getAddress(): String {
//        SessionManagerUI.getInstance(this).isDeliveryAddressUpdated =
//            "0" //0 after updating resetting the value
//        var cardDeliveryAddressString = SessionManagerUI.getInstance(this).cardDeliveryAddress
//        if (!cardDeliveryAddressString.isNullOrEmpty()) {
//            var cardDeliveryAddress = JsonUtil.toObject(
//                cardDeliveryAddressString,
//                CardDeliveryAddressModel::class.java
//            ) as CardDeliveryAddressModel
//            viewModel.strAddress.set(
//                cardDeliveryAddress.addressLine1 + ", " + cardDeliveryAddress.addressLine2
//                        + ", " + cardDeliveryAddress.landmark + " " + cardDeliveryAddress.city
//                        + ", " + cardDeliveryAddress.state + ", " + cardDeliveryAddress.pinCode
//            )
//        }
//        return viewModel.strAddress.get().toString()
//    }
}