package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.cache.model.StringList

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

/**
 * Map a [AccountCached] from/to an [AccountEntity] instance when data is moving between this later and the Data layer.
 */
open class AccountEntityMapper : EntityMapper<AccountCached, AccountEntity> {
    override fun fromCached(type: AccountCached) =
        AccountEntity(
            UserEntity(ProfileEntity(type.fullName, type.email, type.phone), type.termsAccepted ?: false),
            type.locale,
            type.currency,
            type.distanceUnit,
            type.groups?.list,
            type.carrierId
        )

    override fun toCached(type: AccountEntity) =
        AccountCached(
            type.user.profile.fullName,
            type.user.profile.email,
            type.user.profile.phone,
            type.locale,
            type.currency,
            type.distanceUnit,
            type.groups?.let { StringList(it) },
            type.user.termsAccepted,
            type.carrierId
        )
}
