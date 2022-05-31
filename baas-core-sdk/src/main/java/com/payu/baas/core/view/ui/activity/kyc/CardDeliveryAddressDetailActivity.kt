package com.payu.baas.core.view.ui.activity.kyc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityCardDeliveryAddressDetailBinding
import com.payu.baas.core.model.model.CardDeliveryAddressModel
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.JsonUtil
import com.payu.baas.core.util.enums.UserState
import com.payu.baas.core.view.ui.BaseActivity
import java.util.*

class CardDeliveryAddressDetailActivity : BaseActivity() {
    lateinit var binding: ActivityCardDeliveryAddressDetailBinding
    private lateinit var viewModel: CardDeliveryAddressViewModel
    var dob: String = ""
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_card_delivery_address_detail)
        activity = this
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        var cardDeliveryAddressString = SessionManagerUI.getInstance(this).cardDeliveryAddress
        if (!cardDeliveryAddressString.isNullOrEmpty()) {
            var cardDeliveryAddress = JsonUtil.toObject(
                cardDeliveryAddressString,
                CardDeliveryAddressModel::class.java
            ) as CardDeliveryAddressModel
            viewModel.strAddressLine1.set(cardDeliveryAddress.addressLine1)
            viewModel.strAddressLine2.set(cardDeliveryAddress.addressLine2)
            viewModel.strCity.set(cardDeliveryAddress.city)
            viewModel.strPinCode.set(cardDeliveryAddress.pinCode)
            viewModel.strState.set(cardDeliveryAddress.state)
        }
        viewModel.baseCallBack?.showKeyboard(binding.etPinCode)
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        onTextFilled(binding.etAddressLine1)
        onTextFilled(binding.etAddressLine2)
        onTextFilled(binding.etPinCode)
        onTextFilled(binding.etCity)
        onTextFilled(binding.etState)
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
                    !viewModel.strCity.get().isNullOrEmpty() &&
                    !viewModel.strState.get().isNullOrEmpty() &&
                    !viewModel.strPinCode.get().isNullOrEmpty() &&
                    (viewModel.strPinCode.get()!!.length == 6)
                ) {
                    binding.btSave.isEnabled = true
                } else
                    binding.btSave.isEnabled = false
            }
        })
    }

    fun openPreviousScreen(view: View) {
        finish()
        viewModel.baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_BACK_CARD_DELIVERY_ADDRESS,
            BaaSConstantsUI.CL_USER_BACK_CARD_DELIVERY_ADDRESS_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date()
        )
    }

    fun save(view: View) {
        hideKeyboard(this)
        var cardDeliveryAddress = CardDeliveryAddressModel()
        cardDeliveryAddress.pinCode = binding.etPinCode.text.toString().trim()
        cardDeliveryAddress.addressLine1 = binding.etAddressLine1.text.toString().trim()
        cardDeliveryAddress.addressLine2 = binding.etAddressLine2.text.toString().trim()
//        cardDeliveryAddress.landmark = binding.etLandmark.text.toString().trim()
        cardDeliveryAddress.city = binding.etCity.text.toString().trim()
        cardDeliveryAddress.state = binding.etState.text.toString().trim()
        SessionManagerUI.getInstance(this).cardDeliveryAddress =
            JsonUtil.toString(cardDeliveryAddress)
        SessionManagerUI.getInstance(this).userStatusCode =
            UserState.CARD_DELIVERY_ADDRESS_SAVED_LOCAL.getValue()
        finish()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            CardDeliveryAddressViewModel.CardDeliveryViewModelFactory(this, this)
        )[CardDeliveryAddressViewModel::class.java]

        binding.viewModel = viewModel
    }


}