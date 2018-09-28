package com.kg.gettransfer.data.model

import java.util.Currency
import java.util.Locale

/**
 * Representation for a [AccountEntity] fetched from an external layer data source
 */
data class AccountEntity(val email: String?,
                         val phone: String?,
                         val locale: Locale?,
                         val currency: Currency?,
                         val distanceUnit: String?,
                         val fullName: String?,
                         val groups: Array<String>?,
                         val termsAccepted: Boolean = true)
