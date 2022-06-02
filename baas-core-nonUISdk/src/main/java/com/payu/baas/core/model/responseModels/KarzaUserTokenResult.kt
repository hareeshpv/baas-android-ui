package com.payu.baas.core.model.responseModels

class KarzaUserTokenResult {
    var data //user data in inner userToken field
            : KarzaUserData? = null
    var success = false
    var reason: String? = null
}