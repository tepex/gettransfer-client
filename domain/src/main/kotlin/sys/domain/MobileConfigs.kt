package com.kg.gettransfer.sys.domain


import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

@ExperimentalTime
data class MobileConfigs(
    /*
    val pushShowDelay: Int,*/
    val orderMinimumMinutes: Duration,
    val termsUrl: String,
    val smsResendDelaySec: Duration,
    val isDriverAppNotify: Boolean,
    val isDriverModeBlock: Boolean,
    val buildsConfigs: Map<String, BuildsConfigs>
) {

    companion object {
        val SMS_RESEND_DELAY_SEC_DEFAULT = 90.seconds

        val EMPTY = MobileConfigs(
            /*
            pushShowDelay = 5,*/
            orderMinimumMinutes = 120.minutes,
            termsUrl = "terms_of_use",
            smsResendDelaySec = SMS_RESEND_DELAY_SEC_DEFAULT,
            isDriverAppNotify = false,
            isDriverModeBlock = false,
            buildsConfigs = emptyMap()
        )
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean
)
