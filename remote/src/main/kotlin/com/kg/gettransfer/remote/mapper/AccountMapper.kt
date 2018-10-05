package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.remote.model.AccountModel

/**
 * Map a [AccountModel] from an [AccountEntity] instance when data is moving between this later and the Data layer.
 */
open class AccountMapper(): EntityMapper<AccountModel, AccountEntity> {
    override fun fromRemote(type: AccountModel) =
        AccountEntity(UserEntity(type.fullName, type.email, type.phone, type.termsAccepted),
                      type.locale,
                      type.currency,
                      type.distanceUnit,
                      type.groups,
                      type.carrierId)
    
    override fun toRemote(type: AccountEntity) = 
        AccountModel(type.user.email,
                     type.user.phone,
                     type.locale,
                     type.currency,
                     type.distanceUnit,
                     type.user.name,
                     type.groups,
                     type.user.termsAccepted,
                     type.carrierId)
}
