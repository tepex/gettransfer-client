package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Account(
    val user: User,
    var locale: Locale,
    var currency: Currency,
    var distanceUnit: DistanceUnit,
    var groups: List<String>,
    var carrierId: Long?
) {

    companion object {
        const val GROUP_CARRIER_DRIVER = "carrier/driver"
    }
}
