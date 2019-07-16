package com.kg.gettransfer.data.model

import com.kg.gettransfer.core.domain.Minute
import com.kg.gettransfer.core.domain.Second

import com.kg.gettransfer.sys.domain.BuildsConfigs
import com.kg.gettransfer.sys.domain.MobileConfigs

data class MobileConfigEntity(
    val pushShowDelay: Int,
    val orderMinimumMinutes: Int,
    val termsUrl: String,
    val smsResendDelaySec: Int?,
    val buildsConfigs: Map<String, BuildsConfigsEntity>?
) {

    companion object {
        const val ENTITY_NAME              = "mobile_configs"
        const val INTERNAL_PUSH_SHOW_DELAY = "internal_push_show_delay"
        const val ORDER_MINIMUM_MINUTES    = "order_minimum_minutes"
        const val LICENSE_URL              = "license_url"
        const val SMS_RESEND_DELAY_SEC     = "sms_resend_delay_sec"
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

fun MobileConfigEntity.map() =
    MobileConfigs(
        /*
        pushShowDelay,*/
        Minute(orderMinimumMinutes),
        termsUrl,
        smsResendDelaySec?.let { Second(it) } ?: MobileConfigs.SMS_RESEND_DELAY_SEC_DEFAULT,
        buildsConfigs?.mapValues { it.value.map() } ?: emptyMap()
    )
