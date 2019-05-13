package com.kg.gettransfer.domain.model

data class MobileConfig(
    val pushShowDelay: Int,
    val orderMinimumMinutes: Int,
    val termsUrl: String,
    val smsResendDelaySec: Int,
    val buildsConfigs: Map<String, BuildsConfigs>?,
    var isUpdated: Boolean = false // field if need to check whether config was loaded or is hardcoded one
) {

    companion object {
        const val FROM_REMOTE = true
    }
}

data class BuildsConfigs(
    val updateRequired: Boolean?
)
