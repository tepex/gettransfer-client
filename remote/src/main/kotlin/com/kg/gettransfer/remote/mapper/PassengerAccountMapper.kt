package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.remote.model.PassengerAccountModel

/**
 * Map a [PassengerAccountModel] from a [PassengerAccountEntity] instance when data is moving between this later and the Data layer.
 */
open class PassengerAccountMapper(private val profileMapper: ProfileMapper): EntityMapper<PassengerAccountModel, PassengerAccountEntity> {

    override fun fromRemote(type: PassengerAccountModel) = 
        PassengerAccountEntity(profileMapper.fromRemote(type.))
    override fun toRemote(type: PassengerAccountEntity): PassengerAccountModel(type.name, type.point, type.placeId)
}
