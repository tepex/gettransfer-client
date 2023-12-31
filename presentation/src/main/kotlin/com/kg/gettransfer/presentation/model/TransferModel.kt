package com.kg.gettransfer.presentation.model

import androidx.annotation.StringRes

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Status
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.presentation.model.TransferModel.Companion.MILLIS_PER_MINUTE
import com.kg.gettransfer.presentation.ui.utils.DistanceUtils

import kotlin.math.absoluteValue

import java.util.Date

data class TransferModel(
    val id: Long,
    val createdAt: Date,
    val duration: Int?,
    val distance: Int?,
    val status: Status,
    @StringRes
    val statusName: Int?,
    val from: String,
    val to: String?,
    val dateTime: Date,
    val dateTimeTZ: Date,
    val dateTimeReturn: Date?,
    val dateTimeReturnTZ: Date?,
    val flightNumber: String?,
/* ================================================== */
    val flightNumberReturn: String?,
    val transportTypes: List<TransportTypeModel>,
    val countPassengers: Int,
    val bookNow: TransportType.ID?,
    val time: Int?,
    val nameSign: String?,
    val comment: String?,
    val countChilds: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
/* ================================================== */
    val childSeatsBooster: Int,
    val promoCode: String?,
    val passengerOfferedPrice: String?,
    val price: String?,
    val paidSum: String?,
    val remainsToPay: String?,
    val paidPercentage: Int,
    val watertaxi: Boolean,
    val bookNowOffers: List<BookNowOfferModel>,
    val offersCount: Int,
/* ================================================== */
    val relevantCarriersCount: Int?,
    /* offersUpdatedAt */
    val dateRefund: Date?,
    val paypalOnly: Boolean?,
    val carrierMainPhone: String?,
    val pendingPaymentId: Int?,
    val analyticsSent: Boolean,
    val rubPrice: Double?,
    val refundedPrice: String?,
    val campaign: String?,
/* ================================================== */
    val editableFields: List<String>?, /* not used */
    val airlineCard: String?,
    val unreadMessagesCount: Int,
/* ================================================== */
/* ================================================== */
    val statusCategory: String,
    val timeToTransfer: Int,
    val matchedOffer: Offer? = null
) {
    fun isBookNow() = paidPercentage != 0 && bookNow != null

    fun isPaymentInProgress() = pendingPaymentId != null

    fun getChildrenCount() = childSeatsInfant + childSeatsBooster + childSeatsConvertible

    companion object {
        const val MILLIS_PER_MINUTE = 60_000
        const val ZERO_VALUE = 0
    }
}

// private val systemTransportTypes = get<SystemInteractor>().transportTypes

fun Transfer.map(transportTypesModels: List<TransportTypeModel>) =
    TransferModel(
        id,
        createdAt,
        duration,
        distance ?: to?.point?.let { toPoint ->
            from.point?.let { fromPoint ->
                DistanceUtils.getPointToPointDistance(fromPoint, toPoint)
            }
        },
        status,
        R.string::class.members.find { it.name == "LNG_RIDE_STATUS_${status.name}" }?.call() as? Int,
        from.name,
        to?.name,
        dateToLocal,
        dateToTZ,
        dateReturnLocal,
        dateReturnTZ,
        flightNumber,
/* ================================================== */
        flightNumberReturn,
        transportTypesModels.filter { transportTypeIds.contains(it.id) },
        pax,
        bookNow,
        time,
        nameSign,
        comment,
        childSeats,
        childSeatsInfant,
        childSeatsConvertible,
/* ================================================== */
        childSeatsBooster,
        promoCode,
        passengerOfferedPrice,
        price?.def,
        paidSum?.def,
        remainsToPay?.def,
        paidPercentage,
        watertaxi,
        bookNowOffers.map { it.map() },
        offersCount,
/* ================================================== */
        relevantCarriersCount,
        /* offersUpdatedAt */
        dateRefund,
        paypalOnly,
        carrierMainPhone,
        pendingPaymentId,
        analyticsSent,
        rubPrice,
        refundedPrice?.def,
        campaign,
/* ================================================== */
        editableFields,
        airlineCard,
        unreadMessagesCount,
/* ================================================== */
/* ================================================== */
        checkStatusCategory(),
        ((dateToTZ.time - Date().time).absoluteValue / MILLIS_PER_MINUTE).toInt()
    )
