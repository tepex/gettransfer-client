package com.kg.gettransfer.data.model

data class MobileConfigEntity(
    val pushShowDelay: Int,
    val orderMinimumMinutes: Int,
    val termsUrl: String
) {

    companion object {
        const val INTERNAL_PUSH_SHOW_DELAY = "internal_push_show_delay"
        const val ORDER_MINIMUM_MINUTES    = "order_minimum_minutes"
        const val LICENSE_URL              = "license_url"
    }
}
