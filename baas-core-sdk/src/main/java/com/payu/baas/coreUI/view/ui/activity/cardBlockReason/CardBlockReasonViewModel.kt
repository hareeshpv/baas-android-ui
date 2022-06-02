package com.payu.baas.coreUI.view.ui.activity.cardBlockReason

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.BlockCardResponse
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.launch

class CardBlockReasonViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val blockResponseObs = MutableLiveData<Resource<Any>>()

    val isLostSelected: ObservableField<Boolean> = ObservableField(false)
    val isChoriSelected: ObservableField<Boolean> = ObservableField(false)
    val isDamageSelected: ObservableField<Boolean> = ObservableField(false)
    val isOtherSelected: ObservableField<Boolean> = ObservableField(false)
    val visOtherEdittext: ObservableField<Boolean> = ObservableField(false)
    val strOtherReason: ObservableField<String> = ObservableField("")
    val ivLost: ObservableField<Drawable> = ObservableField()
    val ivDamage: ObservableField<Drawable> = ObservableField()
    val ivChori: ObservableField<Drawable> = ObservableField()
    val ivOther: ObservableField<Drawable> = ObservableField()

    init {
        visOtherEdittext.set(false)
    }

    fun getMyViewVisibility(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }

    fun setLostSelected() {
        if (isLostSelected.get() == true) {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
        } else {
            isLostSelected.set(true)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
            ivLost.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_svg_tick_green
                )
            )
            ivChori.set(null)
            ivDamage.set(null)
            ivOther.set(null)
        }
        visOtherEdittext.set(false)
    }

    fun setDamsgeSelected() {
        if (isDamageSelected.get() == true) {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
        } else {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(true)
            isOtherSelected.set(false)
            ivDamage.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_svg_tick_green
                )
            )
            ivChori.set(null)
            ivLost.set(null)
            ivOther.set(null)
        }
        visOtherEdittext.set(false)
    }

    fun setChoriSelected() {
        if (isDamageSelected.get() == true) {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
        } else {
            isLostSelected.set(false)
            isChoriSelected.set(true)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
            ivChori.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_svg_tick_green
                )
            )
            ivDamage.set(null)
            ivLost.set(null)
            ivOther.set(null)
        }
        visOtherEdittext.set(false)
    }

    fun setOtherSelected() {
        if (isDamageSelected.get() == true) {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(false)
            visOtherEdittext.set(false)
        } else {
            isLostSelected.set(false)
            isChoriSelected.set(false)
            isDamageSelected.set(false)
            isOtherSelected.set(true)
            visOtherEdittext.set(true)
            ivOther.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_svg_tick_green
                )
            )
            ivDamage.set(null)
            ivLost.set(null)
            ivChori.set(null)
        }
    }

    fun getBlockCardReason(): String {
        var reason = ""
        if (isLostSelected.get()!!.equals(true))
            reason = "lost"
        else if (isDamageSelected.get()!!.equals(true))
            reason = "damage"
        else if (isChoriSelected.get()!!.equals(true))
            reason = "chori"
        else
            reason = "other : " + strOtherReason.get().toString()
        return reason
    }

    fun blockCard() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                blockResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        reason = getBlockCardReason()
                    }

                    ApiCall.callAPI(ApiName.BLOCK_CARD, apiParams, object : ApiHelperUI {
                        override fun onSuccess(apiResponse: ApiResponse) {
                            if (apiResponse is BlockCardResponse) {
                                blockResponseObs.postValue(Resource.success(apiResponse))
                            }
                        }

                        override fun onError(errorResponse: ErrorResponse) {
                            blockResponseObs.postValue(
                                Resource.error(
                                    errorResponse.errorMessage!!,
                                    errorResponse
                                )
                            )
                        }
                    })
                } catch (e: Exception) {
                    blockResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun getBlockResponseObs(): LiveData<Resource<Any>> {
        return blockResponseObs
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class CardBlockViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CardBlockReasonViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CardBlockReasonViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}