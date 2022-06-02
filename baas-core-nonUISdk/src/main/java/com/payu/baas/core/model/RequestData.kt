package com.payu.baas.core.model

class RequestData(val result: HashMap<String, Any>?, val isValid: Boolean, val errorMessage:String = "", val errorCode: Int = -1)