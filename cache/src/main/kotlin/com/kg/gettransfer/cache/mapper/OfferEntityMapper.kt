package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.data.model.OfferEntity

import org.koin.standalone.get

open class OfferEntityMapper : EntityMapper<OfferCached, OfferEntity> {
    private val priceMapper = get<PriceEntityMapper>()
    private val ratingsMapper = get<RatingsEntityMapper>()
    private val carrierMapper = get<CarrierEntityMapper>()
    private val vehicleMapper = get<VehicleEntityMapper>()
    private val profileMapper = get<ProfileEntityMapper>()

    override fun fromCached(type: OfferCached) =
            OfferEntity(
                    id = type.id,
                    transferId = type.transferId,
                    status = type.status,
                    wifi = type.wifi,
                    refreshments = type.refreshments,
                    charger = type.charger,
                    createdAt = type.createdAt,
                    updatedAt = type.updatedAt,
                    price = priceMapper.fromCached(type.price),
                    ratings = type.ratings?.let { ratingsMapper.fromCached(it) },
                    passengerFeedback = type.passengerFeedback,
                    carrier = carrierMapper.fromCached(type.carrier),
                    vehicle = vehicleMapper.fromCached(type.vehicle),
                    driver = type.driver?.let { profileMapper.fromCached(it) }
            )

    override fun toCached(type: OfferEntity) =
            OfferCached(
                    id = type.id,
                    transferId = type.transferId,
                    status = type.status,
                    wifi = type.wifi,
                    refreshments = type.refreshments,
                    charger = type.charger,
                    createdAt = type.createdAt,
                    updatedAt = type.updatedAt,
                    price = priceMapper.toCached(type.price),
                    ratings = type.ratings?.let { ratingsMapper.toCached(it) },
                    passengerFeedback = type.passengerFeedback,
                    carrier = carrierMapper.toCached(type.carrier),
                    vehicle = vehicleMapper.toCached(type.vehicle),
                    driver = type.driver?.let { profileMapper.toCached(it) }
            )
}