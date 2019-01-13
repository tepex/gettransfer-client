package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.ProfileCached
import com.kg.gettransfer.data.model.ProfileEntity

open class ProfileEntityMapper : EntityMapper<ProfileCached, ProfileEntity> {

    override fun fromCached(type: ProfileCached) =
            ProfileEntity(
                    fullName = type.fullName,
                    email = type.email,
                    phone = type.phone
            )

    override fun toCached(type: ProfileEntity) =
            ProfileCached(
                    fullName = type.fullName,
                    email = type.email,
                    phone = type.phone
            )

}