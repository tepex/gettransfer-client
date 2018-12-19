package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.presentation.model.ProfileModel

open class ProfileMapper : Mapper<ProfileModel, Profile> {
    override fun toView(type: Profile) =
        ProfileModel(
            name  = type.fullName,
            email = type.email,
            phone = type.phone
        )

    override fun fromView(type: ProfileModel) =
        Profile(
            fullName = type.name,
            email    = type.email,
            phone    = type.phone
        )
}
