package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit

import java.util.Locale

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
    var repeatedPassword: String? = null,
    val partner: PartnerEntity? = null
) {

    companion object {
        const val ENTITY_NAME   = "account"
        const val LOCALE        = "locale"
        const val CURRENCY      = "currency"
        const val DISTANCE_UNIT = "distance_unit"
        const val GROUPS        = "groups"
        const val CARRIER_ID    = "carrier_id"
        const val PARTNER       = "partner"

        const val PASSWORD              = "password"
        const val PASSWORD_CONFIRMATION = "password_confirmation"
    }
}

fun AccountEntity.map(configs: Configs) =
    Account(
        user.map(),
        configs.availableLocales.find { it.language == locale }?.let {
            Locale(it.language, Locale.getDefault().country)
        } ?: Locale.getDefault(),
        configs.supportedCurrencies.find { it.code == currency } ?: Currency.DEFAULT,
        distanceUnit?.let { DistanceUnit.valueOf(it.toUpperCase()) } ?: DistanceUnit.KM,
        groups ?: emptyList(),
        carrierId,
        partner?.map()
    )

fun Account.map() =
    AccountEntity(user.map(), locale.language, currency.code, distanceUnit.name.toLowerCase(), groups, carrierId)
