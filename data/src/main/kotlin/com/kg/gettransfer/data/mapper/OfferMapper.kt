package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.mapper.CarrierMapper
import com.kg.gettransfer.data.mapper.PriceMapper
import com.kg.gettransfer.data.mapper.ProfileMapper
import com.kg.gettransfer.data.mapper.RatingsMapper
import com.kg.gettransfer.data.mapper.VehicleMapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Ratings

/**
 * Map a [OfferEntity] to and from a [Offer] instance when data is moving between this later and the Domain layer.
 */
open class OfferMapper(private val priceMapper: PriceMapper,
                       private val ratingsMapper: RatingsMapper,
                       private val carrierMapper: CarrierMapper,
                       private val vehicleMapper: VehicleMapper,
                       private val profileMapper: ProfileMapper): Mapper<OfferEntity, Offer> {
    /**
     * Map a [OfferEntity] instance to a [Offer] instance.
     */
    override fun fromEntity(type: OfferEntity): Offer {
        var ratings: Ratings? = null
        if(type.ratings != null) ratings = ratingsMapper.fromEntity(type.ratings)
        var driver: Profile? = null
        if(type.driver != null) driver = profileMapper.fromEntity(type.driver)
        return Offer(type.id,
                     type.status,
                     type.wifi,
                     type.refreshments,
                     Mapper.ISO_FORMAT.parse(type.createdAt),
                     priceMapper.fromEntity(type.price),
                     ratings,
                     type.passengerFeedback,
                     carrierMapper.fromEntity(type.carrier),
                     vehicleMapper.fromEntity(type.vehicle),
                     driver)
    }

    /**
     * Map a [Offer] instance to a [OfferEntity] instance.
     */
    override fun toEntity(type: Offer): OfferEntity {
        throw UnsupportedOperationException()
    }
}
