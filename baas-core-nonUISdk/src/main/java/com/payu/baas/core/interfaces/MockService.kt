package com.payu.baas.core.interfaces

import com.payu.baas.core.model.model.*
import com.payu.baas.core.model.responseModels.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface MockService {
    //OnBoarding
    @POST("user/mobile")
    fun getCurrentOtpData(@Body sendOtp: SendOtp): Call<SendOtpResponse>

    @POST("user/mobile/otp/verify")
    fun getVerifyOtpData(@Body verifyOtp: VerifyOtp): Call<VerifyOtpResponse>

    @POST("user/mobile/otp/verify")
    fun getVerifyOtpErrorData(@Body verifyOtp: VerifyOtp): Call<ApiResponse>

    @POST("user/address")
    fun getSaveAAdressData(@Body saveAddress: SaveAddressRequestModel): Call<SaveAddressResponse>

    @POST("user/address")
    fun getSaveAAdressData(@Body saveAddress: SaveAddressWrongRequestModel): Call<SaveAddressResponse>

    @POST("user/employee/verify")
    fun getEmployeeVerifiactionData(@Body verification: EmploymentVerificationRequestModel): Call<VerifyEmployeeResponse>

    @POST("user/employee/verify")
    fun getEmployeeVerifiactionData(@Body verification: EmploymentVerificationWrongTypeRequestModel): Call<VerifyEmployeeResponse>

    @POST("user/pan-validate")
    fun getValidatePanData(@Body verification: ValidatePanRequestModel): Call<PanValidateResponse>

    @POST("user/passcode")
    fun getSetPasscodeData(@Body requestModel: SetPasscodeRequestModel): Call<SetPasswordResponse>

    @POST("login")
    fun getLoginData(@Body requestModel: LoginRequestModel): Call<LoginResponse>

    //Home Screen
    @GET("account/balance")
    fun getAccountBalanceData(): Call<GetAccountBalanceDetailsResponse>

    @GET("transactions?startDate=2020-05-06&endDate=2020-07-06&accountType=saving&debitIndicator=Debit")
    fun getTransactionDetailsData(): Call<GetTransactionDetailsResponse>

    @GET("account/details")
    fun getAccountDetailsData(): Call<GetAccountDetailsResponse>

    @GET("user/details")
    fun getUserDetailsData(): Call<GetUserDetailsResponse>

    @PUT("user/details")
    fun getUpdateUserEmailData(@Body requestModel: UpdateUserEmailRequestModel): Call<UpdateUserDetailsResponse>

    @PUT("user/details")
    fun getUpdateUserMobileData(@Body requestModel: UpdateUserMobileRequestModel): Call<UpdateUserDetailsResponse>

    @PUT("user/details")
    fun getUpdateUserAddressData(@Body requestModel: UpdateUserAddressRequestModel): Call<UpdateUserDetailsResponse>

    @PUT("user/passcode")
    fun changePasscode(@Body requestModel:SetPasscodeRequestModel ): Call<SetPasswordResponse>

    @PUT("user/passcode/reset")
    fun resetPasscodeData(@Body requestModel:ResetPasscodeRequestModel ): Call<SetPasswordResponse>

    @GET("user/state")
    fun getUserStateData(@Body requestModel:GetUserStateModel ): Call<GetUserStateResponse>

    @GET("user/address")
    fun getUserAddressData(): Call<GetAddressResponse>

    //Benificiary Apis
    @GET("transaction/charges")
    fun getTransferChargesData(): Call<GetTransactionChargesResponse>

    @GET("user/beneficiary?page=2&size=5&sort=asc")
    fun getUserBenificiaryData(): Call<GetBeneficiaryResponse>

    @GET("user/beneficiary/recent?page=1&size=2&sort=asc")
    fun getRecentUserBenificiaryData(): Call<GetRecentBeneficiaryResponse>

    @POST("user/beneficiary")
    fun getCreateBenificiaryData(@Body requestModel: CreateAndUpdateBenificiaryRequestModel): Call<CreateBeneficiaryResponse>

    @POST("user/T1234/beneficiary")
    fun getUpdateUserBenificiaryData(@Body requestModel: CreateAndUpdateBenificiaryRequestModel): Call<UpdateBeneficiaryResponse>

    @POST("user/beneficiary/bank-transfer")
    fun getUpdateUserBenificiaryBankTransferData(@Body requestModel: UserBenificiaryBankTransferRequestModel): Call<BeneficiaryBankTransferResponse>

    @DELETE("user/T1234/beneficiary")
    fun getDeleteUserBenificiaryData(): Call<DeleteBeneficiaryResponse>

    // salary advance info
    @GET("user/salaryAdvance")
    fun getSalaryAdvanceinfoData(): Call<GetSalaryAdvanceInfoResponse>

    //KYC SDK
    @POST("kyc/selfie")
    fun getKycSelfieData(@Body requestModel: MultipartBody.Part): Call<KYCSelfieResponse>

    @POST("kyc/aadhaar")
    fun getKycAadharData(@Body requestModel: KycAadharRequestModel): Call<KYCAadharResponse>

    @POST("kyc/location")
    fun getKycLocationData(@Body requestModel: KycLocationRequestModel): Call<KYCLocationResponse>

    // Card apis
    @GET("card/image-url")
    fun getCardImageData(): Call<CardImageResponse>

    @GET("card/details")
    fun getCardDetailsData(): Call<CardDetailResponse>

    @GET("card/fulfillment")
    fun getCardFulfillmentData(): Call<CardFulfilmentResponse>

    @POST("card/transaction/modes")
    fun setCardTransactionModeData(@Body requestModel: GetTransactionModeResponse): Call<SetTransactionModeResponse>

    @GET("card/transaction/modes")
    fun getCardTransactionModeData(): Call<GetTransactionModeResponse>

    @POST("card/channel/limits")
    fun setCardLimitsData(@Body requestModel: GetLimitsResponse): Call<SetLimitResponse>

    @GET("card/channel/limits")
    fun getCardLimitsData(): Call<GetLimitsResponse>

    @PUT("card/block")
    fun setBlockCardData(@Body requestModel: BlockCardRequestModel): Call<BlockCardResponse>

    @PUT("card/unblock")
    fun setUnBlockCardData(): Call<UnblockCardResponse>

    @PUT("card/pin/url")
    fun setCardPinData(@Body requestModel: SetPinRequestModel): Call<CardSetPinResponse>

    @PUT("card/pin?pin_status=true")
    fun updateCardPinStatusData(): Call<UpdateCardPinSetStatusResponse>

    @GET("card/pin/status")
    fun getCardPinData(): Call<GetCardPinStatusResponse>

    @PUT("card/reorder")
    fun cardReOrder(): Call<CardReorderResponse>

    @PUT("card/validate-kit")
    fun validateCardKit(@Body requestModel: ValidateCardKitModel): Call<ValidateCardKitResponse>

    @PUT("card/status")
    fun getValidateCardKitStatus(): Call<GetValidateCardKitStatusResponse>
}
