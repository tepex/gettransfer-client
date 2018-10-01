package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.remote.model.AccountModel

/**
 * Map a [AccountModel] from an [AccountEntity] instance when data is moving between this later and the Data layer.
 */
open class AccountMapper(): EntityMapper<AccountModel, AccountEntity> {
    override fun fromRemote(type: AccountModel) =
        AccountEntity(type.email,
                      type.phone,
                      type.locale,
                      type.currency,
                      type.distanceUnit,
                      type.fullName,
                      type.groups,
                      type.termsAccepted)
    
    override fun toRemote(type: AccountEntity) = 
        AccountModel(type.email,
                     type.phone,
                     type.locale,
                     type.currency,
                     type.distanceUnit,
                     type.fullName,
                     type.groups,
                     type.termsAccepted)
}
