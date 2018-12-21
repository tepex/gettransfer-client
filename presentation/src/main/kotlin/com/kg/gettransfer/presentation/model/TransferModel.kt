package com.kg.gettransfer.presentation.model

import android.support.annotation.StringRes

import com.kg.gettransfer.domain.model.Transfer.Status
import com.kg.gettransfer.domain.model.TransportType

import java.util.Date
import java.util.Locale

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
    val dateTimeReturn: Date?,
    val flightNumber: String?,
/* ================================================== */
/*  flightNumberReturn not used */
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
    val editableFields: List<String>, /* not used */
    val airlineCard: String?,
    val paymentPercentages: List<Int>,
/* ================================================== */
/* ================================================== */
    val statusCategory: String,
    val timeToTransfer: Int
)
