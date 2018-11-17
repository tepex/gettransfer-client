package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [PassengerAccountEntity] to and from a [PassengerAccount] instance when data is moving between this later and the Domain layer.
 */
open class PassengerAccountMapper: Mapper<PassengerAccountEntity, PassengerAccount> {
    private val profileMapper = get<ProfileMapper>()
    private val dateFormat    = get<ThreadLocal<DateFormat>>("iso_date")

    override fun fromEntity(type: PassengerAccountEntity) = PassengerAccount(profileMapper.fromEntity(type.profile), dateFormat.get().parse(type.lastSeen))
    override fun toEntity(type: PassengerAccount) = PassengerAccountEntity(profileMapper.toEntity(type.profile), dateFormat.get().format(type.lastSeen))
}
