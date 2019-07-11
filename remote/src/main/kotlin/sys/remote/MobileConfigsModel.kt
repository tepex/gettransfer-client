package com.kg.gettransfer.sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.sys.data.BuildsConfigsEntity
import com.kg.gettransfer.sys.data.MobileConfigsEntity

data class MobileConfigsModel(
    @SerializedName(MobileConfigEntity.INTERNAL_PUSH_SHOW_DELAY) @Expose val pushShowDelay: Int,
    @SerializedName(MobileConfigEntity.ORDER_MINIMUM_MINUTES) @Expose val orderMinimumMinutes: Int,
    @SerializedName(MobileConfigEntity.LICENSE_URL) @Expose val termsOfUseUrl: String,
    @SerializedName(MobileConfigEntity.SMS_RESEND_DELAY_SEC) @Expose val smsResendDelaySec: Int?,
    @SerializedName(MobileConfigEntity.DRIVER_APP_NOTIFY) @Expose val driverAppNotify: Boolean?,
    @SerializedName(MobileConfigEntity.DRIVER_MODE_BLOCK) @Expose val driverModeBlock: Boolean?,
    @SerializedName(MobileConfigEntity.BUILDS_CONFIGS) @Expose val buildsConfigs: Map<String, BuildsConfigsModel>?
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
