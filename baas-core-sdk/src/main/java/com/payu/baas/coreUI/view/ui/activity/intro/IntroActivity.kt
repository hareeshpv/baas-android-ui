package com.payu.baas.coreUI.view.ui.activity.intro

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityIntroBinding
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.view.ui.BaseActivity
import com.payu.baas.coreUI.view.ui.activity.mobileverification.MobileVerificationActivity
import com.payu.baas.coreUI.view.ui.activity.onboarding.PermissionActivity


class IntroActivity : BaseActivity() {

    lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        activity = this
        setupUI()
    }

    private fun setupUI() {

        binding.nestedscrollview?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < binding.payuTextLayout.height) {
                //"TOP SCROLL"
                binding.toolbar.visibility=View.GONE
            }else{
                binding.toolbar.visibility=View.VISIBLE
            }
        })

//        val paint: TextPaint = binding.appName.paint
//        val width: Float = paint.measureText(binding.appName.text.toString())
//
//        val textShader: Shader = LinearGradient(
//            0f, 0f, width, binding.appName.textSize, intArrayOf(
//                SessionManagerUI.getInstance(binding.appName.context).primaryColor,
//                SessionManagerUI.getInstance(binding.appName.context).primaryDarkColor
//            ), null, Shader.TileMode.CLAMP
//        )
//        binding.appName.paint.shader = textShader

        val word: Spannable = SpannableString(getString(R.string.no_joining_fees_amp_annual_fees))
        word.setSpan(
            ForegroundColorSpan(Color.TRANSPARENT),
            5,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        word.setSpan(
            StrikethroughSpan(),
            0,
            4,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.noJoiningFeeTemp.text = word
    }

    private fun setupObserver() {
    }

    private fun setupViewModel() {
//        viewModel = ViewModelProvider(
//            this
//        ).get(IntroViewModel::class.java)
    }

    fun apply_karein(view: View) {
       if(SessionManagerUI.getInstance(applicationContext).permissionResult == "1") {
           callNextScreen(Intent(this, MobileVerificationActivity::class.java), null)
           finish()
       }else{
           callNextScreen(Intent(this, PermissionActivity::class.java), null)
           finish()
       }

    }
}
