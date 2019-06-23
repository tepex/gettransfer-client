package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Offer

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [OfferEntity] to and from a [Offer] instance when data is moving between this later and the Domain layer.
 */
open class OfferMapper : Mapper<OfferEntity, Offer> {
    private val dateFormat    = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [OfferEntity] instance to a [Offer] instance.
     */
    override fun fromEntity(type: OfferEntity) =
        Offer(
            id                = type.id,
            transferId        = type.transferId!!,
            status            = type.status,
            currency          = type.currency,
            wifi              = type.wifi,
            refreshments      = type.refreshments,
            charger           = type.charger,
            createdAt         = dateFormat.get().parse(type.createdAt),
            updatedAt         = type.updatedAt?.let { dateFormat.get().parse(it) },
            price             = type.price.map(),
            ratings           = type.ratings?.let { it.map() },
            passengerFeedback = type.passengerFeedback,
            carrier           = type.carrier.map(),
            vehicle           = type.vehicle.map(),
            driver            = type.driver?.let { it.map() }
        )

    /**
     * Map a [Offer] instance to a [OfferEntity] instance.
     */
    override fun toEntity(type: Offer) =
            OfferEntity(
                    id                = type.id,
                    transferId        = type.transferId,
                    status            = type.status,
                    currency          = type.currency,
                    wifi              = type.wifi,
                    refreshments      = type.refreshments,
                    charger           = type.charger,
                    createdAt         = dateFormat.get().format(type.createdAt),
                    updatedAt         = type.updatedAt?.let { dateFormat.get().format(it) },
                    price             = type.price.map(),
                    ratings           = type.ratings?.let { it.map() },
                    passengerFeedback = type.passengerFeedback,
                    carrier           = type.carrier.map(),
                    vehicle           = type.vehicle.map(),
                    driver            = type.driver?.let { it.map() }
            )
}
