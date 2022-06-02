package com.payu.baas.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Environment
import com.ericdecanini.mockwebtest.MockResponseFileReader
import com.payu.baas.core.model.model.*
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.util.JsonUtils
import com.payu.baas.core.interfaces.MockService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MockApiCallManger {
    public lateinit var mockWebServer: MockWebServer
    lateinit var retrofit: Retrofit
    lateinit var service: MockService
    fun initialize() {
        mockWebServer = MockWebServer()
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("").toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //Set a response for retrofit to handle. We can copy a sample
//        //response from your server to simulate a correct result or an error.
//        //MockResponse can also be customized with different parameters
//        //to match our test needs

        service = retrofit.create(MockService::class.java)
    }

    fun callForSendOtpApi(mockResponseJson: String): Call<SendOtpResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val sendOtp = SendOtp()
        sendOtp.mobileNumber = "8544941620"
        return service.getCurrentOtpData(sendOtp)
    }

    fun callForVerifyOtpApi(mockResponseJson: String): Call<VerifyOtpResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val verifyOtp: VerifyOtp = VerifyOtp()
        verifyOtp.code = "1111"
        verifyOtp.identity = "8544941620"
        verifyOtp.type = "SignIn"
        return service.getVerifyOtpData(verifyOtp)
    }

    fun callForVerifyOtpApiWrongOtp(mockResponseJson: String): Call<ApiResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200) //if set responseCode here as 404. it will throw NullPointer Error
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val verifyOtp: VerifyOtp = VerifyOtp()
        verifyOtp.code = "1110"
        verifyOtp.identity = "8544941620"
        verifyOtp.type = "SignIn"
        return service.getVerifyOtpErrorData(verifyOtp)
    }

    fun tearDownServer() {
        mockWebServer.shutdown()
    }

    fun callForSaveAddressApi(mockResponseJson: String): Call<SaveAddressResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val saveAddressRequest = JsonUtils.toObject(
            MockResponseFileReader
                ("json_save_address_request.json").content, SaveAddressRequestModel::class.java
        ) as SaveAddressRequestModel
        return service.getSaveAAdressData(saveAddressRequest)
    }

    fun callForSaveAddressApi_MissingFields(mockResponseJson: String): Call<SaveAddressResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val saveAddressRequest = JsonUtils.toObject(
            MockResponseFileReader
                ("json_save_address_missing_field_request.json").content,
            SaveAddressRequestModel::class.java
        ) as SaveAddressRequestModel
        return service.getSaveAAdressData(saveAddressRequest)
    }

    fun callForSaveAddressApi_WrongFieldType(mockResponseJson: String): Call<SaveAddressResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_save_address_request.json").content, SaveAddressWrongRequestModel::class.java
        )
                as SaveAddressWrongRequestModel
        return service.getSaveAAdressData(requestModel)
    }

    fun callForEmploymentVerificationApi(mockResponseJson: String): Call<VerifyEmployeeResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val verificationRequest = JsonUtils.toObject(
            MockResponseFileReader
                ("json_employee_verification_request.json").content,
            EmploymentVerificationRequestModel::class.java
        ) as EmploymentVerificationRequestModel
        return service.getEmployeeVerifiactionData(verificationRequest)
    }

    fun callForEmploymentVerificationApi_WrongFieldType(mockResponseJson: String): Call<VerifyEmployeeResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val verificationRequest = JsonUtils.toObject(
            MockResponseFileReader
                ("json_employee_verification_request.json").content,
            EmploymentVerificationWrongTypeRequestModel::class.java
        ) as EmploymentVerificationWrongTypeRequestModel
        return service.getEmployeeVerifiactionData(verificationRequest)
    }

    fun callForValidatePanApi(mockResponseJson: String): Call<PanValidateResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val verificationRequest = JsonUtils.toObject(
            MockResponseFileReader
                ("json_validate_pan_request.json").content, ValidatePanRequestModel::class.java
        ) as ValidatePanRequestModel

        return service.getValidatePanData(verificationRequest)
    }

    fun callForSetPasscodeApi(mockResponseJson: String): Call<SetPasswordResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val request = JsonUtils.toObject(
            MockResponseFileReader
                ("json_set_passcode_request.json").content, SetPasscodeRequestModel::class.java
        ) as SetPasscodeRequestModel
        return service.getSetPasscodeData(request)
    }

    fun callForLoginApi(mockResponseJson: String): Call<LoginResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val request = JsonUtils.toObject(
            MockResponseFileReader
                ("json_login_request.json").content,
            LoginRequestModel::class.java
        ) as LoginRequestModel
        return service.getLoginData(request)
    }

    fun callForGetAccountBalanceDetailsApi(): Call<GetAccountBalanceDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_account_balance_success.json").content)
        )
        return service.getAccountBalanceData()
    }

    fun callForGetTransactionDetailsApi(): Call<GetTransactionDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_transaction_details_success.json").content)
        )
        return service.getTransactionDetailsData()
    }

    fun callForGetAccountDetailsApi(): Call<GetAccountDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_account_details_success.json").content)
        )
        return service.getAccountDetailsData()
    }

    fun callForGetUserDetailsApi(): Call<GetUserDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_user_details_success.json").content)
        )
        return service.getUserDetailsData()
    }

    fun callForUpdateUserEmailApi(): Call<UpdateUserDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_update_user_details_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_update_user_email_request.json").content,
            UpdateUserEmailRequestModel::class.java
        )
                as UpdateUserEmailRequestModel

        return service.getUpdateUserEmailData(requestModel)
    }

    fun callForUpdateUserMobileNumberApi(): Call<UpdateUserDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_update_user_details_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_update_user_mobile_request.json").content,
            UpdateUserMobileRequestModel::class.java
        )
                as UpdateUserMobileRequestModel

        return service.getUpdateUserMobileData(requestModel)
    }

    fun callForUpdateUserAddressApi(): Call<UpdateUserDetailsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_update_user_details_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_update_user_address_request.json").content,
            UpdateUserAddressRequestModel::class.java
        )
                as UpdateUserAddressRequestModel

        return service.getUpdateUserAddressData(requestModel)
    }

    fun callForChangePasscodeApi(mockResponseJson: String): Call<SetPasswordResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val request = JsonUtils.toObject(
            MockResponseFileReader
                ("json_change_passcode_request.json").content, SetPasscodeRequestModel::class.java
        ) as SetPasscodeRequestModel
        return service.changePasscode(request)
    }

    fun callForResetPasscodeApi(mockResponseJson: String): Call<SetPasswordResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(mockResponseJson).content)
        )
        val request = JsonUtils.toObject(
            MockResponseFileReader
                ("json_reset_passcode_request.json").content, SetPasscodeRequestModel::class.java
        ) as ResetPasscodeRequestModel
        return service.resetPasscodeData(request)
    }

    fun callForGetUserStateApi(): Call<GetUserStateResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_user_state_success_response.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_get_user_state_request.json").content,
            GetUserStateModel::class.java
        )
                as GetUserStateModel

        return service.getUserStateData(requestModel)
    }

    fun callForGetUserAddressApi(path : String): Call<GetAddressResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader(path).content)
        )
        return service.getUserAddressData()
    }

    // Beneficiary apis
    fun callForGetUserBenificiaryApi(): Call<GetTransactionChargesResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_user_benificiary_success.json").content)
        )
        return service.getTransferChargesData()
    }

    fun callForCreateUserBenificiariesApi(): Call<CreateBeneficiaryResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_create_user_benificiary_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_create_user_benificiary_request.json").content,
            CreateAndUpdateBenificiaryRequestModel::class.java
        )
                as CreateAndUpdateBenificiaryRequestModel
        return service.getCreateBenificiaryData(requestModel)
    }

    fun callForGetRecentUserBenificiariesApi(): Call<GetRecentBeneficiaryResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_recent_user_benificiary_success.json").content)
        )
        return service.getRecentUserBenificiaryData()
    }

    fun callForUpdateUserBenificiariesApi(): Call<UpdateBeneficiaryResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_update_user_benificiary_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_create_user_benificiary_request.json").content,
            CreateAndUpdateBenificiaryRequestModel::class.java
        )
                as CreateAndUpdateBenificiaryRequestModel
        return service.getUpdateUserBenificiaryData(requestModel)
    }

    fun callForUserBenificiaryBankTransferApi(): Call<BeneficiaryBankTransferResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_user_benificiary_bank_transfer_success.json").content)
        )

        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_user_benificiary_bank_transfer_request.json").content,
            UserBenificiaryBankTransferRequestModel::class.java
        )
                as UserBenificiaryBankTransferRequestModel
        return service.getUpdateUserBenificiaryBankTransferData(requestModel)
    }
    fun callForUserBenificiaryBankTransferApi_FailureCase(): Call<BeneficiaryBankTransferResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_user_benificiary_bank_transfer_error.json").content)
        )

        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_user_benificiary_bank_transfer_missing_fields_request.json").content,
            UserBenificiaryBankTransferRequestModel::class.java
        )
                as UserBenificiaryBankTransferRequestModel
        return service.getUpdateUserBenificiaryBankTransferData(requestModel)
    }
    fun callForDeleteUserBenificiary(): Call<DeleteBeneficiaryResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_delete_user_benificiary_success.json").content)
        )
        return service.getDeleteUserBenificiaryData()
    }

    fun callForGetTransferChargesApi(): Call<GetTransactionChargesResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_transfer_charges_success.json").content)
        )
        return service.getTransferChargesData()
    }

    // Salary advance info
    fun callForGetSalaryAdvanceInfoApi(): Call<GetSalaryAdvanceInfoResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_salary_advance_info_success.json").content)
        )
        return service.getSalaryAdvanceinfoData()
    }

    // KYC SDK
    fun callForKycSelfieApi(myDrawable: Drawable): Call<KYCSelfieResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_kyc_selfie_success.json").content)
