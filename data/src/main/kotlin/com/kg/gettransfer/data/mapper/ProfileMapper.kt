package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ProfileEntity

import com.kg.gettransfer.domain.model.Entity
import com.kg.gettransfer.domain.model.Profile

/**
 * Map a [ProfileEntity] to and from a [Profile] instance when data is moving between
 * this later and the Domain layer.
 */
open class ProfileMapper : Mapper<ProfileEntity, Profile> {
    /**
     * Map a [ProfileEntity] instance to a [Profile] instance.
     */
    override fun fromEntity(type: ProfileEntity) =
        Profile(
            fullName = type.fullName,
            email = type.email,
            phone = type.phone
        )

    /**
     * Map a [Profile] instance to a [ProfileEntity] instance.
     */
    override fun toEntity(type: Profile) =
        ProfileEntity(
            fullName = type.fullName,
            email = type.email,
            phone = type.phone
        )
}
