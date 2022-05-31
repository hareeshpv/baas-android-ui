package com.payu.baas.core.view.ui.activity.notVerifiedEmp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityNotVerifiedEmpBinding
import com.payu.baas.core.view.ui.BaseActivity

class NotVerifiedEmpActivity : BaseActivity() {
    lateinit var binding: ActivityNotVerifiedEmpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_not_verified_emp)
        activity = this
    }

    fun openPreviousScreen(view: View) {
        finish()
    }
}