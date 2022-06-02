package com.payu.baas.coreUI.view.ui.activity.cardUpdatePin

import android.app.Activity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityUpadatePinBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.CardSetPinResponse
import com.payu.baas.core.model.responseModels.UpdateCardPinSetStatusResponse
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.view.ui.BaseActivity

class UpadatePinActivity : BaseActivity() {
    lateinit var binding: ActivityUpadatePinBinding
    private lateinit var viewModel: UpdatePinViewModel
    var pinStatus = false

    //    lateinit var font: Typeface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upadate_pin)
        activity = this
//        font = Typeface.createFromAsset(assets, "fonts/Poppins.ttf")
        setupViewModel()
        setupObserver()
    }

    fun setupUi(url: String) {
        val webSettings: WebSettings = binding.wvUpdatePin.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        binding.wvUpdatePin.setWebViewClient(MyAppWebViewClient(this))
//        binding.wvUpdatePin.webChromeClient = MyWebChromeClient(this)

//        binding.wvUpdatePin.setWebViewClient(object : WebViewClient() {
//            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
//                super.doUpdateVisitedHistory(view, url, isReload)
//                val currentUrl = view?.url
//                if (currentUrl!!.contains(BaaSConstantsUI.CARD_SET_PIN_REDIRECT_URL)) {
//                    if (currentUrl!!.contains("success=true"))
//                        pinStatus = true
//                    else
//                        pinStatus = false
//                    viewModel.updatePinStatus(pinStatus)
//                }
//            }
//
//            override fun onReceivedError(
//                view: WebView,
//                errorCode: Int,
//                description: String,
//                failingUrl: String
//            ) {
//                showToastMessage(description)
//            }
//        })
        binding.wvUpdatePin.loadUrl(url)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            UpdatePinViewModel.UpdatePinViewModelFactory(this, this)
        )[UpdatePinViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun setupObserver() {
        viewModel.getUpadtePinResponseObs().observe(this, {
            parseResponse(it, ApiName.CARD_SET_PIN)
        })
        viewModel.getUpadtePinStatusResponseObs().observe(this, {
            parseResponse(it, ApiName.UPDATE_PIN)
        })
        viewModel.getAccessTokenResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_CLIENT_TOKEN)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.CARD_SET_PIN -> {
                        it.data?.let {
                            if (it is CardSetPinResponse) {
                                it.redirectUrl?.let { it1 -> setupUi(it1) }
                            }
                        }
                    }
                    ApiName.UPDATE_PIN -> {
                        it.data?.let {
                            if (it is UpdateCardPinSetStatusResponse) {
//                                it.userMessage?.let { it1 ->
//                                    SimpleCustomSnackbar.showSwitchMsg.make(
//                                        binding.rlParent, it1,
//                                        Snackbar.LENGTH_LONG, pinStatus
//                                    )?.show()
//                                }
//
//                                setResult(Activity.RESULT_OK)
//                                finish()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                                return
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
                        var errorUiRes = com.payu.baas.coreUI.util.JsonUtil.toObject(
                            it.message,
                            com.payu.baas.coreUI.model.ErrorResponseUI::class.java
                        ) as com.payu.baas.coreUI.model.ErrorResponseUI
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
//                if (!(it.message.isNullOrEmpty()))
//                    if (it.message.contains("error") || it.message.contains("msg")) {
//                        viewModel.baseCallBack?.showToastMessage(it.message)
//                    } else if (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN) || it.message!!.contains(
//                            BaaSConstantsUI.DEVICE_BINDING_FAILED
//                        )
//                    ) {
//                        viewModel.reGenerateAccessToken(false)
//                    }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.CARD_SET_PIN -> {
                        viewModel.updatePin()
                    }
                }
            }
        }
    }

    //    // WebViewClient
    class MyAppWebViewClient(private val activity: UpadatePinActivity) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if (url!!.contains(BaaSConstantsUI.CARD_SET_PIN_REDIRECT_URL)) {
                var status = false
                if (url.contains("success=true"))
                    status = true
                else
                    status = false
                activity.viewModel.updatePinStatus(status)
                return true
            }
            return false
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        viewModel.updatePinStatus(false)
    }
}