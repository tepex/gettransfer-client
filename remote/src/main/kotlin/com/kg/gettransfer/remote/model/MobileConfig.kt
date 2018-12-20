package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MobileConfig(@SerializedName("internal_push_show_delay") @Expose val pushShowDelay: Int,
                        @SerializedName("order_minimum_minutes") @Expose val orderMinimumMinutes: Int,
                        @SerializedName("license_url") @Expose val termsOfUseUrl: String)