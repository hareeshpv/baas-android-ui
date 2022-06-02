package com.payu.baasSampleApp

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.SDKLauncher
import com.payu.baasSampleApp.databinding.ActivitySplashBinding

open class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        activity = this
        setupNextScreen()
    }
    private fun setupNextScreen() {
        //From here we launch our UI SDK
        SDKLauncher().launch(this)
        finish()
    }


}