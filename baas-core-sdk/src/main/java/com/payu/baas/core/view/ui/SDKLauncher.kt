package com.payu.baas.core.view.ui

import android.app.Activity
import android.content.Intent
import com.payu.baas.core.view.ui.activity.advance.AdvanceActivity
import com.payu.baas.core.view.ui.activity.cards.CardsActivity
import com.payu.baas.core.view.ui.activity.enterpasscode.PasscodeActivity
import com.payu.baas.core.view.ui.activity.kyc.CompleteKYCActivity
import com.payu.baas.core.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.core.view.ui.activity.onboarding.PermissionActivity
import com.payu.baas.core.view.ui.activity.otpverification.OTPVerificationActivity
import com.payu.baas.core.view.ui.activity.panvalidate.VerifyPANCardNumberActivity
import com.payu.baas.core.view.ui.activity.profile.RatesChargesActivity
import com.payu.baas.core.view.ui.activity.profile.UserProfileActivity
import com.payu.baas.core.view.ui.activity.reset_passcode.ResetPasscodeActivity
import com.payu.baas.core.view.ui.activity.reviewAndSubmit.ReviewSubmitDetailActivity
import com.payu.baas.core.view.ui.activity.set_passcode.SetPasscodeActivity
import com.payu.baas.core.view.ui.activity.splash.SplashScreenActivity

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