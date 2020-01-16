package com.kg.gettransfer.sys.data

import com.kg.gettransfer.sys.domain.BuildsConfigs
import com.kg.gettransfer.sys.domain.MobileConfigs

import kotlin.time.minutes
import kotlin.time.seconds

data class MobileConfigsEntity(
    val pushShowDelay: Int,
    val orderMinimumMinutes: Int,
    val termsUrl: String,
    val smsResendDelaySec: Int?,
    val isDriverAppNotify: Boolean,
    val isDriverModeBlock: Boolean,
    val buildsConfigs: Map<String, BuildsConfigsEntity>?
) {

    companion object {
        const val ENTITY_NAME              = "mobile_configs"
        const val INTERNAL_PUSH_SHOW_DELAY = "internal_push_show_delay"
        const val ORDER_MINIMUM_MINUTES    = "order_minimum_minutes"
        const val LICENSE_URL              = "license_url"
        const val SMS_RESEND_DELAY_SEC     = "sms_resend_delay_sec"
        const val DRIVER_APP_NOTIFY        = "driver_app_notify"
        const val DRIVER_MODE_BLOCK        = "driver_mode_block"
        const val BUILDS_CONFIGS           = "android"
    }
}

data class BuildsConfigsEntity(
    val updateRequired: Boolean
) {

    companion object {
        const val UPDATE_REQUIRED = "update_required"
    }
}

fun BuildsConfigsEntity.map() = BuildsConfigs(updateRequired)

fun MobileConfigsEntity.map() =
    MobileConfigs(
        /* Not used now
        pushShowDelay,*/
        orderMinimumMinutes.minutes,
        termsUrl,
        smsResendDelaySec?.seconds ?: MobileConfigs.SMS_RESEND_DELAY_SEC_DEFAULT,
        isDriverAppNotify,
        isDriverModeBlock,
        buildsConfigs?.mapValues { it.value.map() } ?: emptyMap()
    )
