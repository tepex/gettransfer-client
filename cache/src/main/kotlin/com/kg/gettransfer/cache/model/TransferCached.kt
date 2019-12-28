package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.data.model.TransferEntity

@Entity(tableName = TransferEntity.ENTITY_NAME)
data class TransferCached(
    @PrimaryKey @ColumnInfo(name = TransferEntity.ID)          val id: Long,
    @ColumnInfo(name = TransferEntity.CREATED_AT)              val createdAt: String,
    @ColumnInfo(name = TransferEntity.DURATION)                val duration: Int?,
    @ColumnInfo(name = TransferEntity.DISTANCE)                val distance: Int?,
    @ColumnInfo(name = TransferEntity.STATUS)                  val status: String,
    @Embedded(prefix = TransferEntity.FROM)                    val from: CityPointCached,
    @Embedded(prefix = TransferEntity.TO)                      val to: CityPointCached?,
    @ColumnInfo(name = TransferEntity.DATE_TO_LOCAL)           val dateToLocal: String,
    @ColumnInfo(name = TransferEntity.DATE_RETURN_LOCAL)       val dateReturnLocal: String?,
    @ColumnInfo(name = TransferEntity.FLIGHT_NUMBER)           val flightNumber: String?,
    /* ================================================== */
    @ColumnInfo(name = TransferEntity.FLIGHT_NUMBER_RETURN)    val flightNumberReturn: String?,
    @ColumnInfo(name = TransferEntity.TRANSPORT_TYPE_IDS)      val transportTypeIds: StringList,
    @ColumnInfo(name = TransferEntity.PAX)                     val pax: Int,
    @ColumnInfo(name = TransferEntity.BOOK_NOW)                val bookNow: String?,
    @ColumnInfo(name = TransferEntity.TIME)                    val time: Int?,
    @ColumnInfo(name = TransferEntity.NAME_SIGN)               val nameSign: String?,
    @ColumnInfo(name = TransferEntity.COMMENT)                 val comment: String?,
    @ColumnInfo(name = TransferEntity.CHILD_SEATS)             val childSeats: Int,
    @ColumnInfo(name = TransferEntity.CHILD_SEATS_INFANT)      val childSeatsInfant: Int,
    @ColumnInfo(name = TransferEntity.CHILD_SEATS_CONVERTIBLE) val childSeatsConvertible: Int,
    /* ================================================== */
    @ColumnInfo(name = TransferEntity.CHILD_SEATS_BOOSTER)     val childSeatsBooster: Int,
    @ColumnInfo(name = TransferEntity.PROMO_CODE)              val promoCode: String?,
    @ColumnInfo(name = TransferEntity.PASSENGER_OFFERED_PRICE) val passengerOfferedPrice: String?,
    @Embedded(prefix = TransferEntity.PRICE)                   val price: MoneyCached?,
    @Embedded(prefix = TransferEntity.PAID_SUM)                val paidSum: MoneyCached?,
    @Embedded(prefix = TransferEntity.REMAINS_TO_PAY)          val remainsToPay: MoneyCached?,
    @ColumnInfo(name = TransferEntity.PAID_PERCENTAGE)         val paidPercentage: Int,
    @ColumnInfo(name = TransferEntity.WATERTAXI)               val watertaxi: Boolean,
    @ColumnInfo(name = TransferEntity.BOOK_NOW_OFFERS)         val bookNowOffers: BookNowOfferCachedMap,
    @ColumnInfo(name = TransferEntity.OFFERS_COUNT)            val offersCount: Int,
    /* ================================================== */
    @ColumnInfo(name = TransferEntity.RELEVANT_CARRIERS_COUNT) val relevantCarriersCount: Int,
    @ColumnInfo(name = TransferEntity.OFFERS_UPDATED_AT)       val offersUpdatedAt: String?,
    @ColumnInfo(name = TransferEntity.DATE_REFUND)             val dateRefund: String?,
    @ColumnInfo(name = TransferEntity.PAYPAL_ONLY)             val paypalOnly: Boolean?,
    @ColumnInfo(name = TransferEntity.CARRIER_MAIN_PHONE)      val carrierMainPhone: String?,
    @ColumnInfo(name = TransferEntity.PENDING_PAYMENT_ID)      val pendingPaymentId: Int?,
    @ColumnInfo(name = TransferEntity.ANALYTICS_SENT)          val analyticsSent: Boolean,
    @ColumnInfo(name = TransferEntity.RUB_PRICE)               val rubPrice: Double?,
    @Embedded(prefix = TransferEntity.REFUNDED_PRICE)          val refundedPrice: MoneyCached?,
    @ColumnInfo(name = TransferEntity.CAMPAIGN)                val campaign: String?,
    /* ================================================== */
    @ColumnInfo(name = TransferEntity.EDITABLE_FIELDS)         val editableFields: StringList?,
    @ColumnInfo(name = TransferEntity.AIRLINE_CARD)            val airlineCard: String?,
    @ColumnInfo(name = TransferEntity.UNREAD_MESSAGES_COUNT)   val unreadMessagesCount: Int,
    @ColumnInfo(name = TransferEntity.LAST_OFFERS_UPDATED_AT)  val lastOffersUpdatedAt: String?
)

fun TransferCached.map() =
    TransferEntity(
        id,
        createdAt,
        duration,
        distance,
        status,
        from.map(),
        to?.map(),
        dateToLocal,
        dateReturnLocal,
        flightNumber,
        /* ================================================== */
        flightNumberReturn,
        transportTypeIds.list,
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
        price?.map(),
        paidSum?.map(),
        remainsToPay?.map(),
        paidPercentage,
        watertaxi,
        bookNowOffers.map.mapValues { it.value.map() },
        offersCount,
        /* ================================================== */
        relevantCarriersCount,
        offersUpdatedAt,
        dateRefund,
        paypalOnly,
        carrierMainPhone,
        pendingPaymentId,
        analyticsSent,
        rubPrice,
        refundedPrice?.map(),
        campaign,
        /* ================================================== */
        editableFields?.list,
        airlineCard,
        unreadMessagesCount,
        lastOffersUpdatedAt
    )

fun TransferEntity.map() =
    TransferCached(
        id,
        createdAt,
        duration,
        distance,
        status,
        from.map(),
        to?.map(),
        dateToLocal,
        dateReturnLocal,
        flightNumber,
        /* ================================================== */
        flightNumberReturn,
        StringList(transportTypeIds),
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
        price?.map(),
        paidSum?.map(),
        remainsToPay?.map(),
        paidPercentage,
        watertaxi,
        BookNowOfferCachedMap(bookNowOffers.mapValues { it.value.map() }),
        offersCount,
        /* ================================================== */
        relevantCarriersCount,
        offersUpdatedAt,
        dateRefund,
        paypalOnly,
        carrierMainPhone,
        pendingPaymentId,
        analyticsSent,
        rubPrice,
        refundedPrice?.map(),
        campaign,
        /* ================================================== */
        editableFields?.let { StringList(it) },
        airlineCard,
        unreadMessagesCount,
        lastOffersUpdatedAt
    )
