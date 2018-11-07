package com.kg.gettransfer.data.model

/**
 * Representation for a [AccountEntity] fetched from an external layer data source.
 */
data class AccountEntity(val user: UserEntity,
                         val locale: String?,
                         val currency: String?,
                         val distanceUnit: String?,
                         val groups: Array<String>?,
                         val carrierId: Long?) {
    companion object {
        const val PASSWORD       = "password"
        const val LOCALE         = "locale"
        const val CURRENCY       = "currency"
        const val DISTANCE_UNIT  = "distance_unit"
        const val GROUPS         = "groups"
        const val TERMS_ACCEPTED = "terms_accepted"
        const val CARRIER_ID     = "carrier_id"
        
        val NO_ACCOUNT = AccountEntity(UserEntity(ProfileEntity(null, null, null)), null, null, null, null, null)
    }
}
