package com.payu.baas.core.model.apiModels

import android.content.Context
import com.payu.baas.core.interfaces.SdkCallback
import com.payu.baas.core.enums.ApiName

class ApiModelFactory(
    val context: Context,
    val apiName: ApiName,
    val requestMap: HashMap<String, Any>,
    val sdkCallback: SdkCallback
) {
    fun getApiModel(): ApiModel {
        return when (apiName) {
            ApiName.SERVER_CALL -> ServerTestingModel(context, requestMap, sdkCallback)
            ApiName.GET_CLIENT_TOKEN -> GetClientTokenApiModel(context, requestMap, sdkCallback)
            ApiName.SEND_OTP -> SendOtpApiModel(context, requestMap, sdkCallback)
            ApiName.VERIFY_OTP -> VerifyOtpApiModel(context, requestMap, sdkCallback)
            ApiName.SAVE_ADDRESS -> SaveAddressApiModel(context, requestMap, sdkCallback)
            ApiName.GET_ADDRESS -> GetAddressApiModel(context, requestMap, sdkCallback)
            ApiName.VERIFY_EMPLOYEE -> VerifyEmployeeApiModel(context, requestMap, sdkCallback)
            ApiName.SET_PASSWORD -> SetPasswordApiModel(context, requestMap, sdkCallback)
            ApiName.LOGIN -> LoginApiModel(context, requestMap, sdkCallback)
            ApiName.GET_USER_STATE -> GetUserStateApiModel(context, requestMap, sdkCallback)
            ApiName.PAN_VALIDATE -> PanValidateApiModel(context, requestMap, sdkCallback)
            ApiName.UPDATE_PASSWORD -> UpdatePasswordApiModel(context, requestMap, sdkCallback)
            ApiName.CARD_IMAGE -> CardImageApiModel(context, requestMap, sdkCallback)
            ApiName.CARD_DETAILS -> CardDetailApiModel(context, requestMap, sdkCallback)
            ApiName.CARD_FULFILMENT -> CardFulmentApiModel(context, requestMap, sdkCallback)
            ApiName.SET_TRANSACTION_MODE -> SetTransactionModeApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_TRANSACTION_MODE -> GetTransactionModeApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.SET_LIMITS -> SetLimitApiModel(context, requestMap, sdkCallback)
            ApiName.GET_LIMITS -> GetLimitsApiModel(context, requestMap, sdkCallback)
            ApiName.CARD_SET_PIN -> CardSetPinApiModel(context, requestMap, sdkCallback)
            ApiName.BLOCK_CARD -> BlockCardApiModel(context, requestMap, sdkCallback)
            ApiName.UNBLOCK_CARD -> UnblockCardApiModel(context, requestMap, sdkCallback)
            ApiName.GET_PIN_STATUS -> GetCardPinStatusApiModel(context, requestMap, sdkCallback)
            ApiName.UPDATE_PIN -> UpdateCardPinSetStatusApiModel(context, requestMap, sdkCallback)
            ApiName.ADD_BENEFICIARY -> CreateBeneficiaryApiModel(context, requestMap, sdkCallback)
            ApiName.GET_BENEFICIARY -> GetBeneficiaryApiModel(context, requestMap, sdkCallback)
            ApiName.UPDATE_BENEFICIARY -> UpdateBeneficiaryApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.PREVALIDATE_TRANSACTION -> PrevalidateTrasactionApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.BENEFICIARY_BANK_TRANSFER -> BeneficiaryBankTransferApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_RECENT_BENEFICIARY -> GetRecentBeneficiaryApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.DELETE_BENEFICIARY -> DeleteBeneficiaryApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.UNDO_DELETED_BENEFICIARY -> UndoDeletedBeneficiaryApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_TRANSACTION_CHARGES -> GetTransactionChargesApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_SALARY_ADVANCE_INFO -> GetSalaryAdvanceInfoApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_ACCOUNT_BALANCE_DETAILS -> GetAccountBalanceApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_TRANSACTION_LIST -> GetTransactionListApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_TRANSACTION_DETAIL -> GetTransactionDetailApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_ACCOUNT_DETAILS -> GetAccountDetailsApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_USER_DETAILS -> GetUserDetailsApiModel(context, requestMap, sdkCallback)
            ApiName.UPDATE_USER_EMAIL -> UpdateEmailApiModel(context, requestMap, sdkCallback)
            ApiName.UPDATE_MOBILE_NUMBER -> UpdateMobileNumberApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.UPDATE_USER_ADDRESS -> UpdateAddressApiModel(context, requestMap, sdkCallback)
            ApiName.KYC_SELFIE -> KYCSelfieApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KYC_AADHAR -> KYCAadharApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KYC_LOCATION -> KYCLocationApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KYC_RESULTS -> AddKYCResultApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.VERIFY_KYC_RESULTS -> VerifyKYCResultApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_APPLICATION_ID ->

                GetApplicationIdApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_ONBOARD_STATUS -> GetOnBoardStatusApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KARZA_TOKEN -> KarzaSessionApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KARZA_ADD_NEW_CUSTOMER, -> KarzaNewCustomerApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KARZA_GENERATE_CUSTOMER_TOKEN -> KarzaCustomerTokenApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.KARZA_KEY -> GetKarzaKeyApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.CARD_REORDER -> CardReorderApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.HELP -> HelpApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.FAQS -> FAQsApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.RATES_CHARGES -> RatesCharegesApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_S3_BUCKET_LINK -> GetS3BucketLinkApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.VALIDATE_CARD_KIT -> ValidateCardKitApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_VALIDATE_CARD_KIT_STATUS -> GetValidateCardKitStatusApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_TIPS -> GetTipsApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_OFFER -> GetOfferApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.GET_NOTIFICATIONS -> GetNotificationApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.LOGOUT -> LogOutApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.IFSC_CODE -> IFSCApiModel(
                context,
                requestMap,
                sdkCallback
            )
            ApiName.ZENDESK_CREDENTIALS -> GetZendeskCredentialsApiModel(
                context,
                requestMap,
                sdkCallback
            )
            else -> GetClientTokenApiModel(context, requestMap, sdkCallback)
        }
    }
}