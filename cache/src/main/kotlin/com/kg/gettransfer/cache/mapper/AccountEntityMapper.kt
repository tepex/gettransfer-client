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
            user         = UserEntity(ProfileEntity(type.fullName, type.email, type.phone), type.termsAccepted ?: false),
            locale       = type.locale,
            currency     = type.currency,
            distanceUnit = type.distanceUnit,
            groups       = type.groups?.list,
            carrierId    = type.carrierId
        )

    override fun toCached(type: AccountEntity) =
        AccountCached(
            fullName      = type.user.profile.fullName,
            email         = type.user.profile.email,
            phone         = type.user.profile.phone,
            locale        = type.locale,
            currency      = type.currency,
            distanceUnit  = type.distanceUnit,
            groups        = type.groups?.let { StringList(it) },
            termsAccepted = type.user.termsAccepted,
            carrierId     = type.carrierId
        )
}
