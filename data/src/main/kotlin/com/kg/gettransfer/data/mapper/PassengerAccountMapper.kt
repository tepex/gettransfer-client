package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.domain.model.PassengerAccount

/**
 * Map a [PassengerAccountEntity] to and from a [PassengerAccount] instance when data is moving between this later and the Domain layer.
 */
open class PassengerAccountMapper(private val profileMapper: ProfileMapper): Mapper<PassengerAccountEntity, PassengerAccount> {
    override fun fromEntity(type: PassengerAccountEntity) = PassengerAccount(profileMapper.fromEntity(type.profile), Mapper.ISO_FORMAT.parse(type.lastSeen))
    override fun toEntity(type: PassengerAccount) = PassengerAccountEntity(profileMapper.toEntity(type.profile), Mapper.ISO_FORMAT.format(type.lastSeen))
}
