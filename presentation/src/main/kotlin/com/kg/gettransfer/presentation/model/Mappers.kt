package com.kg.gettransfer.presentation.model

import android.location.Location

import android.support.annotation.StringRes

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.presentation.mapper.Mapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.utilities.Analytics

import java.util.Currency
import java.util.Locale
import java.util.Calendar

import kotlin.math.absoluteValue

import org.koin.standalone.get
import org.koin.standalone.KoinComponent

object Mappers : KoinComponent {
    private val systemInteractor = get<SystemInteractor>()
    private val transportTypeMapper = get<TransportTypeMapper>()


    fun point2LatLng(point: Point) = LatLng(point.latitude, point.longitude)

    fun latLng2Point(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)

    fun getRouteModel(distance: Int?,
                      polyLines: List<String>?,
                      from: String,
                      to: String,
                      fromPoint: Point,
                      toPoint: Point,
                      dateTime: String) = RouteModel(distance ?: Mapper.checkDistance(fromPoint, toPoint),
                                                     polyLines,
                                                     from,
                                                     to,
                                                     fromPoint,
                                                     toPoint,
                                                     dateTime)

    fun getTransferModel(type: Transfer) =
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
            transportTypes = systemInteractor.transportTypes.filter {
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

    @StringRes
    fun getTransferStatusName(status: Transfer.Status): Int? {
        val nameRes = R.string::class.members.find( { it.name == "LNG_RIDE_STATUS_${status.name.toUpperCase()}" } )
        return (nameRes?.call() as Int?)
    }

    fun getTransferNew(from: CityPoint,
                       dest: Dest<CityPoint, Int>,
                       tripTo: Trip,
                       tripReturn: Trip?,
                       transportTypes: List<String>,
                       pax: Int,
                       childSeats: Int?,
                       passengerOfferedPrice: Double?,
                       comment: String?,
                       user: User,
                       promoCode: String = "",
                       paypalOnly: Boolean) = TransferNew(from,
                                                          dest,
                                                          tripTo,
                                                          tripReturn,
                                                          transportTypes,
                                                          pax,
                                                          childSeats,
                                                          passengerOfferedPrice?.let { it.times(100).toInt() },
                                                          comment,
                                                          user,
                                                          promoCode,
                                                          paypalOnly)



}
