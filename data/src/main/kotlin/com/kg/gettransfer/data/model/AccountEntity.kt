package com.kg.gettransfer.data.model

/**
 * Representation for a [AccountEntity] fetched from an external layer data source.
 */
data class AccountEntity(
    val user: UserEntity,
    val locale: String?,
    val currency: String?,
    val distanceUnit: String?,
    val groups: List<String>?,
    val carrierId: Long?,

    var password: String? = null,
    var repeatedPassword: String? = null
) {

    companion object {
        const val ENTITY_NAME   = "account"
        const val LOCALE        = "locale"
        const val CURRENCY      = "currency"
        const val DISTANCE_UNIT = "distance_unit"
        const val GROUPS        = "groups"
        const val CARRIER_ID    = "carrier_id"

        const val PASSWORD              = "password"
        const val PASSWORD_CONFIRMATION = "password_confirmation"
    }
}
