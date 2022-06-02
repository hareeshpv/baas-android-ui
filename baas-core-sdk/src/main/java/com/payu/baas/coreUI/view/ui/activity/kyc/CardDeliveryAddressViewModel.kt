package com.payu.baas.coreUI.view.ui.activity.kyc

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel

class CardDeliveryAddressViewModel(baseCallBack: BaseCallback?,
                                   context: Context
) : BaseViewModel(baseCallBack, null,context){
    val strPinCode: ObservableField<String> = ObservableField("")
    val strAddressLine1: ObservableField<String> = ObservableField("")
    val strAddressLine2: ObservableField<String> = ObservableField("")
    val strLandmark: ObservableField<String> = ObservableField("")
    val strCity: ObservableField<String> = ObservableField("")
    val strState: ObservableField<String> = ObservableField("")

    init {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class CardDeliveryViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CardDeliveryAddressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CardDeliveryAddressViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}