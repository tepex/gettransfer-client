package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

/**
 * Map a [AccountEntity] to and from a [Account] instance when data is moving between
 * this later and the Domain layer
 */
open class AccountMapper(): Mapper<AccountEntity, Account> {
    internal lateinit var configs: Configs
    
    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: AccountEntity): Account {
        if(type == AccountEntity.NO_ACCOUNT) return Account.NO_ACCOUNT
        return Account(type.email,
                       type.phone,
                       configs.availableLocales.find { it.language == type.locale }!!,
                       configs.supportedCurrencies.find { it.currencyCode == type.currency }!!,
                       DistanceUnit.parse(type.distanceUnit),
                       type.fullName,
                       type.groups,
                       type.termsAccepted)
    }

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) = 
        AccountEntity(type.email,
                      type.phone,
                      type.locale?.language,
                      type.currency?.currencyCode,
                      type.distanceUnit?.name,
                      type.fullName,
                      type.groups,
                      type.termsAccepted)
}
