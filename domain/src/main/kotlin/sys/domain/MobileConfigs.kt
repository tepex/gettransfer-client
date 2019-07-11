package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.Hour
import com.kg.gettransfer.core.domain.Minute
import com.kg.gettransfer.core.domain.Second

data class MobileConfigs(
    /*
    val pushShowDelay: Int,*/
    val orderMinimum: Minute,
    val termsUrl: String,
    val smsResendDelay: Second,
    val driverAppNotify: Boolean,
    val driverModeBlock: Boolean,
    val buildsConfigs: Map<String, BuildsConfigs>
) {

    companion object {
        val SMS_RESEND_DELAY_SEC_DEFAULT = Second(90)
        val DRIVER_APP_NOTIFY_DEFAULT = false
        val DRIVER_MODE_BLOCK_DEFAULT = false

        val EMPTY = MobileConfigs(
            /*
            pushShowDelay = 5,*/
            orderMinimum = Hour(2).minutes,
            termsUrl = "terms_of_use",
            smsResendDelay = SMS_RESEND_DELAY_SEC_DEFAULT,
            driverAppNotify = DRIVER_APP_NOTIFY_DEFAULT,
            driverModeBlock = DRIVER_MODE_BLOCK_DEFAULT,
            buildsConfigs = emptyMap()
        )
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean
)
