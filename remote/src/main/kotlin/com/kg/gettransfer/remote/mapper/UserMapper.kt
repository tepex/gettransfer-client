package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.remote.model.UserModel

/**
 * Map a [UserEntity] to and from a [UserModel] instance when data is moving between this later and the Data layer.
 */
open class UserMapper : EntityMapper<UserModel, UserEntity> {

    /**
     * Map a [UserModel] instance to a [UserEntity] instance.
     */
    override fun fromRemote(type: UserModel): UserEntity { throw UnsupportedOperationException() }

    /**
     * Map a [UserEntity] instance to a [UserModel] instance.
     */
    override fun toRemote(type: UserEntity) =
        UserModel(
            fullName = type.profile.fullName!!,
            email = type.profile.email,
            phone = type.profile.phone,
            termsAccepted = type.termsAccepted
        )
}
