package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Account(val user: User,
                   var locale: Locale?,
                   var currency: Currency?,
                    /* Only for Tepex: Server protocol problem */
                   var distanceUnit: DistanceUnit?,
                   var groups: List<String>?,
                   var carrierId: Long?) {
    companion object {
        val NO_ACCOUNT = Account(User(Profile(null, null, null)), null, null, null, null, null)
        @JvmField val GROUP_CARRIER_DRIVER = "carrier/driver"
    }
}
