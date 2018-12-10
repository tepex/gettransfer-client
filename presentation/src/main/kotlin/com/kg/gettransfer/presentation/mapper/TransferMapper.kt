package com.kg.gettransfer.presentation.mapper

import android.support.annotation.StringRes

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.presentation.model.TransferModel

import java.util.Calendar

import kotlin.math.absoluteValue

import org.koin.standalone.get

open class TransferMapper : Mapper<TransferModel, Transfer> {
    private val transportTypeMapper  = get<TransportTypeMapper>()
    private val systemTransportTypes = get<SystemInteractor>().transportTypes

    override fun toView(type: Transfer) =
        TransferModel(
            id = type.id,
            createdAt = type.createdAt,
            duration = type.duration,
            distance = type.to?.let { type.distance ?: Mapper.checkDistance(type.from.point!!, type.to!!.point!!) },
            status = type.status,
            statusName = getTransferStatusName(type.status),
            from = type.from.name!!,
            to = type.to?.name,
            dateTime = type.dateToLocal,
            /* dateReturn */
            dateRefund = type.dateRefund,
/* ================================================== */
            nameSign = type.nameSign,
            comment = type.comment,
            /* malinaCard */
            flightNumber = type.flightNumber,
            /* flightNumberReturn */
            countPassengers = type.pax,
            countChilds = type.childSeats,
            offersCount = type.offersCount,
            relevantCarriersCount = type.relevantCarriersCount,
            /* offersUpdatedAt */
/* ================================================== */
            time = type.time,
            paidSum = type.paidSum?.default,
            remainToPay = type.remainsToPay?.default,
            paidPercentage = type.paidPercentage,
            /* pendingPaymentId
               bookNow
               bookNowExpiration */
            transportTypes = systemTransportTypes.filter {
                type.transportTypeIds.contains(it.id) }.map { transportTypeMapper.toView(it) },
            /* passengerOfferedPrice */
            price = type.price?.default,
/* ================================================== */
            paymentPrecentages = type.paymentPercentages,
/* ================================================== */
/* ================================================== */
            statusCategory = type.checkStatusCategory(),
            timeToTransfer = (type.dateToLocal.time - Calendar.getInstance().timeInMillis).toInt().absoluteValue / 60_000
            //checkOffers = type.checkOffers
        )

    override fun fromView(type: TransferModel): Transfer { throw UnsupportedOperationException() }

    companion object {
        @StringRes
        private fun getTransferStatusName(status: Transfer.Status): Int? {
            val nameRes = R.string::class.members.find( { it.name == "LNG_RIDE_STATUS_${status.name.toUpperCase()}" } )
            return (nameRes?.call() as Int?)
        }
    }
}
