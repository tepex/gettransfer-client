package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.repository.SessionRepository

import java.text.DateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

/**
 * Map a [TransferEntity] to and from a [Transfer] instance when data is moving between this later and the Domain layer.
 */
open class TransferMapper : KoinComponent {
    private val dateFormatTZ       = get<ThreadLocal<DateFormat>>("iso_date_TZ")
    private val dateFormat         = get<ThreadLocal<DateFormat>>("iso_date")

    private val transportTypes     = get<SessionRepository>().configs.transportTypes

    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    fun fromEntity(type: TransferEntity) =
        Transfer(
            id              = type.id,
            createdAt       = convertDate(type.createdAt),
            duration        = type.duration,
            distance        = type.distance,
            status          = Transfer.Status.valueOf(type.status.toUpperCase(Locale.US)),
            from            = type.from.map(),
            to              = type.to?.let { it.map() },
            dateToLocal     = convertDate(type.dateToLocal),
            dateToTZ        = convertDateTZ(type.dateToLocal),
            dateReturnLocal = type.dateReturnLocal?.let { convertDate(it) },
            dateReturnTZ    = type.dateReturnLocal?.let { convertDateTZ(it) },
            flightNumber    = type.flightNumber,
/* ================================================== */
            flightNumberReturn    = type.flightNumberReturn,
            transportTypeIds      = type.transportTypeIds.map { TransportType.ID.parse(it) },
            pax                   = type.pax,
            bookNow               = type.bookNow?.let { TransportType.ID.parse(it) },
            time                  = type.time,
            nameSign              = type.nameSign,
            comment               = type.comment,
            childSeats            = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
/* ================================================== */
            childSeatsBooster     = type.childSeatsBooster,
            promoCode             = type.promoCode,
            passengerOfferedPrice = type.passengerOfferedPrice,
            price                 = type.price?.let { it.map() },
            paidSum               = type.paidSum?.let { it.map() },
            remainsToPay          = type.remainsToPay?.let { it.map() },
            paidPercentage        = type.paidPercentage,
            watertaxi             = type.watertaxi,
            bookNowOffers         = type.bookNowOffers.map { entry ->
                entry.value.map(
                    transportTypes.find { it.id === TransportType.ID.parse(entry.key) } ?: transportTypes.first()
                )
            },
            offersCount           = type.offersCount,
/* ================================================== */
            relevantCarriersCount = type.relevantCarriersCount,
            offersUpdatedAt       = type.offersUpdatedAt?.let { convertDate(it) },
            dateRefund            = type.dateRefund?.let { convertDate(it) },
            paypalOnly            = type.paypalOnly,
            carrierMainPhone      = type.carrierMainPhone,
            pendingPaymentId      = type.pendingPaymentId,
            analyticsSent         = type.analyticsSent,
            rubPrice              = type.rubPrice,
            refundedPrice         = type.refundedPrice?.let { it.map() },
            campaign              = type.campaign,
/* ================================================== */
            editableFields      = type.editableFields,
            airlineCard         = type.airlineCard,
            paymentPercentages  = type.paymentPercentages,
            unreadMessagesCount = type.unreadMessagesCount,
            showOfferInfo       = allowOfferInfo(type, convertDate(type.dateReturnLocal ?: type.dateToLocal)),
            lastOffersUpdatedAt = type.lastOffersUpdatedAt?.let { convertDate(it) }
        )

    private fun convertDate (dateString: String) = dateFormat.get().parse(dateString)
    private fun convertDateTZ (dateString: String) = dateFormatTZ.get().parse(dateString)

    companion object {
        private const val HOURS_TO_SHOWING_OFFER_INFO = 24

        private fun allowOfferInfo(transfer: TransferEntity, date: Date): Boolean {
            return if (transfer.status != Transfer.Status.NEW.name.toLowerCase() &&
                transfer.status != Transfer.Status.CANCELED.name.toLowerCase() &&
                transfer.status != Transfer.Status.OUTDATED.name.toLowerCase() &&
                transfer.status != Transfer.Status.PERFORMED.name.toLowerCase()) {

                val calendar = Calendar.getInstance()
                calendar.apply {
                    time = date
                    add(Calendar.MINUTE, transfer.time ?: transfer.duration?.times(60) ?: 0)
                    add(Calendar.MINUTE, HOURS_TO_SHOWING_OFFER_INFO.times(60))
                }
                calendar.time.after(Calendar.getInstance().time)
            } else {
                transfer.status == Transfer.Status.PERFORMED.name.toLowerCase()
            }
        }
    }
}
