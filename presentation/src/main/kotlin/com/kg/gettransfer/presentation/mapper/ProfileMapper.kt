package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.presentation.model.ProfileModel

open class ProfileMapper : Mapper<ProfileModel, Profile> {
    override fun toView(type: Profile) = ProfileModel(type.fullName, type.email, type.phone)
    override fun fromView(type: ProfileModel) = Profile(type.name, type.email, type.phone)
}
