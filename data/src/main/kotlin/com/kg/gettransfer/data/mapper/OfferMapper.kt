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

import java.text.SimpleDateFormat

import java.util.Locale


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
    override fun fromEntity(type: OfferEntity) =
        Offer(type.id,
              type.status,
              type.wifi,
              type.refreshments,
              SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(type.createdAt),
              type.updatedAt?.let { SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(it) },
              priceMapper.fromEntity(type.price),
              type.ratings?.let { ratingsMapper.fromEntity(it) },
              type.passengerFeedback,
              carrierMapper.fromEntity(type.carrier),
              vehicleMapper.fromEntity(type.vehicle),
              type.driver?.let { profileMapper.fromEntity(it) })

    /**
     * Map a [Offer] instance to a [OfferEntity] instance.
     */
    override fun toEntity(type: Offer): OfferEntity { throw UnsupportedOperationException() }
}
