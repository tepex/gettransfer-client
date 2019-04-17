package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.IntList
import com.kg.gettransfer.cache.model.BookNowOfferCachedMap
import com.kg.gettransfer.data.model.TransferEntity

import org.koin.standalone.get

open class TransferEntityMapper : EntityMapper<TransferCached, TransferEntity> {
    private val moneyMapper = get<MoneyEntityMapper>()
    private val cityPointMapper = get<CityPointEntityMapper>()
    private val bookNowOfferMapper = get<BookNowOfferEntityMapper>()

    override fun fromCached(type: TransferCached) =
            TransferEntity(
                    id                    = type.id,
                    createdAt             = type.createdAt,
                    duration              = type.duration,
                    distance              = type.distance,
                    status                = type.status,
                    from                  = cityPointMapper.fromCached(type.from),
                    to                    = type.to?.let { cityPointMapper.fromCached(it) },
                    dateToLocal           = type.dateToLocal,
                    dateReturnLocal       = type.dateReturnLocal,
                    flightNumber          = type.flightNumber,
                    /* ================================================== */
                    flightNumberReturn    = type.flightNumberReturn,
                    transportTypeIds      = type.transportTypeIds.list,
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
                    price                 = type.price?.let { moneyMapper.fromCached(it) },
                    paidSum               = type.paidSum?.let { moneyMapper.fromCached(it) },
                    remainsToPay          = type.remainsToPay?.let { moneyMapper.fromCached(it) },
                    paidPercentage        = type.paidPercentage,
                    watertaxi             = type.watertaxi,
                    bookNowOffers         = type.bookNowOffers.map.mapValues { bookNowOfferMapper.fromCached(it.value) },
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
                    refundedPrice         = type.refundedPrice?.let { moneyMapper.fromCached(it) },
                    campaign              = type.campaign,
                    /* ================================================== */
                    editableFields        = type.editableFields?.list,
                    airlineCard           = type.airlineCard,
                    paymentPercentages    = type.paymentPercentages?.list,
                    unreadMessagesCount   = type.unreadMessagesCount,
                    lastOffersUpdatedAt   = type.lastOffersUpdatedAt
            )

    override fun toCached(type: TransferEntity) =
            TransferCached(
                    id                    = type.id,
                    createdAt             = type.createdAt,
                    duration              = type.duration,
                    distance              = type.distance,
                    status                = type.status,
                    from                  = cityPointMapper.toCached(type.from),
                    to                    = type.to?.let { cityPointMapper.toCached(it) },
                    dateToLocal           = type.dateToLocal,
                    dateReturnLocal       = type.dateReturnLocal,
                    flightNumber          = type.flightNumber,
                    /* ================================================== */
                    flightNumberReturn    = type.flightNumberReturn,
                    transportTypeIds      = StringList(type.transportTypeIds),
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
                    price                 = type.price?.let { moneyMapper.toCached(it) },
                    paidSum               = type.paidSum?.let { moneyMapper.toCached(it) },
                    remainsToPay          = type.remainsToPay?.let { moneyMapper.toCached(it) },
                    paidPercentage        = type.paidPercentage,
                    watertaxi             = type.watertaxi,
                    bookNowOffers         = BookNowOfferCachedMap(type.bookNowOffers.mapValues { bookNowOfferMapper.toCached(it.value) }),
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
                    refundedPrice         = type.refundedPrice?.let { moneyMapper.toCached(it) },
                    campaign              = type.campaign,
                    /* ================================================== */
                    editableFields        = type.editableFields?.let { StringList(it) },
                    airlineCard           = type.airlineCard,
                    paymentPercentages    = type.paymentPercentages?.let { IntList(it) },
                    unreadMessagesCount   = type.unreadMessagesCount,
                    lastOffersUpdatedAt   = type.lastOffersUpdatedAt
            )
}