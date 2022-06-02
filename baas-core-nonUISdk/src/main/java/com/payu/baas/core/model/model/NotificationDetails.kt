package com.payu.baas.core.model.model

import com.google.gson.annotations.SerializedName

class NotificationDetails {
    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("notificationText")
    var notificationText: String? = null

    @SerializedName("ctaURL")
    var ctaURL: String? = null

}