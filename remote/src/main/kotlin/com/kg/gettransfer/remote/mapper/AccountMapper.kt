package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.remote.model.AccountModel

/**
 * Map a [AccountModel] from an [AccountEntity] instance when data is moving between this later and the Data layer.
 */
open class AccountMapper : EntityMapper<AccountModel, AccountEntity> {
    override fun fromRemote(type: AccountModel) =
        AccountEntity(
            user = UserEntity(
                profile = ProfileEntity(
                    fullName = type.fullName,
                    email = type.email,
                    phone = type.phone
                ),
                termsAccepted = type.termsAccepted
            ),
            locale       = type.locale,
            currency     = type.currency,
            distanceUnit = type.distanceUnit,
            groups       = type.groups,
            carrierId    = type.carrierId
        )

    override fun toRemote(type: AccountEntity) =
        AccountModel(
            fullName      = type.user.profile.fullName,
            email         = type.user.profile.email,
            phone         = type.user.profile.phone,
            locale        = type.locale,
            currency      = type.currency,
            distanceUnit  = type.distanceUnit,
            groups        = type.groups,
            termsAccepted = type.user.termsAccepted,
            carrierId     = type.carrierId,

            password = type.password,
            repeatedPassword = type.repeatedPassword
        )
}
