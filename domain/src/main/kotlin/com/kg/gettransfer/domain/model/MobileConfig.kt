package com.kg.gettransfer.domain.model

data class MobileConfig(
    val pushShowDelay: Int,
    val orderMinimumMinutes: Int,
    val termsUrl: String,
    val smsResendDelaySec: Int,
    val buildsConfigs: Map<String, BuildsConfigs>?
) {

    companion object {
        const val FROM_REMOTE = true

        const val SMS_RESEND_DELAY_SEC_DEFAULT = 90
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean?
)
