package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType

import java.text.DateFormat

import org.koin.standalone.get
import java.util.Date
import java.util.Calendar
import java.util.Locale

/**
 * Map a [TransferEntity] to and from a [Transfer] instance when data is moving between this later and the Domain layer.
 */
open class TransferMapper : Mapper<TransferEntity, Transfer> {
    private val cityPointMapper    = get<CityPointMapper>()
    private val bookNowOfferMapper = get<BookNowOfferMapper>()
    private val moneyMapper        = get<MoneyMapper>()
    private val dateFormatTZ        = get<ThreadLocal<DateFormat>>("iso_date_TZ")
    private val dateFormat         = get<ThreadLocal<DateFormat>>("iso_date")



    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    override fun fromEntity(type: TransferEntity) =
        Transfer(
            id              = type.id,
            createdAt       = dateFormat.get().parse(type.createdAt),
            duration        = type.duration,
            distance        = type.distance,
            status          = Transfer.Status.valueOf(type.status.toUpperCase(Locale.US)),
            from            = cityPointMapper.fromEntity(type.from),
            to              = type.to?.let { cityPointMapper.fromEntity(it) },
            dateToLocal     = dateFormat.get().parse(type.dateToLocal),
            dateToTZ        = dateFormatTZ.get().parse(type.dateToLocal),
            dateReturnLocal = type.dateReturnLocal?.let { dateFormat.get().parse(it) },
            dateReturnTZ    = type.dateReturnLocal?.let { dateFormatTZ.get().parse(it) },
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
            price                 = type.price?.let { moneyMapper.fromEntity(it) },
            paidSum               = type.paidSum?.let { moneyMapper.fromEntity(it) },
            remainsToPay          = type.remainsToPay?.let { moneyMapper.fromEntity(it) },
            paidPercentage        = type.paidPercentage,
            watertaxi             = type.watertaxi,
            bookNowOffers         = type.bookNowOffers.entries.associate { TransportType.ID.parse(it.key) to bookNowOfferMapper.fromEntity(it.value) },
            offersCount           = type.offersCount,
/* ================================================== */
            relevantCarriersCount = type.relevantCarriersCount,
            /* offersUpdatedAt */
            dateRefund            = type.dateRefund?.let { dateFormat.get().parse(it) },
            paypalOnly            = type.paypalOnly,
            carrierMainPhone      = type.carrierMainPhone,
            pendingPaymentId      = type.pendingPaymentId,
            analyticsSent         = type.analyticsSent,
            rubPrice              = type.rubPrice,
            refundedPrice         = type.refundedPrice?.let { moneyMapper.fromEntity(it) },
            campaign              = type.campaign,
/* ================================================== */
            editableFields      = type.editableFields,
            airlineCard         = type.airlineCard,
            paymentPercentages  = type.paymentPercentages,
            unreadMessagesCount = type.unreadMessagesCount,
            showOfferInfo       = allowOfferInfo(type, dateFormat.get().parse(type.dateReturnLocal ?: type.dateToLocal))
        )

    /**
     * Map a [Transfer] instance to a [TransferEntity] instance.
     */
    override fun toEntity(type: Transfer): TransferEntity { throw UnsupportedOperationException() }

    companion object {
        private const val HOURS_TO_SHOWING_OFFER_INFO = 24

        private fun allowOfferInfo(transfer: TransferEntity, date: Date): Boolean {
            if (transfer.status != Transfer.Status.NEW.name.toLowerCase() &&
                    transfer.status != Transfer.Status.CANCELED.name.toLowerCase() &&
                    transfer.status != Transfer.Status.OUTDATED.name.toLowerCase() &&
                    transfer.status != Transfer.Status.PERFORMED.name.toLowerCase()) {

                val calendar = Calendar.getInstance()
                calendar.apply {
                    time = date
                    add(Calendar.MINUTE, transfer.time ?: transfer.duration?.times(60) ?: 0)
                    add(Calendar.MINUTE, HOURS_TO_SHOWING_OFFER_INFO.times(60))
                }
                return calendar.time.after(Calendar.getInstance().time)
            }
            return transfer.status == Transfer.Status.PERFORMED.name.toLowerCase()
        }
    }
}
