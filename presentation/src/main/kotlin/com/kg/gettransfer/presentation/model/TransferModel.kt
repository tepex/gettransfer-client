package com.kg.gettransfer.presentation.model

import android.support.annotation.StringRes

import com.kg.gettransfer.domain.model.Transfer.Status

import java.util.Date
import java.util.Locale

class TransferModel(
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
    /* dateReturn not used */
    val dateRefund: Date?,
/* ================================================== */
    val nameSign: String?,
    val comment: String?,
    /* malinaCard not used */
    val flightNumber: String?,
    /* flightNumberReturn not used */
    val countPassengers: Int,
    val countChilds: Int,
    val offersCount: Int,
    val relevantCarriersCount: Int?,
    /* offersUpdatedAt */
/* ================================================== */
    val time: Int?,
    val paidSum: String?,
    val remainToPay: String?,
    val paidPercentage: Int,
    /* pendingPaymentId
       bookNow
       bookNowExpiration */
    val transportTypes: List<TransportTypeModel>,
    /* passengerOfferedPrice */
    val price: String?,
/* ================================================== */
    val paymentPrecentages: List<Int>,
/* ================================================== */
/* ================================================== */
    val statusCategory: String,
    val timeToTransfer: Int
    //val checkOffers: Boolean
)
