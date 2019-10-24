package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils

import org.koin.core.inject

open class OfferMapper : Mapper<OfferModel, Offer> {
    private val carrierMapper: CarrierMapper by inject()
    private val profileMapper: ProfileMapper by inject()
    lateinit var url: String

    override fun toView(type: Offer) =
        OfferModel(
            type.id,
            type.transferId,
            type.status,
            type.currency,
            type.wifi,
            type.isNameSignPresent,
            type.isWithNameSign,
            type.refreshments,
            type.charger,
            SystemUtils.formatDateTime(type.createdAt),
            type.price,
            type.ratings,
            type.passengerFeedback,
            carrierMapper.toView(type.carrier),
            type.vehicle.map(url),
            type.driver?.let { profileMapper.toView(it) },
            type.phoneToCall
        )

    override fun fromView(type: OfferModel): Offer { throw UnsupportedOperationException() }
}
