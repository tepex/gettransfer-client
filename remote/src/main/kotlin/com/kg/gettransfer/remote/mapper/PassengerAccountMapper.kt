package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity
import com.kg.gettransfer.data.model.ProfileEntity

import com.kg.gettransfer.remote.model.PassengerAccountModel

/**
 * Map a [PassengerAccountModel] from a [PassengerAccountEntity] instance when data is moving between this later and the Data layer.
 */
open class PassengerAccountMapper : EntityMapper<PassengerAccountModel, PassengerAccountEntity> {
    override fun fromRemote(type: PassengerAccountModel) =
        PassengerAccountEntity(
            profile = ProfileEntity(
                fullName = type.fullName,
                email = type.email,
                phone = type.phone
            ),
            id = type.id,
            lastSeen = type.lastSeen
        )

    override fun toRemote(type: PassengerAccountEntity): PassengerAccountModel { throw UnsupportedOperationException() }
}
