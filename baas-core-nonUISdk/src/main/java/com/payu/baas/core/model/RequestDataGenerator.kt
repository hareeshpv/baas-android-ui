package com.payu.baas.core.model

import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.util.BaaSConstants
import com.payu.baas.core.util.BaaSConstants.INVALID_DATA

class RequestDataGenerator(val apiDetails: ApiDetails) {
    fun getRequestData(): RequestData {
        return when (apiDetails.apiName) {
            ApiName.SERVER_CALL -> validateServerAPICall()
            ApiName.GET_CLIENT_TOKEN -> validateGetAccessTokenData()
            ApiName.SEND_OTP -> validateSendOtp()
            ApiName.VERIFY_OTP -> validateVerifyOtp()
            ApiName.GET_ONBOARD_STATUS -> validateOnBoardUserStatus()
            ApiName.SAVE_ADDRESS -> validateSaveAddress()
            ApiName.GET_ADDRESS -> validateGetAddress()
            ApiName.VERIFY_EMPLOYEE -> validateVerifyEmployee()
            ApiName.SET_PASSWORD -> validateSetPassword()
            ApiName.LOGIN -> validateLogin()
            ApiName.GET_USER_STATE -> validateGetUserState()
            ApiName.PAN_VALIDATE -> validatePan()
            ApiName.UPDATE_PASSWORD -> validateUpdatePassword()
            ApiName.CARD_IMAGE -> validateCardImage()
            ApiName.CARD_DETAILS -> validateCardDetail()
            ApiName.CARD_FULFILMENT -> validateCardFulfilment()
            ApiName.SET_TRANSACTION_MODE -> validateSetTransactionMode()
            ApiName.GET_TRANSACTION_MODE -> validateGetTransactionMode()
            ApiName.GET_LIMITS -> validateGetLImits()
            ApiName.SET_LIMITS -> validateSetLimits()
            ApiName.CARD_SET_PIN -> validateCardSetPin()
            ApiName.BLOCK_CARD -> validateBlockCard()
            ApiName.UNBLOCK_CARD -> validateUnblockCard()
            ApiName.GET_PIN_STATUS -> validatePinStatus()
            ApiName.UPDATE_PIN -> validateUpdatePinStatus()
            ApiName.ADD_BENEFICIARY -> validateAddBeneficiary()
            ApiName.GET_BENEFICIARY -> validateGetBeneficiary()
            ApiName.UPDATE_BENEFICIARY -> validateUpdateBeneficiary()
            ApiName.PREVALIDATE_TRANSACTION -> validatePrevalidateTransaction()
            ApiName.BENEFICIARY_BANK_TRANSFER -> validateBeneficiaryBankTransfer()
            ApiName.GET_RECENT_BENEFICIARY -> validateGetRecentBeneficiary()
            ApiName.DELETE_BENEFICIARY -> validateDeleteBeneficiary()
            ApiName.UNDO_DELETED_BENEFICIARY -> validateUndoDeletedBeneficiary()
            ApiName.GET_SALARY_ADVANCE_INFO -> validateGetSalaryAdvanceInfo()
            ApiName.GET_ACCOUNT_BALANCE_DETAILS -> validateGetAccountBalance()
            ApiName.GET_ACCOUNT_DETAILS -> validateGetAccountData()
            ApiName.GET_USER_DETAILS -> validateGetUserData()
            ApiName.GET_TRANSACTION_LIST -> validateGetTransactionListData()
            ApiName.GET_TRANSACTION_DETAIL -> validateGetTransactionDetailData()
            ApiName.GET_TRANSACTION_CHARGES -> validateGetTransferChargesData()
            ApiName.KARZA_KEY -> validateGetKarzaKeyData()
            ApiName.CARD_REORDER -> validateGetCardReorderData()
            ApiName.UPDATE_USER_EMAIL -> validateUpdateUserEmail()
            ApiName.UPDATE_USER_ADDRESS -> validateUpdateUserAddress()
            ApiName.UPDATE_MOBILE_NUMBER -> validateUpdateUserMobileNumber()
            ApiName.KARZA_TOKEN -> validateKarzaSessionData()
            ApiName.KARZA_ADD_NEW_CUSTOMER -> validateKarzaNewCustomerData()
            ApiName.KARZA_GENERATE_CUSTOMER_TOKEN -> validateKarzaGenerateUserTokenData()
            ApiName.GET_APPLICATION_ID -> validateApplicationId()
            ApiName.KYC_AADHAR -> validateKYCAadhar()
            ApiName.KYC_LOCATION -> validateKYCLocation()
            ApiName.KYC_SELFIE -> validateKYCSelfie()
            ApiName.KYC_RESULTS -> validateAddKYCResults()
            ApiName.VERIFY_KYC_RESULTS -> validateVerifyKYCResults()
            ApiName.HELP -> validateHelp()
            ApiName.FAQS -> validateFAQs()
            ApiName.RATES_CHARGES -> validateRatesCharges()
            ApiName.GET_S3_BUCKET_LINK -> validateS3BucketLink()
            ApiName.VALIDATE_CARD_KIT -> validateCardKit()
            ApiName.GET_VALIDATE_CARD_KIT_STATUS -> validateGetCardKitStatus()
            ApiName.GET_NOTIFICATIONS -> validateNotifications()
            ApiName.GET_OFFER -> validateOffer()
            ApiName.GET_TIPS -> validateTips()
            ApiName.LOGOUT -> validateLogout()
            ApiName.IFSC_CODE -> validateIfsCode()
            ApiName.ZENDESK_CREDENTIALS -> validateZendeskCredentials()

            else -> RequestData(null, false, INVALID_DATA)
        }
    }

