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

        val EMPTY = MobileConfig(
            pushShowDelay = 5,
            orderMinimumMinutes = 120,
            termsUrl = "terms_of_use",
            smsResendDelaySec = 90,
            buildsConfigs = null
        )
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean?
)
