package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.BuildsConfigsEntity

data class MobileConfigModel(
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

fun MobileConfigModel.map() =
    MobileConfigEntity(
        pushShowDelay,
        orderMinimumMinutes,
        termsOfUseUrl,
        smsResendDelaySec,
        driverAppNotify,
        driverModeBlock,
        buildsConfigs?.mapValues { it.value.map() }
    )

fun BuildsConfigsModel.map() = BuildsConfigsEntity(updateRequired ?: false)
