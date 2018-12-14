package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.remote.model.TripModel

/**
 * Map a [TripModel] from an [TripEntity] instance when data is moving between this later and the Data layer.
 */
open class TripMapper : EntityMapper<TripModel, TripEntity> {
    override fun fromRemote(type: TripModel) = TripEntity(type.date, type.time, type.flight)
    override fun toRemote(type: TripEntity)  = TripModel(type.date, type.time, type.flight)
}
