package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierEntity
import com.kg.gettransfer.data.model.ProfileEntity

import com.kg.gettransfer.remote.model.CarrierModel

import org.koin.standalone.get

/**
 * Map a [CarrierModel] from an [CarrierEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierMapper : EntityMapper<CarrierModel, CarrierEntity> {
    private val localeMapper  = get<LocaleMapper>()
    private val ratingsMapper = get<RatingsMapper>()

    override fun fromRemote(type: CarrierModel) =
        CarrierEntity(
            ProfileEntity(type.id, type.title, type.email, type.phone),
            type.approved,
            type.completedTransfers,
            type.languages.map { localeMapper.fromRemote(it) },
            ratingsMapper.fromRemote(type.ratings),
            type.canUpdateOffers
        )

    override fun toRemote(type: CarrierEntity): CarrierModel { throw UnsupportedOperationException() }
}
