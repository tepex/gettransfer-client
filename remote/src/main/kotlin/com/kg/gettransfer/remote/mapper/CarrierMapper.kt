package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierEntity
import com.kg.gettransfer.data.model.ProfileEntity

import com.kg.gettransfer.remote.model.CarrierModel
import com.kg.gettransfer.remote.model.map

import org.koin.standalone.get

/**
 * Map a [CarrierModel] from an [CarrierEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierMapper : EntityMapper<CarrierModel, CarrierEntity> {
    private val localeMapper  = get<LocaleMapper>()

    override fun fromRemote(type: CarrierModel) =
        CarrierEntity(
            id = type.id,
            profile = ProfileEntity(
                fullName = type.title,
                email = type.email,
                phone = type.phone
            ),
            approved = type.approved,
            completedTransfers = type.completedTransfers,
            languages = type.languages.map { localeMapper.fromRemote(it) },
            ratings = type.ratings.map(),
            canUpdateOffers = type.canUpdateOffers
        )

    override fun toRemote(type: CarrierEntity): CarrierModel { throw UnsupportedOperationException() }
}
