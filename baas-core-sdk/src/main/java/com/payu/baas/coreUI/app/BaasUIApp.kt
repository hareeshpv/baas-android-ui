package com.payu.baas.coreUI.app

import android.app.Application
import android.content.Context

class BaasUIApp : Application() {

    companion object {
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        com.payu.baas.coreUI.app.BaasUIApp.Companion.ctx = this
    }
}