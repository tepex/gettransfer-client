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
            user = userMapper.fromEntity(type.user),
            locale = configs.availableLocales.find { it.language == type.locale } ?: Locale.getDefault(),
            currency = configs.supportedCurrencies.find { it.currencyCode == type.currency } ?: Currency.getInstance("USD"),
            distanceUnit = type.distanceUnit?.let { DistanceUnit.valueOf(it) } ?: DistanceUnit.km,
            groups = type.groups ?: emptyList<String>(),
            carrierId = type.carrierId
        )

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) =
        AccountEntity(
            user = userMapper.toEntity(type.user),
            locale = type.locale.language,
            currency = type.currency.currencyCode,
            distanceUnit = type.distanceUnit.name,
            groups = type.groups,
            carrierId = type.carrierId
        )
}
