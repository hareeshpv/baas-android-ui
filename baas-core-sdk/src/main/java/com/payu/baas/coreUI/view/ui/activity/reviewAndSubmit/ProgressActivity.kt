package com.payu.baas.coreUI.view.ui.activity.reviewAndSubmit

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ActivityInProgressBinding
import com.payu.baas.coreUI.util.BaaSConstantsUI.CL_USER_NOT_VERIFIED
import com.payu.baas.coreUI.view.ui.BaseActivity


class ProgressActivity : BaseActivity() {
    lateinit var binding: ActivityInProgressBinding
//    private lateinit var viewModel: ReviewAndSubmitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_in_progress)
        activity = this
        CL_USER_NOT_VERIFIED = "1"
        setUpUI()
    }

    private fun setUpUI() {
    }

    fun openPreviousScreen(view: View) {
        finish()
    }

//    private fun setupObserver() {
//        viewModel.getKycSelfieResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_SELFIE)
//        })
//        viewModel.getKycAadhaarResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_AADHAR)
//        })
//        viewModel.getKycResultsResponseObs().observe(this, {
//            parseResponse(it, ApiName.KYC_RESULTS)
//        })
//        viewModel.getVerifyKycResponseObs().observe(this, {
//            parseResponse(it, ApiName.VERIFY_KYC_RESULTS)
//        })
//    }
//
//    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
//        when (it?.status) {
//            Status.SUCCESS -> {
//                hideProgress()
//                when (apiName) {
//                    ApiName.KYC_SELFIE -> {
//                        it.data?.let {
//                            if (it is KYCSelfieResponse) {
//                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//                    ApiName.VERIFY_KYC_RESULTS -> {
//                        it.data?.let {
//                            if (it is VerifyKYCResultsResponse) {
//                                setUpUI()
////                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
////                                var intent = Intent(this, WelcomeScreenActivity::class.java)
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
////                                callNextScreen(intent, null)
//                            }
//                        }
//                    }
//                }
//
//            }
//            Status.LOADING -> {
//                showProgress("")
//            }
//            Status.ERROR -> {
//                //Handle Error
//                hideProgress()
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//            }
//            Status.RETRY -> {
//                hideProgress()
//                when (apiName) {
//                    ApiName.KYC_SELFIE -> {
//                        viewModel.saveUserSelfie()
//                    }
//                    ApiName.KYC_AADHAR -> {
//                    viewModel.saveKYCAadhar()
//                }
//                    ApiName.KYC_RESULTS -> {
//                        viewModel.addKycResults()
//                    }
//                    ApiName.VERIFY_KYC_RESULTS -> {
//                        viewModel.verifyKycResults()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun setupViewModel() {
//        viewModel = ViewModelProvider(
//            this
//        ).get(ReviewAndSubmitViewModel::class.java)
//        binding.viewModel = viewModel
//    }

}