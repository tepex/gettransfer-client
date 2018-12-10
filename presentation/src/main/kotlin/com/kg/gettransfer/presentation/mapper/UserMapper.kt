package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.User

import com.kg.gettransfer.presentation.model.UserModel

import org.koin.standalone.get

open class UserMapper : Mapper<UserModel, User> {
    private val profileMapper = get<ProfileMapper>()

    override fun toView(type: User) =
        UserModel(
            profileMapper.toView(type.profile),
            type.termsAccepted
        )

    override fun fromView(type: UserModel) =
        User(
            profileMapper.fromView(type.profile),
            type.termsAccepted
        )
}
