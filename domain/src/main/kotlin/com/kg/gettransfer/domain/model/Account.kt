package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Account(var email: String?,
                   var phone: String?,
                   var locale: Locale?,
                   var currency: Currency?,
                   var distanceUnit: DistanceUnit,
                   var fullName: String?,
                   var groups: Array<String>?,
                   var termsAccepted: Boolean = true) {
    fun isLoggedIn() = email != null
}
