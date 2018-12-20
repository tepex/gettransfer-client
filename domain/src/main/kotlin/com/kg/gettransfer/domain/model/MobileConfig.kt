package com.kg.gettransfer.domain.model

data class MobileConfig(val pushShowDelay: Int,
                        val orderMinimumMinutes: Int,
                        val termsUrl: String,
                        var isUpdated: Boolean = false) // field if need to check whether config was loaded or is hardcoded one
{

    companion object {
        val LAST_KNOWN_CONFIGS = MobileConfig(5, 120, "terms_of_use")

        const val FROM_REMOTE = true
    }
}