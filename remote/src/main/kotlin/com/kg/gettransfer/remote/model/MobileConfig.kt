package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.MobileConfigEntity

data class MobileConfig(
    @SerializedName(MobileConfigEntity.INTERNAL_PUSH_SHOW_DELAY) @Expose val pushShowDelay: Int,
    @SerializedName(MobileConfigEntity.ORDER_MINIMUM_MINUTES) @Expose val orderMinimumMinutes: Int,
    @SerializedName(MobileConfigEntity.LICENSE_URL) @Expose val termsOfUseUrl: String
)
