package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.remote.model.OfferModel

import org.koin.standalone.get

/**
 * Map a [OfferEntity] from an [OfferModel] instance when data is moving between this later and the Data layer.
 */
open class OfferMapper : EntityMapper<OfferModel, OfferEntity> {
    private val priceMapper   = get<PriceMapper>()
    private val ratingsMapper = get<RatingsMapper>()
    private val carrierMapper = get<CarrierMapper>()
    private val vehicleMapper = get<VehicleMapper>()
    private val profileMapper = get<ProfileMapper>()

    internal var transferId = 0L

    /**
     * Map a [OfferModel] instance to a [OfferEntity] instance.
     */
    override fun fromRemote(type: OfferModel) =
        OfferEntity(
            id                = type.id,
            transferId        = transferId,
            status            = type.status,
            wifi              = type.wifi,
            refreshments      = type.refreshments,
            createdAt         = type.createdAt,
            updatedAt         = type.updatedAt,
            price             = priceMapper.fromRemote(type.price),
            ratings           = type.ratings?.let { ratingsMapper.fromRemote(it) },
            passengerFeedback = type.passengerFeedback,
            carrier           = carrierMapper.fromRemote(type.carrier),
            vehicle           = vehicleMapper.fromRemote(type.vehicle),
            driver            = type.driver?.let { profileMapper.fromRemote(it) }
        )

    /**
     * Map a [OfferEntity] instance to a [OfferModel] instance.
     */
    override fun toRemote(type: OfferEntity): OfferModel { throw UnsupportedOperationException() }
}
