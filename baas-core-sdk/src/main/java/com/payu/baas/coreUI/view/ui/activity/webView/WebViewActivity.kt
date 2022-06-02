package com.payu.baas.coreUI.view.ui.activity.webView

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityWebViewBinding
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.util.enums.ProfileWebViewEnum
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.onboarding.ErrorActivity


class WebViewActivity : BaseActivity() {
    lateinit var binding: ActivityWebViewBinding
    var flag: String? = null
    lateinit var webURL: String
    lateinit var screenKey: String

    lateinit var baseCallBack: BaseCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        activity = this
        baseCallBack = this
        setupUI()
        var extra = intent.extras
        if (extra != null) {
            flag = extra.getString(BaaSConstantsUI.USER_PROFILE_FLAG)
            webURL = extra.getString(BaaSConstantsUI.USER_URL).toString()
            if (!baseCallBack!!.isInternetAvailable(false)) {
                val mBundle = Bundle()
                mBundle.putInt(BaaSConstantsUI.BS_KEY_ERROR_TYPE, ErrorType.NO_INTERNET.ordinal)
                callNextScreen(Intent(this, ErrorActivity::class.java), mBundle)
                finish()
            } else {
                showWebView(webURL)
            }
            when (flag) {
                ProfileWebViewEnum.HELP.getValue() -> {
                    binding.title.text = BaaSConstantsUI.HELP_TITLE
                }

                ProfileWebViewEnum.FAQS.getValue() -> {
                    binding.title.text = BaaSConstantsUI.FAQS_TITLE
                }
                ProfileWebViewEnum.NOTIFICATIONS.getValue() -> {
                    binding.title.text = BaaSConstantsUI.NOTIFICATIONS_TITLE
                }
                ProfileWebViewEnum.OFFERS.getValue() -> {
                    binding.title.text = BaaSConstantsUI.OFFERS_AND_DEALS_TITLE
                }
                else -> {
                    binding.titleLayout.visibility = View.GONE
                }

            }
        }
    }

    private fun setupUI() {
        binding.progressBar.indeterminateDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Color.GREEN,
                BlendModeCompat.SRC_ATOP
            )
    }

    private fun showWebView(webURL: String) {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.pluginState = WebSettings.PluginState.ON
//        binding.webView.settings.setPluginsEnabled(true)
        binding.webView.webViewClient = PageWebViewClient()
        if (webURL.lowercase().contains(".pdf")) {
            binding.webView.loadUrl(BaaSConstantsUI.PDF_LOADING_URL + webURL)
        } else {
            binding.webView.loadUrl(webURL)
        }


    }

    fun webview_backarrow(view: View) {
        finish()
    }

    inner class PageWebViewClient : WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let { view?.loadUrl(it) }
            return true
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (view.title.equals("")) {
                view.reload()
                return
            }

            binding.progressBar.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    //
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}