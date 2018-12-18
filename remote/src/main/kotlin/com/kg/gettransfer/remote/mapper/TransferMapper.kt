package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.BookNowOfferEntity
import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.remote.model.TransferModel

import org.koin.standalone.get

/**
 * Map a [TransferEntity] to and from a [TransferModel] instance when data is moving between this later and the Data layer.
 */
open class TransferMapper : EntityMapper<TransferModel, TransferEntity> {
    private val bookNowOfferMapper = get<BookNowOfferMapper>()
    private val cityPointMapper    = get<CityPointMapper>()
    private val moneyMapper        = get<MoneyMapper>()

    /**
     * Map a [TransferModel] instance to a [TransferEntity] instance.
     */
    override fun fromRemote(type: TransferModel) =
        TransferEntity(
            id                    = type.id,
            createdAt             = type.createdAt,
            duration              = type.duration,
            distance              = type.distance,
            status                = type.status,
            from                  = cityPointMapper.fromRemote(type.from),
            to                    = type.to?.let { cityPointMapper.fromRemote(it) },
            dateToLocal           = type.dateToLocal,
            dateReturnLocal       = type.dateReturnLocal,
            flightNumber          = type.flightNumber,
/* ================================================== */
            flightNumberReturn    = type.flightNumberReturn,
            transportTypeIds      = type.transportTypeIds,
            pax                   = type.pax,
            bookNow               = type.bookNow,
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
            price                 = type.price?.let { moneyMapper.fromRemote(it) },
            paidSum               = type.paidSum?.let { moneyMapper.fromRemote(it) },
            remainsToPay          = type.remainsToPay?.let { moneyMapper.fromRemote(it) },
            paidPercentage        = type.paidPercentage,
            watertaxi             = type.watertaxi,
            bookNowOffers         = type.bookNowOffers?.map { bookNowOfferMapper.fromRemote(it) } ?: emptyList<BookNowOfferEntity>(),
            offersCount           = type.offersCount,
/* ================================================== */
            relevantCarriersCount = type.relevantCarriersCount,
            offersUpdatedAt       = type.offersUpdatedAt,
            dateRefund            = type.dateRefund,
            paypalOnly            = type.paypalOnly,
            carrierMainPhone      = type.carrierMainPhone,
            pendingPaymentId      = type.pendingPaymentId,
            analyticsSent         = type.analyticsSent,
            rubPrice              = type.rubPrice,
            refundedPrice         = type.refundedPrice?.let { moneyMapper.fromRemote(it) },
            campaign              = type.campaign,
/* ================================================== */
            editableFields        = type.editableFields,
            airlineCard           = type.airlineCard,
            paymentPercentages    = type.paymentPercentages
        )

    /**
     * Map a [TransferEntity] instance to a [TransferModel] instance.
     */
    override fun toRemote(type: TransferEntity): TransferModel { throw UnsupportedOperationException() }
}
