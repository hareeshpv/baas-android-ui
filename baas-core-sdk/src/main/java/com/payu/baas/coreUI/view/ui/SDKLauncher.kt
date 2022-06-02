package com.payu.baas.coreUI.view.ui

import android.app.Activity
import android.content.Intent
import com.payu.baas.coreUI.view.ui.activity.splash.SplashScreenActivity

class SDKLauncher {

    fun launch(activity: Activity) {
        goToNextActivity(activity)
    }

    @JvmSynthetic
    internal fun goToNextActivity(activity: Activity){
        val intent = Intent(activity, SplashScreenActivity::class.java)
          activity.startActivity(intent)
    }
}