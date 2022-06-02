package com.payu.baas.coreUI.view.ui.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityWelcomeScreenBinding
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.set_passcode.SetPasscodeActivity
import java.util.*

class WelcomeScreenActivity : BaseActivity() {
    lateinit var binding: ActivityWelcomeScreenBinding
    lateinit var welcomeBaseCallback: BaseCallback
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen)
        setupUI()
    }

    private fun setupUI() {
        welcomeBaseCallback = this
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        welcomeBaseCallback.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_ACCOUNT_OPENED,
            BaaSConstantsUI.CL_USER_ACCOUNT_OPENED_EVENT_ID,
            SessionManager.getInstance(this).accessToken, imei, mobileNumber, Date()
        )
    }


    fun next(view: View) {
        SessionManagerUI.getInstance(this).userStatusCode =
            UserState.WELCOM_SCREEN_REACHED.getValue()
        callNextScreen(Intent(this, SetPasscodeActivity::class.java), null,true)
    }

    fun cross_icon(view: View) {
        finish()
        welcomeBaseCallback.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_CLOSE_BADHAI_HO,
            BaaSConstantsUI.CL_USER_CLOSE_BADHAI_HO_EVENT_ID,  SessionManager.getInstance(this).accessToken, imei, mobileNumber, Date()
        )
    }

}