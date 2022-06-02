package com.payu.baas.coreUI.view.ui.activity.reviewAndSubmit

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.payu.baas.coreUI.R
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.coreUI.model.entities.model.PanDetailsModelUI
import com.payu.baas.core.model.model.CardDeliveryAddressModel
import com.payu.baas.core.model.params.ApiParams
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.util.*
import com.payu.baas.coreUI.util.enums.UserState
import com.payu.baas.coreUI.view.callback.BaseCallback
import com.payu.baas.coreUI.view.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION")
class ReviewAndSubmitViewModel(
    baseCallBack: BaseCallback?,
    context: Context
) : BaseViewModel(baseCallBack, null, context) {

    private val kycAadhaarResponseObs = MutableLiveData<Resource<Any>>()
    private val kycLocationResponseObs = MutableLiveData<Resource<Any>>()
    private val kycSelfieResponseObs = MutableLiveData<Resource<Any>>()
    private val addKycResultsResponseObs = MutableLiveData<Resource<Any>>()
    private val verifyKycResponseObs = MutableLiveData<Resource<Any>>()
    private val onBoardResponseObs = MutableLiveData<Resource<Any>>()
    val strAddress: ObservableField<String> = ObservableField("")
    val strName: ObservableField<String> = ObservableField("")
    var strState: String? = null
    var mobileNo_: String? = null
    var imeiNumber: String? = null
    val ivTick1: ObservableField<Drawable> = ObservableField()
    val ivTick2: ObservableField<Drawable> = ObservableField()

    //    val ivTick3: ObservableField<Drawable> = ObservableField()
//    val ivTick4: ObservableField<Drawable> = ObservableField()
    val enableThikHai: ObservableField<Boolean> = ObservableField()
    private var ipv4Address: String = ""
    lateinit var gpsTracker: GPSTracker

    init {
        check1Clicked()
        check2Clicked()
//        gpsTracker = GPSTracker(context);
        CoroutineScope(Dispatchers.IO).launch {
            val ipv4: String? = baseCallBack?.getPublicIP()
            withContext(Dispatchers.Main) {
                ipv4?.let {
                    ipv4Address = it
                }
            }
        }
    }

    fun getAddress(): String {
        var cardDeliveryAddressString = SessionManagerUI.getInstance(context).cardDeliveryAddress
        if (!cardDeliveryAddressString.isNullOrEmpty()) {
            var cardDeliveryAddress = com.payu.baas.coreUI.util.JsonUtil.toObject(
                cardDeliveryAddressString,
                CardDeliveryAddressModel::class.java
            ) as CardDeliveryAddressModel
            strState = cardDeliveryAddress.state
            strAddress.set(
                cardDeliveryAddress.addressLine1 + ", " + cardDeliveryAddress.addressLine2
                        + ", " + cardDeliveryAddress.city
                        + ", " + cardDeliveryAddress.state + ", " + cardDeliveryAddress.pinCode
            )
        }
        return strAddress.get().toString()
    }

    fun getKycAadhaarResponseObs(): LiveData<Resource<Any>> {
        return kycAadhaarResponseObs
    }

    fun getKycSelfieResponseObs(): LiveData<Resource<Any>> {
        return kycSelfieResponseObs
    }

    fun getKycResultsResponseObs(): LiveData<Resource<Any>> {
        return addKycResultsResponseObs
    }

    fun getVerifyKycResponseObs(): LiveData<Resource<Any>> {
        return verifyKycResponseObs
    }

    fun getKycLocationResponseObs(): LiveData<Resource<Any>> {
        return kycLocationResponseObs
    }

    fun getOnBoardResponseObs(): LiveData<Resource<Any>> {
        return onBoardResponseObs
    }

    fun saveKYCAadhar() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                kycAadhaarResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        xmlFileCode =
                            SessionManagerUI.getInstance(context).karzaAadhaarFileCode
                        xmlFileString =
                            SessionManagerUI.getInstance(context).karzaAadhaarFileContent
                        xmlFileString = xmlFileString?.replace("data:application/zip;base64,", "")
                    }
                    ApiCall.callAPI(
                        ApiName.KYC_AADHAR,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                if (apiResponse is KYCAadharResponse) {
                                    kycAadhaarResponseObs.postValue(Resource.success(apiResponse))
                                    SessionManagerUI.getInstance(context).userStatusCode =
                                        UserState.AADHARXML_SAVED.getValue()
                                    addKycResults()
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
//                                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                ) {
//                                    hideCustomProgress()
//                                    baseCallBack.showTechinicalError()
//                                } else
                                kycAadhaarResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    kycAadhaarResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun saveKYCLocation() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                kycLocationResponseObs.postValue(Resource.loading(null))
                try {
                    // check if GPS enabled
                    gpsTracker = GPSTracker(context)
                    if (gpsTracker.getIsGPSTrackingEnabled()) {
                        val apiParams = ApiParams().apply {
//                            ipAddress = baseCallBack.getIPAddress(true)
                            ipAddress = ipv4Address
                            latitude = gpsTracker.getLatitude().toString()
                            longitude = gpsTracker.getLongitude().toString()
//                            ipAddress = ipAdd
                            state = strState
                            country = "India"
                        }
                        if (gpsTracker.location == null) {
                            kycLocationResponseObs.postValue(
                                Resource.error(
                                    BaaSConstantsUI.FETCHING_LOCATION_MESSAGE,
                                    null
                                )
                            )
                            gpsTracker = GPSTracker(context)
                        } else
                            ApiCall.callAPI(

                                ApiName.KYC_LOCATION,
                                apiParams,
                                object : ApiHelperUI {
                                    override fun onSuccess(apiResponse: ApiResponse) {
                                        if (apiResponse is KYCLocationResponse) {
                                            kycLocationResponseObs.postValue(
                                                Resource.success(
                                                    apiResponse
                                                )
                                            )
                                            SessionManagerUI.getInstance(context).userStatusCode =
                                                UserState.LAT_LONG_IP_SAVED.getValue()
                                            saveUserSelfie()

                                        }
                                    }

                                    override fun onError(errorResponse: ErrorResponse) {
//                                    if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                        && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                    ) {
//                                        hideCustomProgress()
//                                        baseCallBack.showTechinicalError()
//                                    } else
                                        kycAadhaarResponseObs.postValue(
                                            Resource.error(
                                                errorResponse.errorMessage!!,
                                                errorResponse
                                            )
                                        )
                                    }
                                })
                    } else {

                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gpsTracker.showSettingsAlert();
                        kycLocationResponseObs.postValue(
                            Resource.error(
                                "GPS is off. You need to enable it.",
                                null
                            )
                        )
                    }
                } catch (e: Exception) {
                    kycLocationResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun saveUserSelfie() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                kycSelfieResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams().apply {
                        live_photo =
                            SessionManagerUI.getInstance(context).karzaUserSelfie
                        karza_photo_name = "kyc_user_selfie.jpg"
                    }
                    ApiCall.callAPI(
                        ApiName.KYC_SELFIE,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                if (apiResponse is KYCSelfieResponse) {
                                    SessionManagerUI.getInstance(context).userStatusCode =
                                        UserState.SELFIE_SAVED.getValue()
                                    kycSelfieResponseObs.postValue(Resource.success(apiResponse))
                                    saveKYCAadhar()
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
//                                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                ) {
//                                    hideCustomProgress()
//                                    baseCallBack.showTechinicalError()
//                                } else
                                kycSelfieResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    kycSelfieResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun addKycResults() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                addKycResultsResponseObs.postValue(Resource.loading(null))
                try {
                    val karzaVerificationString =
                        SessionManagerUI.getInstance(context).karzaVerificationResponse
                    var panDetailsString = SessionManagerUI.getInstance(context).userPanDetails
                    if (!karzaVerificationString.isNullOrEmpty() && !panDetailsString.isNullOrEmpty()) {
                        val karzaVerificationResponse = com.payu.baas.coreUI.util.JsonUtil.toObject(
                            karzaVerificationString,
                            KarzaVerificationResponse::class.java
                        ) as KarzaVerificationResponse
                        val apiParams = ApiParams().apply {
                            var panDetails = com.payu.baas.coreUI.util.JsonUtil.toObject(
                                panDetailsString,
                                com.payu.baas.coreUI.model.entities.model.PanDetailsModelUI::class.java
                            ) as com.payu.baas.coreUI.model.entities.model.PanDetailsModelUI
                            panNumber = panDetails.panNumber
                            maskedAadhaar = karzaVerificationResponse.maskedAadhaar
                            requestId = karzaVerificationResponse.requestId
                            applicationId = karzaVerificationResponse.applicationId
                            if (applicationId.isNullOrEmpty())
                                applicationId =
                                    com.payu.baas.core.storage.SessionManager.getInstance(context).applicationId
                            dob = karzaVerificationResponse.dobAadharXML!!.dob
                            if (!dob.isNullOrEmpty() && dob!!.contains("/")) {
                                dob = UtilsUI.updateDateFormat(dob!!)
//                            dob = dob!!.replace("/", "-")
//                            var month = dob!!.split("-").get(1)
//                            var day = dob!!.split("-").get(0)
//                            var year = dob!!.split("-").get(2)
//                            dob = year + "-" + month + "-" + day
                            }
                            gender = karzaVerificationResponse.genderAadharXML!!.gender
                            createTimeStamp = karzaVerificationResponse.createdTimeStamp
                            panVerified = karzaVerificationResponse.panVerified
                            livenessVerified = karzaVerificationResponse.livenessVerified
                            xmlDateVerified = karzaVerificationResponse.xmlDateVerified
                            isAadhaarVerified = karzaVerificationResponse.isAadhaarVerified
                            var faceObject = JSONObject()
                            faceObject.put(
                                "match",
                                karzaVerificationResponse.faceAadhaarXML!!.matchScore
                            )
                            faceObject.put(
                                "matchMeta",
                                karzaVerificationResponse.faceAadhaarXML!!.matchMeta
                            )
                            faceAadhaarXml = faceObject
                            currentAddress = karzaVerificationResponse.currentAddress!!.address
                            permanentAddress = karzaVerificationResponse.permanentAddress!!.address
                            var userNameObject = JSONObject()
                            userNameObject.put(
                                "match",
                                karzaVerificationResponse.userNameDate!!.matchScore
                            )
                            userNameObject.put(
                                "matchMeta",
                                karzaVerificationResponse.userNameDate!!.matchMeta
                            )

                            userNameObject.put(
                                "formData",
                                karzaVerificationResponse.userNameDate!!.formData
                            )
                            userNameObject.put(
                                "panOCRData",
                                karzaVerificationResponse.userNameDate!!.panOCRData
                            )
                            userNameObject.put(
                                "aadhaarXMLData",
                                karzaVerificationResponse.userNameDate!!.aadhaarXmlData
                            )
                            userNameData = userNameObject
                        }

                        ApiCall.callAPI(
                            ApiName.KYC_RESULTS,
                            apiParams,
                            object : ApiHelperUI {
                                override fun onSuccess(apiResponse: ApiResponse) {
                                    if (apiResponse is AddKYCResultsResponse) {
                                        addKycResultsResponseObs.postValue(
                                            Resource.success(
                                                apiResponse
                                            )
                                        )
                                        SessionManagerUI.getInstance(context).userStatusCode =
                                            UserState.KYC_RESULT_SAVED.getValue()
                                        verifyKycResults()
                                    }
                                }

                                override fun onError(errorResponse: ErrorResponse) {
//                                    if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                        && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                    ) {
//                                        hideCustomProgress()
//                                        baseCallBack.showTechinicalError()
//                                    } else
                                    addKycResultsResponseObs.postValue(
                                        Resource.error(
                                            errorResponse.errorMessage!!,
                                            errorResponse
                                        )
                                    )
                                }
                            })
                    }
                } catch (e: Exception) {
                    addKycResultsResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun verifyKycResults() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            viewModelScope.launch {
                verifyKycResponseObs.postValue(Resource.loading(null))
                try {
                    val apiParams = ApiParams()
                    ApiCall.callAPI(
                        ApiName.VERIFY_KYC_RESULTS,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                if (apiResponse is VerifyKYCResultsResponse) {
                                    verifyKycResponseObs.postValue(Resource.success(apiResponse))
                                    if (apiResponse.message.equals("YES")) {
                                        SessionManagerUI.getInstance(context).userStatusCode =
                                            UserState.KYC_CHECKS_PASSED.getValue()
                                        onBoardUserStatus()
                                    }
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
//                                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                ) {
//                                    hideCustomProgress()
//                                    baseCallBack.showTechinicalError()
//                                } else
                                verifyKycResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    verifyKycResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun onBoardUserStatus() {
        if (baseCallBack?.isInternetAvailable(true) == true) {
            showProgressActivityAsDialog()
            viewModelScope.launch {
                onBoardResponseObs.postValue(Resource.loading(null))
                try {
//                    var panDetailsString = SessionManagerUI.getInstance(context).userPanDetails
//                    var panDetails = JsonUtil.toObject(
//                        panDetailsString,
//                        PanDetailsModelUI::class.java
//                    ) as PanDetailsModelUI
//                    var cardDeliveryAddressString =
//                        SessionManagerUI.getInstance(context).cardDeliveryAddress
//                    var cardAddressModel = JsonUtil.toObject(
//                        cardDeliveryAddressString,
//                        CardDeliveryAddressModel::class.java
//                    ) as CardDeliveryAddressModel
//                    val karzaVerificationString =
//                        SessionManagerUI.getInstance(context).karzaVerificationResponse
//                    val karzaVerificationResponse = JsonUtil.toObject(
//                        karzaVerificationString,
//                        KarzaVerificationResponse::class.java
//                    ) as KarzaVerificationResponse
                    val apiParams = ApiParams().apply {
                        mobile = SessionManagerUI.getInstance(context).userMobileNumber

//                        pan = panDetails.panNumber
//                        employeeId = panDetails.employeeId
//                        firstName = panDetails.firstName
//                        lastName = panDetails.lastName
//                        gender = karzaVerificationResponse.genderAadharXML?.gender
//                        if (gender.equals("female", false)==true)
//                            title = "Ms"
//                        else
//                            title = "Mr"
//                        dob = panDetails.dob
//                        if (SessionManagerUI.getInstance(context).emailId.isNullOrEmpty())
//                            email = ""
//                        else
//                            email = SessionManagerUI.getInstance(context).emailId
//                        country = "India"
//                        countryCode = "+91"
//                        addressLine1 = cardAddressModel.addressLine1
//                        addressLine2 = cardAddressModel.addressLine2
//                        city = cardAddressModel.city
//                        pinCode = cardAddressModel.pinCode
//                        state = cardAddressModel.state
                    }

                    ApiCall.callAPI(
                        ApiName.GET_ONBOARD_STATUS,
                        apiParams,
                        object : ApiHelperUI {
                            override fun onSuccess(apiResponse: ApiResponse) {
                                if (apiResponse is GetOnBoardUserStatusResponse) {
                                    onBoardResponseObs.postValue(Resource.success(apiResponse))
                                    SessionManagerUI.getInstance(context).userStatusCode =
                                        apiResponse.message
                                }
                            }

                            override fun onError(errorResponse: ErrorResponse) {
//                                if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
//                                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
//                                ) {
//                                    hideCustomProgress()
//                                    baseCallBack.showTechinicalError()
//                                } else
                                onBoardResponseObs.postValue(
                                    Resource.error(
                                        errorResponse.errorMessage!!,
                                        errorResponse
                                    )
                                )
                            }
                        })
                } catch (e: Exception) {
                    onBoardResponseObs.postValue(Resource.error(e.toString(), null))
                }
            }
        }
    }

    fun reTry() {
        baseCallBack?.cleverTapUserOnBoardingEvent(
            BaaSConstantsUI.CL_USER_AGREED_TERMS_CONDITION,
            BaaSConstantsUI.CL_USER_AGREED_TERMS_CONDITION_EVENT_ID,
            SessionManager.getInstance(context).accessToken,
            imeiNumber,
            mobileNo_,
            Date()
        )
        var userState = SessionManagerUI.getInstance(context).userStatusCode
        when (userState) {
            UserState.KYC_SCREEN_PASSED.getValue(),
            UserState.AADHARXML_SAVED_LOCAL.getValue() -> {
                saveKYCLocation()
            }
            UserState.LAT_LONG_IP_SAVED.getValue() -> {
                saveUserSelfie()
            }
            UserState.SELFIE_SAVED.getValue() -> {
                saveKYCAadhar()
            }
            UserState.AADHARXML_SAVED.getValue() -> {
                addKycResults()
            }
            UserState.KYC_RESULT_SAVED.getValue() -> {
                verifyKycResults()
            }
            UserState.KYC_CHECKS_PASSED.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_1.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_2.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_3.getValue(),
            UserState.ONBOARDING_IN_PROGRESS_4.getValue(),
            UserState.ONBOARDING_IN_PROGRESS.getValue() -> {
                onBoardUserStatus()
            }
        }
    }

    var mDialog: Dialog? = null

    //    var progressCount = 0
    fun showProgressActivityAsDialog() {
//        progressCount = 0
        try {
            if (mDialog == null) {
                context?.let {
                    val inflator: LayoutInflater = context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val view: View = inflator.inflate(R.layout.activity_in_progress, null)
                    mDialog = Dialog(context!!, R.style.AppBaseTheme)
                    mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog!!.setContentView(view)
                    mDialog!!.setCanceledOnTouchOutside(false)
                    mDialog!!.setCancelable(false)
                    Objects.requireNonNull(mDialog!!.getWindow())
                        ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    mDialog!!.show()
                    var btExit = view.findViewById<Button>(R.id.btExit)
                    btExit.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
//                            hideCustomProgress()
                            if (context is Activity) {
                                context.finish()
                            }
                        }

                    })
//                    var pLimit = 0
//                    when (apiName) {
//                        ApiName.KYC_LOCATION -> {
//                            pLimit = 10
//                        }
//                        ApiName.KYC_SELFIE -> {
//                            pLimit = 25
//                        }
//                        ApiName.KYC_AADHAR -> {
//                            pLimit = 40
//                        }
//                        ApiName.KYC_RESULTS -> {
//                            pLimit = 55
//                        }
//                        ApiName.VERIFY_KYC_RESULTS -> {
//                            pLimit = 70
//                        }
//                        ApiName.SAVE_ADDRESS -> {
//                            pLimit = 85
//                        }
//                        else -> {
//                            pLimit = 0
//                        }
//                    }
//                    setProgressForApi(pLimit)
                }

            }
        } catch (e: Exception) {
        }
    }

//    fun setProgressForApi(progressLimit: Int) {
//        while (progressCount < progressLimit && mDialog != null) {
////        while  (mDialog != null) {
//            mDialog!!.pBar.progress = progressLimit
//            val progressDrawable: Drawable = mDialog!!.pBar.progressDrawable.mutate()
//            progressDrawable.colorFilter =
//                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
//                    Color.GREEN,
//                    BlendModeCompat.SRC_ATOP
//                )
//            mDialog!!.pBar.progressDrawable = progressDrawable
////            progressCount = progressLimit
//            try {
//                Thread.sleep(0)
//            } catch (e: InterruptedException) {
//            }
//            progressCount++
//        }
////        if (progressLimit >= 100) {
//        if (progressLimit >= 100) {
//            hideCustomProgress()
//            baseCallBack?.callNextScreen(
//                Intent(
//                    context,
//                    WelcomeScreenActivity::class.java
//                ), null, true
//            )
//        }
//    }

    fun isProgressShowing(): Boolean {
        try {
            if (mDialog != null
                && mDialog!!.isShowing
            ) return true
        } catch (e: Exception) {
        }
        return false
    }

    fun hideCustomProgress() {
        try {
            if (isProgressShowing()) {
                mDialog!!.dismiss()
                mDialog = null
//                progressCount = 0
            }
        } catch (e: java.lang.Exception) {
        }
    }

//    fun loadingLimitForProgress(apiName: ApiName) {
//        var pLimit = 0
//        when (apiName) {
//            ApiName.KYC_LOCATION -> {
//                pLimit = 10
//            }
//            ApiName.KYC_SELFIE -> {
//                pLimit = 25
//            }
//            ApiName.KYC_AADHAR -> {
//                pLimit = 40
//            }
//            ApiName.KYC_RESULTS -> {
//                pLimit = 55
//            }
//            ApiName.VERIFY_KYC_RESULTS -> {
//                pLimit = 70
//            }
//            ApiName.SAVE_ADDRESS -> {
//                pLimit = 85
//            }
//        }
//        setProgressForApi(pLimit)
//    }

    fun check1Clicked() {
        if (ivTick1.get() == null) {
            ivTick1.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_tick_black
                )
            )
        } else {
            ivTick1.set(null)
        }
        isAllSelected()
    }

    fun check2Clicked() {
        if (ivTick2.get() == null) {
            ivTick2.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_tick_black
                )
            )
        } else {
            ivTick2.set(null)
        }
        isAllSelected()
    }

//    fun check3Clicked() {
//        if (ivTick3.get() == null) {
//            ivTick3.set(
//                AppCompatResources.getDrawable(
//                    context,
//                    R.drawable.ic_tick_black
//                )
//            )
//        } else {
//            ivTick3.set(null)
//        }
//        isAllSelected()
//    }

//    fun check4Clicked() {
//        if (ivTick4.get() == null) {
//            ivTick4.set(
//                AppCompatResources.getDrawable(
//                    context,
//                    R.drawable.ic_tick_black
//                )
//            )
//        } else {
//            ivTick4.set(null)
//        }
//        isAllSelected()
//    }

    fun isAllSelected() {
        if (ivTick1.get() == null || ivTick2.get() == null)
            enableThikHai.set(false)
        else
            enableThikHai.set(true)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal class ReviewViewModelFactory(
        private val baseCallBack: BaseCallback?,
        private val context: Context
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ReviewAndSubmitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewAndSubmitViewModel(baseCallBack, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}