    private fun validateNotifications(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateOffer(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateTips(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateLogout(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validatePinStatus(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateRatesCharges(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateFAQs(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateHelp(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateS3BucketLink(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateCardKit(): RequestData {
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_IS_CARD_RECEIVED] =
            apiDetails.apiParams.isCardReceived!!
        return RequestData(resultMap, true)
    }

    private fun validateIfsCode(): RequestData {
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_IFSC] =
            apiDetails.apiParams.ifsc!!.toString()
        return RequestData(resultMap, true)
    }

    private fun validateGetCardKitStatus(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validatePrevalidateTransaction(): RequestData {
        return when {
            apiDetails.apiParams.txnAmount.isNullOrEmpty() ->
                sendErrorResponse(
                    BaaSConstants.TRANSACTION_AMOUNT_EMPTY_ERROR_MESSAGE,
                    BaaSConstants.TRANSACTION_AMOUNT_EMPTY_ERROR_CODE
                )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_TRANSACTION_AMOUNT] =
                    apiDetails.apiParams.txnAmount!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateUpdatePinStatus(): RequestData {
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_PIN_STATUS] = apiDetails.apiParams.pin_status!!
        return RequestData(resultMap, true)
    }

    private fun validateGetAccessTokenData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateSendOtp(): RequestData {
        return when {
            apiDetails.apiParams.mobileNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_MOBILE_NUMBER] = apiDetails.apiParams.mobileNumber!!
                RequestData(resultMap, true)

            }

        }

    }

    private fun validateSetTransactionMode(): RequestData {
        return when {
            apiDetails.apiParams.transactionModes.toString().isNullOrEmpty() ->
                sendErrorResponse(
                    BaaSConstants.TRANSACTION_MODE_EMPTY_ERROR_MESSAGE,
                    BaaSConstants.TRANSACTION_MODE_EMPTY_ERROR_CODE
                )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_TRANSACTION_MODES] =
                    apiDetails.apiParams.transactionModes!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateSetLimits(): RequestData {
        return when {
            apiDetails.apiParams.transactionLimits.toString().isNullOrEmpty() ->
                sendErrorResponse(
                    BaaSConstants.TRANSACTION_LIMIT_EMPTY_ERROR_MESSAGE,
                    BaaSConstants.TRANSACTION_LIMIT_EMPTY_ERROR_CODE
                )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_TRANSACTION_LIMITS] =
                    apiDetails.apiParams.transactionLimits!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateUnblockCard(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateServerAPICall(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateBlockCard(): RequestData {
        return when {
            apiDetails.apiParams.reason.isNullOrEmpty() ->
                sendErrorResponse(
                    BaaSConstants.REASON_EMPTY_ERROR_MESSAGE,
                    BaaSConstants.REASON_EMPTY_ERROR_CODE
                )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_REASON] = apiDetails.apiParams.reason!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetTransactionMode(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetLImits(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateCardSetPin(): RequestData {
        return when {
            apiDetails.apiParams.redirect_link.isNullOrEmpty() ->
                sendErrorResponse(
                    BaaSConstants.REDIRECT_LINK_EMPTY_ERROR_MESSAGE,
                    BaaSConstants.REDIRECT_LINK_EMPTY_ERROR_CODE
                )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_REDIRECT_LINK] = apiDetails.apiParams.redirect_link!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateCardImage(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateCardDetail(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateCardFulfilment(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateVerifyOtp(): RequestData {
        return when {
            apiDetails.apiParams.mobile.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.code.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.OTP_EMPTY_ERROR_MESSAGE,
                BaaSConstants.OTP_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_IDENTITY] = apiDetails.apiParams.mobile!!
                resultMap[BaaSConstants.BS_KEY_CODE] = apiDetails.apiParams.code!!
                resultMap[BaaSConstants.BS_KEY_TYPE] = BaaSConstants.BS_VALUE_TYPE
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateSaveAddress(): RequestData {
        return when {
            apiDetails.apiParams.pan.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.title.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.TITLE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.TITLE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.firstName.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.FIRST_NAME_EMPTY_ERROR_MESSAGE,
                BaaSConstants.FIRST_NAME_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.lastName.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.LAST_NAME_EMPTY_ERROR_MESSAGE,
                BaaSConstants.LAST_NAME_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.gender.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.GENDER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.GENDER_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.dob.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.DOB_EMPTY_ERROR_MESSAGE,
                BaaSConstants.DOB_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.country.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.COUNTRY_EMPTY_ERROR_MESSAGE,
                BaaSConstants.COUNTRY_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.countryCode.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.COUNTRY_CODE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.COUNTRY_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.mobile.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.addressLine1.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.ADDRESS_1_EMPTY_ERROR_MESSAGE,
                BaaSConstants.ADDRESS_1_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.addressLine2.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.ADDRESS_2_EMPTY_ERROR_MESSAGE,
                BaaSConstants.ADDRESS_2_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.city.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.CITY_EMPTY_ERROR_MESSAGE,
                BaaSConstants.CITY_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.pinCode.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PIN_CODE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PIN_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.state.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.STATE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.STATE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.employeeId.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_PAN] = apiDetails.apiParams.pan!!
                resultMap[BaaSConstants.BS_KEY_TITLE] = apiDetails.apiParams.title!!
                resultMap[BaaSConstants.BS_KEY_FIRST_NAME] = apiDetails.apiParams.firstName!!
                resultMap[BaaSConstants.BS_KEY_LAST_NAME] = apiDetails.apiParams.lastName!!
                resultMap[BaaSConstants.BS_KEY_GENDER] = apiDetails.apiParams.gender!!
                resultMap[BaaSConstants.BS_KEY_DOB] = apiDetails.apiParams.dob!!
                resultMap[BaaSConstants.BS_KEY_EMAIL] = apiDetails.apiParams.email!!
                resultMap[BaaSConstants.BS_KEY_COUNTRY] = apiDetails.apiParams.country!!
                resultMap[BaaSConstants.BS_KEY_COUNTRY_CODE] = apiDetails.apiParams.countryCode!!
                resultMap[BaaSConstants.BS_KEY_MOBILE] = apiDetails.apiParams.mobile!!
                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_1] = apiDetails.apiParams.addressLine1!!
                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_2] = apiDetails.apiParams.addressLine2!!
                resultMap[BaaSConstants.BS_KEY_CITY] = apiDetails.apiParams.city!!
                resultMap[BaaSConstants.BS_KEY_PIN_CODE] = apiDetails.apiParams.pinCode!!
                resultMap[BaaSConstants.BS_KEY_STATE] = apiDetails.apiParams.state!!
                resultMap[BaaSConstants.BS_KEY_EMPLOYEE_ID] = apiDetails.apiParams.employeeId!!
                RequestData(resultMap, true)
            }
        }
    }
    private fun validateOnBoardUserStatus(): RequestData {
        return when {
            apiDetails.apiParams.mobile.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
//            apiDetails.apiParams.pan.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.title.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.TITLE_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.TITLE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.firstName.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.FIRST_NAME_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.FIRST_NAME_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.lastName.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.LAST_NAME_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.LAST_NAME_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.gender.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.GENDER_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.GENDER_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.dob.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.DOB_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.DOB_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.country.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.COUNTRY_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.COUNTRY_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.countryCode.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.COUNTRY_CODE_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.COUNTRY_CODE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.addressLine1.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.ADDRESS_1_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.ADDRESS_1_CODE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.addressLine2.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.ADDRESS_2_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.ADDRESS_2_CODE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.city.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.CITY_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.CITY_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.pinCode.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.PIN_CODE_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.PIN_CODE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.state.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.STATE_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.STATE_EMPTY_ERROR_CODE
//            )
//            apiDetails.apiParams.employeeId.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_CODE
//            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_MOBILE] = apiDetails.apiParams.mobile!!
//                resultMap[BaaSConstants.BS_KEY_PAN] = apiDetails.apiParams.pan!!
//                resultMap[BaaSConstants.BS_KEY_TITLE] = apiDetails.apiParams.title!!
//                resultMap[BaaSConstants.BS_KEY_FIRST_NAME] = apiDetails.apiParams.firstName!!
//                resultMap[BaaSConstants.BS_KEY_LAST_NAME] = apiDetails.apiParams.lastName!!
//                resultMap[BaaSConstants.BS_KEY_GENDER] = apiDetails.apiParams.gender!!
//                resultMap[BaaSConstants.BS_KEY_DOB] = apiDetails.apiParams.dob!!
//                resultMap[BaaSConstants.BS_KEY_EMAIL] = apiDetails.apiParams.email!!
//                resultMap[BaaSConstants.BS_KEY_COUNTRY] = apiDetails.apiParams.country!!
//                resultMap[BaaSConstants.BS_KEY_COUNTRY_CODE] = apiDetails.apiParams.countryCode!!
//                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_1] = apiDetails.apiParams.addressLine1!!
//                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_2] = apiDetails.apiParams.addressLine2!!
//                resultMap[BaaSConstants.BS_KEY_CITY] = apiDetails.apiParams.city!!
//                resultMap[BaaSConstants.BS_KEY_PIN_CODE] = apiDetails.apiParams.pinCode!!
//                resultMap[BaaSConstants.BS_KEY_STATE] = apiDetails.apiParams.state!!
//                resultMap[BaaSConstants.BS_KEY_EMPLOYEE_ID] = apiDetails.apiParams.employeeId!!
                RequestData(resultMap, true)
            }
        }
    }
    private fun validateVerifyEmployee(): RequestData {
        return when {
            apiDetails.apiParams.mobile.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.panNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.employeeId.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.EMPLOYEE_ID_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_MOBILE] = apiDetails.apiParams.mobile!!
                resultMap[BaaSConstants.BS_KEY_PAN] = apiDetails.apiParams.panNumber!!
                resultMap[BaaSConstants.BS_KEY_BRAND_EMP_ID] = apiDetails.apiParams.employeeId!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateSetPassword(): RequestData {
        return when {
            apiDetails.apiParams.password.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PASSWORD_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PASSWORD_EMPTY_ERROR_CODE
            )

            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_OLD_PASSWORD] = ""
                resultMap[BaaSConstants.BS_KEY_NEW_PASSWORD] = apiDetails.apiParams.password!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateLogin(): RequestData {
        return when {
            apiDetails.apiParams.username.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.USERNAME_EMPTY_ERROR_MESSAGE,
                BaaSConstants.USERNAME_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.password.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PASSWORD_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PASSWORD_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_USERNAME] = apiDetails.apiParams.username!!
                resultMap[BaaSConstants.BS_KEY_PASSWORD] = apiDetails.apiParams.password!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetUserState(): RequestData {
        return when {
            apiDetails.apiParams.mobileNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_MOBILE_NUMBER_FOR_USER_STATE] =
                    apiDetails.apiParams.mobileNumber!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validatePan(): RequestData {
        return when {
            apiDetails.apiParams.panNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PAN_NUMBER_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_PAN_NUMBER] = apiDetails.apiParams.panNumber!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateUpdatePassword(): RequestData {
        return when {
//            apiDetails.apiParams.oldPassword.isNullOrEmpty() -> sendErrorResponse(
//                BaaSConstants.OLD_PASSWORD_EMPTY_ERROR_MESSAGE,
//                BaaSConstants.OLD_PASSWORD_EMPTY_ERROR_CODE
//            )
            apiDetails.apiParams.newPassword.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.NEW_PASSWORD_EMPTY_ERROR_MESSAGE,
                BaaSConstants.NEW_PASSWORD_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
//                resultMap[BaaSConstants.BS_KEY_OLD_PASSWORD] = apiDetails.apiParams.oldPassword!!
                resultMap[BaaSConstants.BS_KEY_NEW_PASSWORD] = apiDetails.apiParams.newPassword!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateAddBeneficiary(): RequestData {
        return when {
            apiDetails.apiParams.beneficiaryName.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_NAME_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_NAME_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.accountNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ACCOUNT_NUMBER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ACCOUNT_NUMBER_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.ifsc.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_IFSC_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_IFSC_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_BENEFICIARY_NAME] =
                    apiDetails.apiParams.beneficiaryName!!
                resultMap[BaaSConstants.BS_KEY_ACCOUNT_NUMBER] =
                    apiDetails.apiParams.accountNumber!!
                resultMap[BaaSConstants.BS_KEY_IFSC] = apiDetails.apiParams.ifsc!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetBeneficiary(): RequestData {
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_PAGE] = apiDetails.apiParams.page
        resultMap[BaaSConstants.BS_KEY_SIZE] = apiDetails.apiParams.size
        resultMap[BaaSConstants.BS_KEY_SORT] = apiDetails.apiParams.sort
        return RequestData(resultMap, true)
    }

    private fun validateUpdateBeneficiary(): RequestData {
        return when {
            apiDetails.apiParams.beneficiaryId.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.beneficiaryName.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_NAME_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_NAME_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.accountNumber.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ACCOUNT_NUMBER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ACCOUNT_NUMBER_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.ifsc.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_IFSC_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_IFSC_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_BENEFICIARY_ID] =
                    apiDetails.apiParams.beneficiaryId!!
                resultMap[BaaSConstants.BS_KEY_BENEFICIARY_NAME] =
                    apiDetails.apiParams.beneficiaryName!!
                resultMap[BaaSConstants.BS_KEY_ACCOUNT_NUMBER] =
                    apiDetails.apiParams.accountNumber!!
                resultMap[BaaSConstants.BS_KEY_IFSC] = apiDetails.apiParams.ifsc!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateBeneficiaryBankTransfer(): RequestData {
        return when {
            /*apiDetails.apiParams.remarks.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.REMARKS_EMPTY_ERROR_MESSAGE,
                BaaSConstants.REMARKS_EMPTY_ERROR_CODE
            )*/
            apiDetails.apiParams.amount == 0.0 -> sendErrorResponse(
                BaaSConstants.AMOUNT_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AMOUNT_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.beneficiaryId.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_CODE
            )

            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_REMARKS] =
                    apiDetails.apiParams.remarks!!
                resultMap[BaaSConstants.BS_KEY_USER_BANK_TRANSFER_BENEFICIARY_ID] =
                    apiDetails.apiParams.beneficiaryId!!
                resultMap[BaaSConstants.BS_KEY_AMOUNT] = apiDetails.apiParams.amount!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetRecentBeneficiary(): RequestData {
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_PAGE] = apiDetails.apiParams.page
        resultMap[BaaSConstants.BS_KEY_SIZE] = apiDetails.apiParams.size
        resultMap[BaaSConstants.BS_KEY_SORT] = apiDetails.apiParams.sort
        return RequestData(resultMap, true)
    }

    private fun validateDeleteBeneficiary(): RequestData {
        return when {
            apiDetails.apiParams.userBeneFiciaryIds.toString().isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_USER_BENEFICIARY_IDS] =
                    apiDetails.apiParams.userBeneFiciaryIds!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateUndoDeletedBeneficiary(): RequestData {
        return when {
            apiDetails.apiParams.userBeneFiciaryIds.toString().isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.BENEFICIARY_ID_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_USER_BENEFICIARY_IDS] =
                    apiDetails.apiParams.userBeneFiciaryIds!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetSalaryAdvanceInfo(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetAccountBalance(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetAccountData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetUserData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetTransferChargesData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetKarzaKeyData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetCardReorderData(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateUpdateUserEmail(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.emailId.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.EMAIL_EMPTY_ERROR_MESSAGE,
                BaaSConstants.EMAIL_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_EMAIL_ID] = apiParams.emailId!!
            RequestData(resultMap, true)
        }
    }

    private fun validateUpdateUserMobileNumber(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.mobileNumber.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.MOBILE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.MOBILE_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap.put(BaaSConstants.BS_KEY_MOBILE_NUMBER, apiParams.mobileNumber!!)
            RequestData(resultMap, true)
        }
    }

    private fun validateUpdateUserAddress(): RequestData {
        return when {
            apiDetails.apiParams.addressLine1.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.ADDRESS_1_EMPTY_ERROR_MESSAGE,
                BaaSConstants.ADDRESS_1_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.addressLine2.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.ADDRESS_2_EMPTY_ERROR_MESSAGE,
                BaaSConstants.ADDRESS_2_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.city.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.CITY_EMPTY_ERROR_MESSAGE,
                BaaSConstants.CITY_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.pinCode.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.PIN_CODE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PIN_CODE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.stateId.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.STATE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.STATE_EMPTY_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_1] = apiDetails.apiParams.addressLine1!!
                resultMap[BaaSConstants.BS_KEY_ADDRESS_LINE_2] = apiDetails.apiParams.addressLine2!!
                resultMap[BaaSConstants.BS_KEY_CITY] = apiDetails.apiParams.city!!
                resultMap[BaaSConstants.BS_KEY_PIN_CODE] = apiDetails.apiParams.pinCode!!
                resultMap[BaaSConstants.BS_KEY_STATE_ID] = apiDetails.apiParams.stateId!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetTransactionListData(): RequestData {
        return when {
            /*apiDetails.apiParams.accountType.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.NEW_ACCOUNT_TYPE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.NEW_ACCOUNT_TYPE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.debitIndicator.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.NEW_DEBIT_INDICATOR_EMPTY_ERROR_MESSAGE,
                BaaSConstants.NEW_DEBIT_INDICATOR_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.startDate.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.START_DATE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.START_DATE_EMPTY_ERROR_CODE
            )
            apiDetails.apiParams.endDate.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.END_DATE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.END_DATE_EMPTY_ERROR_CODE
            )*/
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_START_DATE] = apiDetails.apiParams.startDate!!
                resultMap[BaaSConstants.BS_KEY_END_DATE] = apiDetails.apiParams.endDate!!
                resultMap[BaaSConstants.BS_KEY_ACCOUNT_TYPE] = apiDetails.apiParams.accountType!!
                resultMap[BaaSConstants.BS_KEY_DEBIT_INDICATOR] =
                    apiDetails.apiParams.debitIndicator!!
                resultMap[BaaSConstants.BS_KEY_PAGE] =
                    apiDetails.apiParams.page
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateGetTransactionDetailData(): RequestData {
        return when {
            apiDetails.apiParams.id.isNullOrEmpty() -> sendErrorResponse(
                BaaSConstants.TRANSACTION_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.TRANSACTION_ID_ERROR_CODE
            )
            else -> {
                val resultMap = HashMap<String, Any>()
                resultMap[BaaSConstants.BS_KEY_ID] = apiDetails.apiParams.id!!
                RequestData(resultMap, true)
            }
        }
    }

    private fun validateKYCLocation(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.latitude.isNullOrEmpty() || apiParams.longitude.isNullOrEmpty()
            || apiParams.ipAddress.isNullOrEmpty() || apiParams.state.isNullOrEmpty()
            || apiParams.country.isNullOrEmpty()
        )
            sendErrorResponse(
                BaaSConstants.ADDRESS_EMPTY_ERROR_MESSAGE,
                BaaSConstants.ADDRESS_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_LATITUDE] = apiParams.latitude!!
            resultMap[BaaSConstants.BS_KEY_LONGITUDE] = apiParams.longitude!!
            resultMap[BaaSConstants.BS_KEY_IP_ADDRESS] = apiParams.ipAddress!!
            resultMap[BaaSConstants.BS_KEY_STATE] = apiParams.state!!
            resultMap[BaaSConstants.BS_KEY_COUNTRY] = apiParams.country!!
            RequestData(resultMap, true)
        }
    }

    private fun validateKarzaSessionData(): RequestData {
        val apiParams = apiDetails.apiParams
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_PRODUCT_ID] = apiParams.productId!!
        return RequestData(resultMap, true)
    }

    private fun validateKarzaNewCustomerData(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.applicantFormData?.get("applicationId").toString().isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.APPLICATION_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.APPLICATION_ID_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_KARZA_APPLICATION_FORM_DATA] =
                apiParams.applicantFormData!!
            resultMap[BaaSConstants.BS_KEY_KARZA_PAN_DATA] = apiParams.panData!!
//            Logger.getLogger(BaaSConstants.LOG_TAG, "applicantFormData : " + resultMap.toString())
            RequestData(resultMap, true)
        }
    }

    private fun validateKarzaGenerateUserTokenData(): RequestData {
        val apiParams = apiDetails.apiParams
        val resultMap = HashMap<String, Any>()
        resultMap[BaaSConstants.BS_KEY_KARZA_TRANSACTION_ID] = apiParams.transactionId!!
        return RequestData(resultMap, true)
    }

    private fun validateApplicationId(): RequestData {
        val resultMap = HashMap<String, Any>()
        return RequestData(resultMap, true)
    }

    private fun validateKYCAadhar(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.xmlFileString.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.AADHAR_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.xmlFileCode.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.AADHAR_FILE_CODE_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_XML] = apiParams.xmlFileString!!
            resultMap[BaaSConstants.BS_KEY_XML_FILE_CODE] = apiParams.xmlFileCode!!
            RequestData(resultMap, true)
        }
    }

    private fun validateKYCSelfie(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.live_photo == null)
            sendErrorResponse(
                BaaSConstants.PHOTO_EMPTY_ERROR_MESSAGE,
                BaaSConstants.PHOTO_EMPTY_ERROR_CODE
            ) else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_LIVE_PHOTO] = apiParams.live_photo!!
            resultMap[BaaSConstants.BS_KEY_KARZA_SELFIE_NAME] = apiParams.karza_photo_name!!
            RequestData(resultMap, true)
        }
    }

    private fun validateAddKYCResults(): RequestData {
        val apiParams = apiDetails.apiParams
        return if (apiParams.maskedAadhaar.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.MASKED_AADHAR_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.requestId.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.REQUEST_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.applicationId.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.APPLICATION_ID_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.createTimeStamp.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.TIME_STAMP_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.currentAddress.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.CURRENT_ADDRESS_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.permanentAddress.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.PERMANENT_ADDRESS_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.gender.isNullOrEmpty())
            sendErrorResponse(
                BaaSConstants.GENDER_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.faceAadhaarXml == null)
            sendErrorResponse(
                BaaSConstants.FACE_AADHAR_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else if (apiParams.userNameData == null)
            sendErrorResponse(
                BaaSConstants.USER_DATA_AADHAR_EMPTY_ERROR_MESSAGE,
                BaaSConstants.AADHAR_EMPTY_ERROR_CODE
            )
        else {
            val resultMap = HashMap<String, Any>()
            resultMap[BaaSConstants.BS_KEY_PAN_NUMBER] = apiParams.panNumber!!
            resultMap[BaaSConstants.BS_KEY_MASKED_AADHAAR] = apiParams.maskedAadhaar!!
            resultMap[BaaSConstants.BS_KEY_REQUEST_ID] = apiParams.requestId!!
            resultMap[BaaSConstants.BS_KEY_APPLICATION_ID] = apiParams.applicationId!!
            resultMap[BaaSConstants.BS_KEY_PAN_VERIFIED] = apiParams.panVerified!!
            resultMap[BaaSConstants.BS_KEY_AADHAAR_VERIFIED] = apiParams.isAadhaarVerified!!
            resultMap[BaaSConstants.BS_KEY_XML_DATE_VERIFIED] = apiParams.xmlDateVerified!!
            resultMap[BaaSConstants.BS_KEY_LIVENESS_VERIFIED] = apiParams.livenessVerified!!
            resultMap[BaaSConstants.BS_KEY_CREATED_TIME_STAMP] = apiParams.createTimeStamp!!
            resultMap[BaaSConstants.BS_KEY_FACE_AADHAAR_XML] = apiParams.faceAadhaarXml!!
            resultMap[BaaSConstants.BS_KEY_CURRENT_ADDRESS] = apiParams.currentAddress!!
            resultMap[BaaSConstants.BS_KEY_PERMANENT_ADDRESS] = apiParams.permanentAddress!!
            resultMap[BaaSConstants.BS_KEY_USER_NAME_DATA] = apiParams.userNameData!!
            resultMap[BaaSConstants.BS_KEY_DOB] = apiParams.dob!!
            resultMap[BaaSConstants.BS_KEY_GENDER] = apiParams.gender!!
//            Logger.getLogger("addKYC",JsonUtils.toString(resultMap))
            RequestData(resultMap, true)

        }
    }

    private fun validateVerifyKYCResults(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun validateGetAddress(): RequestData {
        return RequestData(HashMap<String, Any>(), true)
    }

    private fun sendErrorResponse(errorMessage: String, errorCode: Int): RequestData {
        return RequestData(null, false, errorMessage, errorCode)
    }
    private fun validateZendeskCredentials(): RequestData {
        val resultMap = HashMap<String, Any>()
        return RequestData(resultMap, true)
    }
}