package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.domain.model.User

import org.koin.standalone.get

/**
 * Map a [UserEntity] to and from a [User] instance when data is moving between this later and the Domain layer.
 */
open class UserMapper : Mapper<UserEntity, User> {
    private val profileMapper = get<ProfileMapper>()

    /**
     * Map a [UserEntity] instance to a [User] instance.
     */
    override fun fromEntity(type: UserEntity) =
        User(
            profile = profileMapper.fromEntity(type.profile),
            termsAccepted = type.termsAccepted
        )

    /**
     * Map a [User] instance to a [UserEntity] instance.
     */
    override fun toEntity(type: User) =
        UserEntity(
            profile = profileMapper.toEntity(type.profile),
            termsAccepted = type.termsAccepted
        )
}
