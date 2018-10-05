package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.domain.model.User

/**
 * Map a [UserEntity] to and from a [User] instance when data is moving between
 * this later and the Domain layer.
 */
open class UserMapper(): Mapper<UserEntity, User> {
    /**
     * Map a [UserEntity] instance to a [User] instance.
     */
    override fun fromEntity(type: UserEntity) = User(type.name, type.email, type.phone, type.termsAccepted)
    /**
     * Map a [User] instance to a [UserEntity] instance.
     */
    override fun toEntity(type: User) = UserEntity(type.name, type.email, type.phone, type.termsAccepted)
}
