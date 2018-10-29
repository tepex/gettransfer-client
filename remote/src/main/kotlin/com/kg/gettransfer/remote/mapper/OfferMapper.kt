package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.RatingsEntity

import com.kg.gettransfer.remote.model.OfferModel

/**
 * Map a [OfferEntity] from an [OfferModel] instance when data is moving between this later and the Data layer.
 */
open class OfferMapper(private val priceMapper: PriceMapper,
                       private val ratingsMapper: RatingsMapper,
                       private val carrierMapper: CarrierMapper,
                       private val vehicleMapper: VehicleMapper,
                       private val profileMapper: ProfileMapper): EntityMapper<OfferModel, OfferEntity> {
    /**
     * Map a [OfferModel] instance to a [OfferEntity] instance.
     */
    override fun fromRemote(type: OfferModel) =
        OfferEntity(type.id,
                    type.status,
                    type.wifi,
                    type.refreshments,
                    type.createdAt,
                    priceMapper.fromRemote(type.price),
                    type.ratings?.let { ratingsMapper.fromRemote(it) },
                    type.passengerFeedback,
                    carrierMapper.fromRemote(type.carrier),
                    vehicleMapper.fromRemote(type.vehicle),
                    type.driver?.let { profileMapper.fromRemote(it) })

    /**
     * Map a [OfferEntity] instance to a [OfferModel] instance.
     */
    override fun toRemote(type: OfferEntity): OfferModel { throw UnsupportedOperationException() }
}
