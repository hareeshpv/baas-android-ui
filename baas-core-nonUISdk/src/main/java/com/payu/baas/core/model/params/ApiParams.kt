package com.payu.baas.core.model.params

import com.google.gson.JsonArray
import com.payu.baas.core.model.model.FaceAadharXMLModel
import com.payu.baas.core.model.model.UserNameAadharXMLModel
import org.json.JSONArray
import org.json.JSONObject

open class ApiParams {
    var txnAmount:String?=null
    var code: String? = null
    var mobile: String? = null
    var newPassword: String? = null
    var oldPassword: String? = null
    var username: String? = null
    var password: String? = null
    var title: String? = null
    var panNumber: String? = null
    var pan: String? = null
    var employeeId: String? = null
    var id: String? = null
    var accountType: String? = null
    var debitIndicator: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var emailId: String? = null
    var address: String? = null
    var mobileNumber: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var gender: String? = null
    var dob: String? = null
    var email: String? = null
    var country: String? = null
    var countryCode: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var address1: String? = null
    var address2: String? = null
    var city: String? = null
    var pinCode: String? = null
    var state: String? = null
    var stateId: String? = null
    var beneficiaryName: String? = null
    var accountNumber: String? = null
    var ifsc: String? = null
    var verified: String? = null
    var status: String? = null
    var beneficiaryId: String? = null
    var userBeneFiciaryIds: JSONArray? = null
    var currency: String? = null
    var paymentType: String? = null
    var remarks: String? = null
    var accountId: String? = null
    var amount: Double? = null
    var creditorDetails: Any? = null
    var name: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var ipAddress: String? = null
    var xmlFileString: String? = null
    var xmlFileCode: String? = null
    var live_photo: Any? = null
    var karza_photo_name: String? = null
    var page: Int = 0
    var size: Int = 10
    var sort: String = "beneficiaryName"
    var redirect_link:String?=null
    var reason:String?=null
    var txn_mode:String?=null
    var allow:String?=null
    var channel:String?=null
    var location:String?=null
    var amount_limit:String?=null
    var target_duration:String?=null
    var usage_limit:String?=null
    var pin_status:Boolean?=null
    var transactionModes: JSONObject?=null
    var transactionLimits:JSONArray?=null
    var productId:JSONArray?=null
    var applicantFormData:JSONObject?=null
    var panData:Any?=null
    var transactionId:String?=null
    var maskedAadhaar:String?=null
    var requestId:String?=null
    var applicationId:String?=null
    var createTimeStamp:String?=null
    var panVerified:Boolean?=null
    var livenessVerified:Boolean?=null
    var xmlDateVerified:Boolean?=null
    var isAadhaarVerified:Boolean?=null
    var faceAadhaarXml:JSONObject?=null
    var currentAddress:String?=null
    var permanentAddress:String?=null
    var userNameData:JSONObject?=null
    var limitConfigs:JSONArray?=null
    var isCardReceived:Boolean?=null



}