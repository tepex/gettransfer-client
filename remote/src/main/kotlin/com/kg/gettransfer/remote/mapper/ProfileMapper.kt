package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ProfileEntity

import com.kg.gettransfer.remote.model.ProfileModel

/**
 * Map a [ProfileModel] from an [ProfileEntity] instance when data is moving between this later and the Data layer.
 */
open class ProfileMapper : EntityMapper<ProfileModel, ProfileEntity> {
    override fun fromRemote(type: ProfileModel) =
        ProfileEntity(
            fullName = type.fullName,
            email = type.email,
            phone = type.phone
        )

    override fun toRemote(type: ProfileEntity) =
        ProfileModel(
            fullName = type.fullName,
            email = type.email,
            phone = type.phone
        )
}
