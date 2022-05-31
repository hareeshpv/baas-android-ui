package com.payu.baas.core.app

import android.app.Application
import android.content.Context

class BaasUIApp : Application() {

    companion object {
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }
}