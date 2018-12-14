package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import java.util.Currency
import java.util.Locale

import org.koin.standalone.get

/**
 * Map a [AccountEntity] to and from a [Account] instance when data is moving between
 * this later and the Domain layer
 */
open class AccountMapper : Mapper<AccountEntity, Account> {
    private val userMapper = get<UserMapper>()
    internal lateinit var configs: Configs

    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: AccountEntity) =
        Account(
            userMapper.fromEntity(type.user),
            configs.availableLocales.find { it.language == type.locale } ?: Locale.getDefault(),
            configs.supportedCurrencies.find { it.currencyCode == type.currency } ?: Currency.getInstance("USD"),
            type.distanceUnit?.let { DistanceUnit.valueOf(it) } ?: DistanceUnit.km,
            type.groups ?: emptyList<String>(),
            type.carrierId
        )

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) =
        AccountEntity(
            userMapper.toEntity(type.user),
            type.locale.language,
            type.currency.currencyCode,
            type.distanceUnit.name,
            type.groups,
            type.carrierId
        )
}
