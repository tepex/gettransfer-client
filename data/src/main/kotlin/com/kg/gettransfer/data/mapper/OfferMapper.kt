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

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [OfferEntity] to and from a [Offer] instance when data is moving between this later and the Domain layer.
 */
open class OfferMapper : Mapper<OfferEntity, Offer> {
    private val priceMapper   = get<PriceMapper>()
    private val ratingsMapper = get<RatingsMapper>()
    private val carrierMapper = get<CarrierMapper>()
    private val vehicleMapper = get<VehicleMapper>()
    private val profileMapper = get<ProfileMapper>()
    private val dateFormat    = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [OfferEntity] instance to a [Offer] instance.
     */
    override fun fromEntity(type: OfferEntity) =
        Offer(
            id                = type.id,
            transferId        = type.transferId!!,
            status            = type.status,
            wifi              = type.wifi,
            refreshments      = type.refreshments,
            createdAt         = dateFormat.get().parse(type.createdAt),
            updatedAt         = type.updatedAt?.let { dateFormat.get().parse(it) },
            price             = priceMapper.fromEntity(type.price),
            ratings           = type.ratings?.let { ratingsMapper.fromEntity(it) },
            passengerFeedback = type.passengerFeedback,
            carrier           = carrierMapper.fromEntity(type.carrier),
            vehicle           = vehicleMapper.fromEntity(type.vehicle),
            driver            = type.driver?.let { profileMapper.fromEntity(it) }
        )

    /**
     * Map a [Offer] instance to a [OfferEntity] instance.
     */
    override fun toEntity(type: Offer) =
            OfferEntity(
                    id                = type.id,
                    transferId        = type.transferId,
                    status            = type.status,
                    wifi              = type.wifi,
                    refreshments      = type.refreshments,
                    createdAt         = dateFormat.get().format(type.createdAt),
                    updatedAt         = type.updatedAt?.let { dateFormat.get().format(it) },
                    price             = priceMapper.toEntity(type.price),
                    ratings           = type.ratings?.let { ratingsMapper.toEntity(it) },
                    passengerFeedback = type.passengerFeedback,
                    carrier           = carrierMapper.toEntity(type.carrier),
                    vehicle           = vehicleMapper.toEntity(type.vehicle),
                    driver            = type.driver?.let { profileMapper.toEntity(it) }
            )
}
