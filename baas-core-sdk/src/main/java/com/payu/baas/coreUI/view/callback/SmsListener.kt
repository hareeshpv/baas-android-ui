package com.payu.baas.coreUI.view.callback

interface SmsListener {
    fun messageReceived(messageText: String?)

}