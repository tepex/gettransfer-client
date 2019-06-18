package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.User

import java.util.Locale

import org.koin.standalone.get

/**
 * Map a [AccountEntity] to and from a [Account] instance when data is moving between
 * this later and the Domain layer
 */
open class AccountMapper : Mapper<AccountEntity, Account> {
    internal lateinit var configs: Configs

    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: AccountEntity) =
        Account(
            type.user.map(),
            configs.availableLocales.find { it.language == type.locale }
                ?.let { Locale(it.language, Locale.getDefault().country) } ?: Locale.getDefault(),
            configs.supportedCurrencies.find { it.code == type.currency } ?: Currency("USD", "\$"),
            type.distanceUnit?.let { DistanceUnit.valueOf(it) } ?: DistanceUnit.km,
            type.groups ?: emptyList<String>(),
            type.carrierId
        )

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) =
        AccountEntity(
            type.user.map(),
            type.locale.language,
            type.currency.code,
            type.distanceUnit.name,
            type.groups,
            type.carrierId
        )

    fun toEntityWithNewPassword(type: Account, pass: String, repeatedPass: String) =
        toEntity(type).apply {
            password = pass
            repeatedPassword = repeatedPass
        }
}
