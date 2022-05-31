package com.payu.baas.core.view.callback

interface SmsListener {
    fun messageReceived(messageText: String?)

}