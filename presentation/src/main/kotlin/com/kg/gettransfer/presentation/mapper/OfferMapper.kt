package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils

import org.koin.core.get

open class OfferMapper : Mapper<OfferModel, Offer> {
    private val carrierMapper = get<CarrierMapper>()
    private val vehicleMapper = get<VehicleMapper>()
    private val profileMapper = get<ProfileMapper>()

    override fun toView(type: Offer) =
        OfferModel(
            type.id,
            type.transferId,
            type.status,
            type.currency,
            type.wifi,
            type.refreshments,
            type.charger,
            SystemUtils.formatDateTime(type.createdAt),
            type.price,
            type.ratings?.let { it.map() },
            type.passengerFeedback,
            carrierMapper.toView(type.carrier),
            vehicleMapper.toView(type.vehicle),
            type.driver?.let { profileMapper.toView(it) },
            type.phoneToCall
        )

    override fun fromView(type: OfferModel): Offer { throw UnsupportedOperationException() }
}
