package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.SimpleDateFormat

import java.util.Locale

/**
 * Map a [PassengerAccountEntity] to and from a [PassengerAccount] instance when data is moving between this later and the Domain layer.
 */
open class PassengerAccountMapper(private val profileMapper: ProfileMapper): Mapper<PassengerAccountEntity, PassengerAccount> {
    private val ISO_FORMAT = SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US)
    
    override fun fromEntity(type: PassengerAccountEntity) = PassengerAccount(profileMapper.fromEntity(type.profile), ISO_FORMAT.parse(type.lastSeen))
    override fun toEntity(type: PassengerAccount) = PassengerAccountEntity(profileMapper.toEntity(type.profile), ISO_FORMAT.format(type.lastSeen))
}
