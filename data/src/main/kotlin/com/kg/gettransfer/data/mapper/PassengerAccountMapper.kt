package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.SimpleDateFormat

import java.util.Locale

import org.koin.standalone.inject

/**
 * Map a [PassengerAccountEntity] to and from a [PassengerAccount] instance when data is moving between this later and the Domain layer.
 */
open class PassengerAccountMapper: Mapper<PassengerAccountEntity, PassengerAccount> {
    private val profileMapper: ProfileMapper by inject()

    override fun fromEntity(type: PassengerAccountEntity) = PassengerAccount(profileMapper.fromEntity(type.profile), SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(type.lastSeen))
    override fun toEntity(type: PassengerAccount) = PassengerAccountEntity(profileMapper.toEntity(type.profile), SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).format(type.lastSeen))
}
