package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.domain.model.Carrier

/**
 * Map a [CarrierEntity] to and from a [Carrier] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierMapper(): Mapper<CarrierEntity, Carrier> {
    /**
     * Map a [CarrierEntity] instance to a [Carrier] instance.
     */
    override fun fromEntity(type: CarrierEntity) = 
        Carrier(type.title,
                type.)
    /**
     * Map a [Carrier] instance to a [CarrierEntity] instance.
     */
    override fun toEntity(type: Carrier) = CarrierEntity(type.average, type.vehicle, type.driver, type.fair)
}

data class Carrier(val title: String?,
                   val email: String?,
                   val phone: String?,
                   val id: Long,
                   val approved: Boolean,
                   val completedTransfers: Int,
                   val languages: List<Locale>,
                   val ratings: Ratings,
                   val canUpdateOffers: Boolean?)
