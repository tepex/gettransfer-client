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
        val NO_ACCOUNT = AccountEntity(UserEntity(ProfileEntity(null, null, null)), null, null, null, null, null)
    }
}
