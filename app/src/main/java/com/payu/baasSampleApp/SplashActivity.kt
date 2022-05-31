package com.payu.baasSampleApp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.SDKLauncher
import com.payu.baas.core.view.ui.activity.intro.IntroActivity
import com.payu.baas.core.view.ui.activity.otpverification.OTPVerificationActivity
import com.payu.baas.core.view.ui.activity.splash.SplashScreenActivity
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