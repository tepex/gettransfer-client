package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit

/**
 * Map a [AccountEntity] to and from a [Account] instance when data is moving between
 * this later and the Domain layer
 */
open class AccountMapper(): Mapper<AccountEntity, Account> {
    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: AccountEntity) = 
        Account(type.email,
                type.phone,
                type.locale,
                type.currency,
                DistanceUnit.parse(type.distanceUnit),
                type.fullName,
                type.groups,
                type.termsAccepted)

    /**
     * Map a [Account] instance to a [AccountEntity] instance
     */
    override fun toEntity(type: Account) = 
        AccountEntity(type.email,
                      type.phone,
                      type.locale,
                      type.currency,
                      type.distanceUnit.name,
                      type.fullName,
                      type.groups,
                      type.termsAccepted)
}
