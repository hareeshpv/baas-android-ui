package com.payu.baas.coreUI.view.ui.activity.profile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityRatesChargesBinding
import com.payu.baas.coreUI.view.ui.BaseActivity

class RatesChargesActivity : BaseActivity() {
    lateinit var binding: ActivityRatesChargesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rates_charges)
        activity = this
        setupUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.joiningFee.text = getString(R.string.rs_prefix)+"\t "+getString(R.string.zero)
        binding.annualRenewelFee.text = getString(R.string.rs_prefix)+"\t "+getString(R.string.zero)
        binding.freeATMWithdrawalMonthly.text = getString(R.string.four)
        binding.atmWithdrawalFeeHintColor.text = getString(R.string.rs_prefix)+"\t"+getString(R.string.twenty_five)+"\t"+"+"+"\t"+"18%"+getString(R.string.gst)
        binding.atmWithdrawalFee.text = getString(R.string.rs_prefix)+"\t "+"29.5"
        binding.freeBankTransfer.text = getString(R.string.six)
        binding.bankTransferFeePerTransactionHintColor.text = getString(R.string.rs_prefix)+"\t"+getString(R.string.five)+"\t"+"+"+""+"18%"+"\t"+getString(R.string.gst)
        binding.bankTransferFeePerTransaction.text= getString(R.string.rs_prefix)+"\t "+"5.9"
        binding.advanceUsagefee.text = "2.36%"
        binding.advanceUsagefeeHintColor.text = getString(R.string.two_percent)+"+"+ getString(R.string.gst)
        binding.latePaymentFees.text= getString(R.string.rs_prefix)+"\t"+getString(R.string.fifty)+"\t"+"+"+"\t "+"18%"+getString(R.string.gst)
        binding.latePaymentFeesInterestCharges.text= getString(R.string.three_percent)+"+"+"\t "+"18%"+getString(R.string.gst)
        binding.maximumFuelSurchargeWaiver.text=getString(R.string.rs_prefix)+"\t "+"62.5"
        binding.latePaymentFeesInterestCharges.text = "28.32%"
        binding.latePaymentFeesInterestChargesHintColor.text = getString(R.string.twenty_four)+"+"+"\t"+"18%"+getString(R.string.gst)+"\n"+"on due amount"
        binding.latePaymentFees.text=getString(R.string.rs_prefix)+"\t "+"59"
        binding.latePaymentFeesHintColor.text=getString(R.string.rs_prefix)+"\t"+getString(R.string.fifty)+"\t"+"+"+""+"18%"+"\t"+getString(R.string.gst)
        binding.latePaymentFeesFlat.text="3.54% Flat"
        binding.latePaymentFeesOnDueAmount.text=  getString(R.string.three_percent)+ "+"+"\t"+getString(R.string.gst)+"\n"+"on due amount"

    }

    fun ratesCharges(view: View) {
        finish()
    }
}