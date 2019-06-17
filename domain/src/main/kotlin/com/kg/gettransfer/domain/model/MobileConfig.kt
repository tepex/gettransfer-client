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
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean?
)
