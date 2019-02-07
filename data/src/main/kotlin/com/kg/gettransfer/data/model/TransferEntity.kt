package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName



/* Align to line :8 */
@Serializable
open class TransferEntity(
    @SerialName(ID)                      val id: Long,
    @SerialName(CREATED_AT)              val createdAt: String,
    @SerialName(DURATION)                val duration: Int?,
    @SerialName(DISTANCE)                val distance: Int?,
    @SerialName(STATUS)                  val status: String,
    @SerialName(FROM)                    val from: CityPointEntity,
    @SerialName(TO)                      val to: CityPointEntity?,
    @SerialName(DATE_TO_LOCAL)           val dateToLocal: String,
    @SerialName(DATE_RETURN_LOCAL)       val dateReturnLocal: String?,
    @SerialName(FLIGHT_NUMBER)           val flightNumber: String?,
/* ================================================== */
    @SerialName(FLIGHT_NUMBER_RETURN)    val flightNumberReturn: String?,
    @SerialName(TRANSPORT_TYPE_IDS)      val transportTypeIds: List<String>,
    @SerialName(PAX)                     val pax: Int,
    @SerialName(BOOK_NOW)                val bookNow: String?,
    @SerialName(TIME)                    val time: Int?,
    @SerialName(NAME_SIGN)               val nameSign: String?, /* Имя на табличке, которую держит встречающий (сейчас поле full_name) */
    @SerialName(COMMENT)                 val comment: String?,
    @SerialName(CHILD_SEATS)             val childSeats: Int,
    @SerialName(CHILD_SEATS_INFANT)      val childSeatsInfant: Int,
    @SerialName(CHILD_SEATS_CONVERTIBLE) val childSeatsConvertible: Int,
/* ================================================== */
    @SerialName(CHILD_SEATS_BOOSTER)     val childSeatsBooster: Int,
    @SerialName(PROMO_CODE)              val promoCode: String?,
    @SerialName(PASSENGER_OFFERED_PRICE) val passengerOfferedPrice: String?,
    @SerialName(PRICE)                   val price: MoneyEntity?,
    @SerialName(PAID_SUM)                val paidSum: MoneyEntity?,
    @SerialName(REMAINS_TO_PAY)          val remainsToPay: MoneyEntity?,
    @SerialName(PAID_PERCENTAGE)         val paidPercentage: Int,
    @SerialName(WATERTAXI)               val watertaxi: Boolean,
    @SerialName(BOOK_NOW_OFFERS)         val bookNowOffers: Map<String, BookNowOfferEntity>,
    @SerialName(OFFERS_COUNT)            val offersCount: Int,
/* ================================================== */
    @SerialName(RELEVANT_CARRIERS_COUNT) val relevantCarriersCount: Int,
    @SerialName(OFFERS_UPDATED_AT)       val offersUpdatedAt: String?,
    @SerialName(DATE_REFUND)             val dateRefund: String?,
    @SerialName(PAYPAL_ONLY)             val paypalOnly: Boolean?,
    @SerialName(CARRIER_MAIN_PHONE)      val carrierMainPhone: String?,
    @SerialName(PENDING_PAYMENT_ID)      val pendingPaymentId: Int?,
    @SerialName(ANALYTICS_SENT)          val analyticsSent: Boolean,
    @SerialName(RUB_PRICE)               val rubPrice: Double?,
    @SerialName(REFUNDED_PRICE)          val refundedPrice: MoneyEntity?,
    @SerialName(CAMPAIGN)                val campaign: String?,
/* ================================================== */
    @SerialName(EDITABLE_FIELDS)         val editableFields: List<String>,
    @SerialName(AIRLINE_CARD)            val airlineCard: String?,
    @SerialName(PAYMENT_PERCENTAGES)     val paymentPercentages: List<Int>,
    @SerialName(UNREAD_MESSAGES_COUNT)   val unreadMessagesCount: Int
) {
    companion object {
        const val ENTITY_NAME             = "transfer"
        const val ID                      = "id"
        const val CREATED_AT              = "created_at"
        const val DURATION                = "duration"
        const val DISTANCE                = "distance"
        const val STATUS                  = "status"
        const val FROM                    = "from"
        const val TO                      = "to"
        const val DATE_TO_LOCAL           = "date_to_local"
        const val DATE_RETURN_LOCAL       = "date_return_local"
        const val FLIGHT_NUMBER           = "flight_number"

        const val FLIGHT_NUMBER_RETURN    = "flight_number_return"
        const val TRANSPORT_TYPE_IDS      = "transport_type_ids"
        const val PAX                     = "pax"
        const val BOOK_NOW                = "book_now"
        const val TIME                    = "time"
        const val NAME_SIGN               = "name_sign"
        const val COMMENT                 = "comment"
        const val CHILD_SEATS             = "child_seats"
        const val CHILD_SEATS_INFANT      = "child_seats_infant"
        const val CHILD_SEATS_CONVERTIBLE = "child_seats_convertible"

        const val CHILD_SEATS_BOOSTER     = "child_seats_booster"
        const val PROMO_CODE              = "promo_code"
        const val PASSENGER_OFFERED_PRICE = "passenger_offered_price"
        const val PRICE                   = "price"
        const val PAID_SUM                = "paid_sum"
        const val REMAINS_TO_PAY          = "remains_to_pay"
        const val PAID_PERCENTAGE         = "paid_percentage"
        const val WATERTAXI               = "watertaxi"
        const val BOOK_NOW_OFFERS         = "book_now_offers"
        const val OFFERS_COUNT            = "offers_count"

        const val RELEVANT_CARRIERS_COUNT = "relevant_carriers_count"
        const val OFFERS_UPDATED_AT       = "offers_updated_at"
        const val DATE_REFUND             = "date_refund"
        const val PAYPAL_ONLY             = "paypal_only"
        const val CARRIER_MAIN_PHONE      = "carrier_main_phone"
        const val PENDING_PAYMENT_ID      = "pending_payment_id"
        const val ANALYTICS_SENT          = "analytics_sent"
        const val RUB_PRICE               = "rub_price"
        const val REFUNDED_PRICE          = "refunded_price"
        const val CAMPAIGN                = "campaign"

        const val EDITABLE_FIELDS         = "editable_fields"
        const val AIRLINE_CARD            = "airlineCard"
        const val PAYMENT_PERCENTAGES     = "payment_percentages"
        const val UNREAD_MESSAGES_COUNT   = "unread_messages_count"
    }
}
