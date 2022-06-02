package com.payu.baas.coreUI.view.ui.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityErrorBinding
import com.payu.baas.coreUI.util.BaaSConstantsUI
import com.payu.baas.coreUI.util.enums.ErrorType
import com.payu.baas.coreUI.view.ui.BaseActivity

class ErrorActivity : BaseActivity() {
    lateinit var binding: ActivityErrorBinding
    var errorType: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_error)
        if (intent.extras != null) {
            errorType = intent.getIntExtra(BaaSConstantsUI.BS_KEY_ERROR_TYPE, 0)
        }
        showErrorOptions()
    }

    private fun showErrorOptions() {
        var errorTitle = ""
        var errorSolution = ""
        when (errorType) {
            ErrorType.TECHNICAL.ordinal -> {
                binding.btExit.visibility = View.VISIBLE
                binding.btReloadPage.visibility = View.VISIBLE
                binding.btOpenWifiSettings.visibility = View.GONE
                errorTitle = resources.getString(R.string.technical_error_title_text)
                errorSolution = resources.getString(R.string.reload_page_message)
            }
            ErrorType.NO_INTERNET.ordinal -> {
                binding.btExit.visibility = View.GONE
                binding.btReloadPage.visibility = View.VISIBLE
                binding.btOpenWifiSettings.visibility = View.VISIBLE
                errorTitle = resources.getString(R.string.no_internet_title_text)
                errorSolution = resources.getString(R.string.no_internet_solution_text)
                binding.ivError.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.no_internet)
                )
            }
            ErrorType.SECURITY.ordinal -> {
                binding.btExit.visibility = View.VISIBLE
                binding.btReloadPage.visibility = View.GONE
                binding.btOpenWifiSettings.visibility = View.GONE
                binding.btExit.background =
                    ContextCompat.getDrawable(this, R.drawable.button_bg)
                binding.btExit.setTextColor(
                    ContextCompat.getColor(this, R.color.black)
                )
                errorTitle = resources.getString(R.string.security_error_title_text)
                errorSolution = resources.getString(R.string.security_error_solution_text)
            }

        }
        binding.tvErrorTitle.text = errorTitle
        binding.tvErrorSolution.text = errorSolution
    }

    fun performAction(view: View) {
        when (view) {
            binding.btExit -> {

            }
            binding.btOpenWifiSettings -> {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            binding.btReloadPage -> {
                //reload current page content here
            }
        }
        finish()
    }
}