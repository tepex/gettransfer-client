package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.remote.model.AccountModel

/**
 * Map a [AccountModel] from an [AccountEntity] instance when data is moving between this later and the Data layer.
 */
open class AccountMapper(): EntityMapper<AccountModel, AccountEntity> {
    override fun fromRemote(type: AccountModel) =
        AccountEntity(UserEntity(ProfileEntity(0L, type.fullName, type.email, type.phone), type.termsAccepted),
                      type.locale,
                      type.currency,
                      type.distanceUnit,
                      type.groups,
                      type.carrierId)

    override fun toRemote(type: AccountEntity) =
        AccountModel(type.user.profile.fullName,
                     type.user.profile.email,
                     type.user.profile.phone,
                     type.locale,
                     type.currency,
                     type.distanceUnit,
                     type.groups,
                     type.user.termsAccepted,
                     type.carrierId)
}
