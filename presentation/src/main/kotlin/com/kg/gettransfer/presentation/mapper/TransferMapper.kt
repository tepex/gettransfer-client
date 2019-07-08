package com.kg.gettransfer.presentation.mapper

import android.support.annotation.StringRes

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.map

import kotlin.math.absoluteValue

import java.util.Date

import org.koin.core.get

open class TransferMapper : Mapper<TransferModel, Transfer> {
    private val systemTransportTypes = get<SystemInteractor>().transportTypes

    override fun toView(type: Transfer): TransferModel {
        val transportTypesModels = systemTransportTypes.map { it.map() }

        return TransferModel(
            id             = type.id,
            createdAt      = type.createdAt,
            duration       = type.duration,
            distance       = type.to?.let { type.distance ?: Mapper.checkDistance(type.from.point!!, type.to!!.point) },
            status         = type.status,

            statusName     = getTransferStatusNameId(type.status),
            from           = type.from.name,
            to             = type.to?.name,
            dateTime       = type.dateToLocal,
            dateTimeTZ     = type.dateToTZ,
            dateTimeReturn = type.dateReturnLocal,
            dateTimeReturnTZ = type.dateReturnTZ,
            flightNumber   = type.flightNumber,
/* ================================================== */
            flightNumberReturn    = type.flightNumberReturn,
            transportTypes        = transportTypesModels.filter { type.transportTypeIds.contains(it.id) },
            countPassengers       = type.pax,
            bookNow               = type.bookNow,
            time                  = type.time,
            nameSign              = type.nameSign,
            comment               = type.comment,
            countChilds           = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
/* ================================================== */
            childSeatsBooster     = type.childSeatsBooster,
            promoCode             = type.promoCode,
            passengerOfferedPrice = type.passengerOfferedPrice,
            price                 = type.price?.def,
            paidSum               = type.paidSum?.def,
            remainsToPay          = type.remainsToPay?.def,
            paidPercentage        = type.paidPercentage,
            watertaxi             = type.watertaxi,
            bookNowOffers         = type.bookNowOffers.map { it.map() },
            offersCount           = type.offersCount,
/* ================================================== */
            relevantCarriersCount = type.relevantCarriersCount,
            /* offersUpdatedAt */
            dateRefund            = type.dateRefund,
            paypalOnly            = type.paypalOnly,
            carrierMainPhone      = type.carrierMainPhone,
            pendingPaymentId      = type.pendingPaymentId,
            analyticsSent         = type.analyticsSent,
            rubPrice              = type.rubPrice,
            refundedPrice         = type.refundedPrice?.def,
            campaign              = type.campaign,
/* ================================================== */
            editableFields        = type.editableFields,
            airlineCard           = type.airlineCard,
            paymentPercentages    = type.paymentPercentages,
            unreadMessagesCount   = type.unreadMessagesCount,
/* ================================================== */
/* ================================================== */
            statusCategory = type.checkStatusCategory(),
            timeToTransfer = ((type.dateToTZ.time - Date().time).absoluteValue / 60_000).toInt(),
            showOfferInfo  = type.showOfferInfo
            //checkOffers = type.checkOffers
        )
    }

    override fun fromView(type: TransferModel): Transfer { throw UnsupportedOperationException() }

    companion object {
        @StringRes
        private fun getTransferStatusNameId(status: Transfer.Status): Int? {
            val nameRes = R.string::class.members.find( { it.name == "LNG_RIDE_STATUS_${status.name}" } )
            return (nameRes?.call() as Int?)
        }
    }
}
