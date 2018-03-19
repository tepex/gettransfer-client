package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class ProfileInfo(
        @Expose
        @SerializedName("full_name")
        var fullName: String? = null,
        @Expose
        @SerializedName("email_notifications")
        var emailNotifications: Boolean? = null) {

    val valid: Boolean get() = emailNotifications != null
}
