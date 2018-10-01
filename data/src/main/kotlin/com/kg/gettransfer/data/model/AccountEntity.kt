package com.kg.gettransfer.data.model

/**
 * Representation for a [AccountEntity] fetched from an external layer data source
 */
data class AccountEntity(val email: String?,
                         val phone: String?,
                         val locale: String?,
                         val currency: String?,
                         val distanceUnit: String?,
                         val fullName: String?,
                         val groups: Array<String>?,
                         val termsAccepted: Boolean = true) {
    companion object {
        val NO_ACCOUNT = AccountEntity(null, null, null, null, null, null, null)
    }
}
