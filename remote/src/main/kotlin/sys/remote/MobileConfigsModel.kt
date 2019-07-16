package com.kg.gettransfer.sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.sys.data.BuildsConfigsEntity
import com.kg.gettransfer.sys.data.MobileConfigsEntity

data class MobileConfigsModel(
    @SerializedName(MobileConfigsEntity.INTERNAL_PUSH_SHOW_DELAY) @Expose val pushShowDelay: Int,
    @SerializedName(MobileConfigsEntity.ORDER_MINIMUM_MINUTES) @Expose val orderMinimumMinutes: Int,
    @SerializedName(MobileConfigsEntity.LICENSE_URL) @Expose val termsOfUseUrl: String,
    @SerializedName(MobileConfigsEntity.SMS_RESEND_DELAY_SEC) @Expose val smsResendDelaySec: Int?,
    @SerializedName(MobileConfigsEntity.DRIVER_APP_NOTIFY) @Expose val driverAppNotify: Boolean?,
    @SerializedName(MobileConfigsEntity.DRIVER_MODE_BLOCK) @Expose val driverModeBlock: Boolean?,
    @SerializedName(MobileConfigsEntity.BUILDS_CONFIGS) @Expose val buildsConfigs: Map<String, BuildsConfigsModel>?
)

data class BuildsConfigsModel(
    @SerializedName(BuildsConfigsEntity.UPDATE_REQUIRED) @Expose val updateRequired: Boolean?
)

fun MobileConfigsModel.map() =
    MobileConfigsEntity(
        pushShowDelay,
        orderMinimumMinutes,
        termsOfUseUrl,
        smsResendDelaySec,
        driverAppNotify ?: false,
        driverModeBlock ?: false,
        buildsConfigs?.mapValues { it.value.map() }
    )

fun BuildsConfigsModel.map() = BuildsConfigsEntity(updateRequired ?: false)
