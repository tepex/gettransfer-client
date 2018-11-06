package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.remote.model.CarrierModel

/**
 * Map a [CarrierModel] from an [CarrierEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierMapper(private val localeMapper: LocaleMapper,
                         private val ratingsMapper: RatingsMapper): EntityMapper<CarrierModel, CarrierEntity> {
    override fun fromRemote(type: CarrierModel) =
        CarrierEntity(type.id,
                      type.title,
                      type.email,
                      type.phone,
                      type.approved,
                      type.completedTransfers,
                      type.languages.map { localeMapper.fromRemote(it) },
                      ratingsMapper.fromRemote(type.ratings),
                      type.canUpdateOffers)
    override fun toRemote(type: CarrierEntity): CarrierModel { throw UnsupportedOperationException() }
}
