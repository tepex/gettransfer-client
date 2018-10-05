package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

/**
 * Map a [AccountEntity] to and from a [Account] instance when data is moving between
 * this later and the Domain layer
 */
open class AccountMapper(private val userMapper: UserMapper): Mapper<AccountEntity, Account> {
    internal lateinit var configs: Configs
    
    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: AccountEntity): Account {
        if(type == AccountEntity.NO_ACCOUNT) return Account.NO_ACCOUNT
        return Account(userMapper.fromEntity(type.user),
                       configs.availableLocales.find { it.language == type.locale }!!,
                       configs.supportedCurrencies.find { it.currencyCode == type.currency }!!,
                       DistanceUnit.parse(type.distanceUnit),
                       type.groups,
                       type.carrierId)
    }

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) = 
        AccountEntity(userMapper.toEntity(type.user),
                      type.locale?.language,
                      type.currency?.currencyCode,
                      type.distanceUnit?.name,
                      type.groups,
                      type.carrierId)
}
