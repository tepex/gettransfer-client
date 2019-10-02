package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.TransferEntity

data class TransferModel(
    @SerializedName(TransferEntity.ID) @Expose                val id: Long,
    @SerializedName(TransferEntity.CREATED_AT) @Expose        val createdAt: String,
    @SerializedName(TransferEntity.DURATION) @Expose          val duration: Int?,
    @SerializedName(TransferEntity.DISTANCE) @Expose          val distance: Int?,
    @SerializedName(TransferEntity.STATUS) @Expose            val status: String,
    @SerializedName(TransferEntity.FROM) @Expose              val from: CityPointModel,
    @SerializedName(TransferEntity.TO) @Expose                val to: CityPointModel?,
    @SerializedName(TransferEntity.DATE_TO_LOCAL) @Expose     val dateToLocal: String,
    @SerializedName(TransferEntity.DATE_RETURN_LOCAL) @Expose val dateReturnLocal: String?,
    @SerializedName(TransferEntity.FLIGHT_NUMBER) @Expose     val flightNumber: String?,
/* ================================================== */
    @SerializedName(TransferEntity.FLIGHT_NUMBER_RETURN) @Expose    val flightNumberReturn: String?,
    @SerializedName(TransferEntity.TRANSPORT_TYPE_IDS) @Expose      val transportTypeIds: List<String>,
    @SerializedName(TransferEntity.PAX) @Expose                     val pax: Int,
    @SerializedName(TransferEntity.BOOK_NOW) @Expose                val bookNow: String?,
    @SerializedName(TransferEntity.TIME) @Expose                    val time: Int?,
    @SerializedName(TransferEntity.NAME_SIGN) @Expose               val nameSign: String?,
    @SerializedName(TransferEntity.COMMENT) @Expose                 val comment: String?,
    @SerializedName(TransferEntity.CHILD_SEATS) @Expose             val childSeats: Int,
    @SerializedName(TransferEntity.CHILD_SEATS_INFANT) @Expose      val childSeatsInfant: Int,
    @SerializedName(TransferEntity.CHILD_SEATS_CONVERTIBLE) @Expose val childSeatsConvertible: Int,
/* ================================================== */
    @SerializedName(TransferEntity.CHILD_SEATS_BOOSTER) @Expose     val childSeatsBooster: Int,
    @SerializedName(TransferEntity.PROMO_CODE) @Expose              val promoCode: String?,
    @SerializedName(TransferEntity.PASSENGER_OFFERED_PRICE) @Expose val passengerOfferedPrice: String?, // Nullable!
    @SerializedName(TransferEntity.PRICE) @Expose                   val price: MoneyModel?, // Nullable!
    @SerializedName(TransferEntity.PAID_SUM) @Expose                val paidSum: MoneyModel?,
    @SerializedName(TransferEntity.REMAINS_TO_PAY) @Expose          val remainsToPay: MoneyModel?,
    @SerializedName(TransferEntity.PAID_PERCENTAGE) @Expose         val paidPercentage: Int,
    @SerializedName(TransferEntity.WATERTAXI) @Expose               val watertaxi: Boolean,
    @SerializedName(TransferEntity.BOOK_NOW_OFFERS) @Expose         val bookNowOffers: Map<String, BookNowOfferModel>?,
    @SerializedName(TransferEntity.OFFERS_COUNT) @Expose            val offersCount: Int,
/* ================================================== */
    @SerializedName(TransferEntity.RELEVANT_CARRIERS_COUNT) @Expose val relevantCarriersCount: Int,
    @SerializedName(TransferEntity.OFFERS_UPDATED_AT) @Expose       val offersUpdatedAt: String?,
    @SerializedName(TransferEntity.DATE_REFUND) @Expose             val dateRefund: String,
    @SerializedName(TransferEntity.PAYPAL_ONLY) @Expose             val paypalOnly: Boolean?,
    @SerializedName(TransferEntity.CARRIER_MAIN_PHONE) @Expose      val carrierMainPhone: String?,
    @SerializedName(TransferEntity.PENDING_PAYMENT_ID) @Expose      val pendingPaymentId: Int?,
    @SerializedName(TransferEntity.ANALYTICS_SENT) @Expose          val analyticsSent: Boolean,
    @SerializedName(TransferEntity.RUB_PRICE) @Expose               val rubPrice: Double?,
    @SerializedName(TransferEntity.REFUNDED_PRICE) @Expose          val refundedPrice: MoneyModel?,
    @SerializedName(TransferEntity.CAMPAIGN) @Expose                val campaign: String?,
/* ================================================== */
    @SerializedName(TransferEntity.EDITABLE_FIELDS) @Expose       val editableFields: List<String>?,
    @SerializedName(TransferEntity.AIRLINE_CARD) @Expose          val airlineCard: String?,
    @SerializedName(TransferEntity.PAYMENT_PERCENTAGES) @Expose   val paymentPercentages: List<Int>?,
    @SerializedName(TransferEntity.UNREAD_MESSAGES_COUNT) @Expose val unreadMessagesCount: Int
)

data class TransfersModel(@SerializedName("transfers") @Expose val transfers: List<TransferModel>,
                          @SerializedName("pages_count") @Expose val pagesCount: Int?)

data class TransferWrapperModel(@SerializedName(TransferEntity.ENTITY_NAME) @Expose val transfer: TransferModel)

fun TransferModel.map() =
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
        transportTypeIds,
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
        bookNowOffers?.mapValues { it.map() } ?: emptyMap(),
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
        editableFields,
        airlineCard,
        paymentPercentages,
        unreadMessagesCount
    )
