package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.RatingsEntity

import com.kg.gettransfer.remote.model.RatingsModel

/**
 * Map a [RatingsModel] from an [RatingsEntity] instance when data is moving between this later and the Data layer.
 */
open class RatingsMapper : EntityMapper<RatingsModel, RatingsEntity> {
    override fun fromRemote(type: RatingsModel) =
        RatingsEntity(
            average = type.average,
            vehicle = type.vehicle,
            driver = type.driver,
            fair = type.fair
        )

    override fun toRemote(type: RatingsEntity) =
        RatingsModel(
            average = type.average,
            vehicle = type.vehicle,
            driver = type.driver,
            fair = type.fair
        )
}