//                .setBody(MockResponseFileReader("json_kyc_selfie_response.json").content)
        )

        var file: File
//        val bitmap = convertToBitmap(myDrawable)
//        if (bitmap != null) {
//            file= bitmapToFile(bitmap, "8544941607_kyc_selfie.png")
//        }
        file = File(myDrawable.toString())
        var filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file", "8544941607_kyc_selfie.png",
            RequestBody.create(MultipartBody.FORM, file)
        )
//        var filePart=   MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart(
//                BaaSConstants.BS_KEY_LIVE_PHOTO,
//                "kyc_selfie_tushar.png",
//                RequestBody.create(
//                   MultipartBody.FORM,
//                    file)
//                ).build()

        return service.getKycSelfieData(filePart)
    }

    fun callForKycAadharApi(): Call<KYCAadharResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_kyc_aadhar_success.json").content)
//                .setBody(MockResponseFileReader("json_kyc_aadhar_response.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_kyc_aadhar_request.json").content,
            KycAadharRequestModel::class.java
        )
                as KycAadharRequestModel
        return service.getKycAadharData(requestModel)
    }

    fun callForKycLocationApi(): Call<KYCLocationResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_kyc_location_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_kyc_location_request.json").content,
            KycLocationRequestModel::class.java
        )
                as KycLocationRequestModel
        return service.getKycLocationData(requestModel)
    }

    // CARD APIS
    fun callForGetImageApi(): Call<CardImageResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_card_image_success.json").content)
        )
        return service.getCardImageData()
    }

    fun callForGetCardDetailsDataApi(): Call<CardDetailResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_card_details_success.json").content)
        )
        return service.getCardDetailsData()
    }

    fun callForGetCardTransactionModeDataApi(): Call<GetTransactionModeResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_get_card_transaction_mode_success.json").content)
        )
        return service.getCardTransactionModeData()
    }

    fun callForSetCardTransactionModeDataApi(): Call<SetTransactionModeResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_set_card_transaction_mode_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_set_card_transaction_mode_request.json").content,
            GetTransactionModeResponse::class.java
        )
                as GetTransactionModeResponse
        return service.setCardTransactionModeData(requestModel)
    }

    fun callForSetBlockCardDataApi(): Call<BlockCardResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_block_card_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_block_card_request.json").content,
            BlockCardRequestModel::class.java
        )
                as BlockCardRequestModel
        return service.setBlockCardData(requestModel)
    }

    fun callForSetUnBlockCardDataApi(): Call<UnblockCardResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_unblock_card_success.json").content)
        )
        return service.setUnBlockCardData()
    }

    fun callForSetPinDataApi(): Call<CardSetPinResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_set_pin_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_card_set_pin_request.json").content,
            SetPinRequestModel::class.java
        )
                as SetPinRequestModel
        return service.setCardPinData(requestModel)
    }

    fun callForSetCardLimitDataApi(): Call<SetLimitResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_set_limit_success.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_card_set_limit_request.json").content,
            GetLimitsResponse::class.java
        )
                as GetLimitsResponse
        return service.setCardLimitsData(requestModel)
    }

    fun callForGetCardLimitDataApi(): Call<GetLimitsResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_get_limit_success.json").content)
        )
        return service.getCardLimitsData()
    }

    fun callForUpdateCardPinSetStatusDataApi(): Call<UpdateCardPinSetStatusResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_update_pin_status_success.json").content)
        )
        return service.updateCardPinStatusData()
    }

    fun callForGetCardPinDataApi(): Call<GetCardPinStatusResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_get_pin_success.json").content)
        )
        return service.getCardPinData()
    }
    fun callForCardReorderApi(): Call<CardReorderResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_card_reorder_success_responset.json").content)
        )
        return service.cardReOrder()
    }
    fun callForValidateCardKitDataApi(): Call<ValidateCardKitResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_validate_card_kit_success_response.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_validate_card_kit_request.json").content,
            ValidateCardKitModel::class.java
        )
                as ValidateCardKitModel
        return service.validateCardKit(requestModel)
    }
    // for validated false
    fun callForValidateFalseCardKitApi(): Call<ValidateCardKitResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_validate_card_kit_error_response.json").content)
        )
        val requestModel = JsonUtils.toObject(
            MockResponseFileReader
                ("json_validate_card_kit_error_request.json").content,
            ValidateCardKitModel::class.java
        )
                as ValidateCardKitModel
        return service.validateCardKit(requestModel)
    }

    fun callForValidateCardKitStatusApi(): Call<GetValidateCardKitStatusResponse> {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(MockResponseFileReader("json_validate_card_kit_status_success_response.json").content)
        )
        return service.getValidateCardKitStatus()
    }

    fun convertToBitmap(drawable: Drawable): Bitmap? {
        val mutableBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, 300, 300)
        drawable.draw(canvas)
        return mutableBitmap
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            file // it will return null
        }
    }
}