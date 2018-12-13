package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName





/* Align to line :9 */
open class TransferEntity(
        @SerialName(ID)                val id: Long,
        @SerialName(CREATED_AT)        val createdAt: String,
        @SerialName(DURATION)          val duration: Int?,
        @SerialName(DISTANCE)          val distance: Int?,
        @SerialName(STATUS)            val status: String,
        @SerialName(FROM)              val from: CityPointEntity,
        @SerialName(TO)                val to: CityPointEntity?,
        @SerialName(DATE_TO_LOCAL)     val dateToLocal: String,
        @SerialName(DATE_RETURN_LOCAL) val dateReturnLocal: String?,
        @SerialName(DATE_REFUND)       val dateRefund: String?,
/* ================================================== */
    @SerialName(NAME_SIGN)               val nameSign: String?, /* Имя на табличке, которую держит встречающий (сейчас поле full_name) */
    @SerialName(COMMENT)                 val comment: String?,
        @SerialName(MALINA_CARD)             val malinaCard: String?,
        @SerialName(FLIGHT_NUMBER)           val flightNumber: String?,
        @SerialName(FLIGHT_NUMBER_RETURN)    val flightNumberReturn: String?,
        @SerialName(PAX)                     val pax: Int,
        @SerialName(CHILD_SEATS)             val childSeats: Int,
        @SerialName(PROMO_CODE)              val promoCode: String?,
        @SerialName(OFFERS_COUNT)            val offersCount: Int,
        @SerialName(RELEVANT_CARRIERS_COUNT) val relevantCarriersCount: Int,
        @SerialName(OFFERS_UPDATED_AT)       val offersUpdatedAt: String?,
/* ================================================== */
    @SerialName(TIME)                    val time: Int,
        @SerialName(PAID_SUM)                val paidSum: MoneyEntity?,
        @SerialName(REMAINS_TO_PAY)          val remainsToPay: MoneyEntity?,
        @SerialName(PAID_PERCENTAGE)         val paidPercentage: Int,
        @SerialName(PENDING_PAYMENT_ID)      val pendingPaymentId: Int?,
        @SerialName(BOOK_NOW)                val bookNow: Boolean,
        @SerialName(BOOK_NOW_EXPIRATION)     val bookNowExpiration: String?,
        @SerialName(TRANSPORT_TYPE_IDS)      val transportTypeIds: List<String>,
        @SerialName(PASSENGER_OFFERED_PRICE) val passengerOfferedPrice: String?,
        @SerialName(PRICE)                   val price: MoneyEntity?,
/* ================================================== */
    @SerialName(PAYMENT_PERCENTAGES)     val paymentPercentages: List<Int>,
        @SerialName(EDITABLE_FIELDS)         val editableFields: List<String>
) {

    companion object {
        const val ID                      = "id"
        const val CREATED_AT              = "created_at"
        const val DURATION                = "duration"
        const val DISTANCE                = "distance"
        const val STATUS                  = "status"
        const val FROM                    = "from"
        const val TO                      = "to"
        const val DATE_TO_LOCAL           = "date_to_local"
        const val DATE_RETURN_LOCAL       = "date_return_local"
        const val DATE_REFUND             = "date_refund"

        const val NAME_SIGN               = "name_sign"
        const val COMMENT                 = "comment"
        const val MALINA_CARD             = "malina_card"
        const val FLIGHT_NUMBER           = "flight_number"
        const val FLIGHT_NUMBER_RETURN    = "flight_number_return"
        const val PAX                     = "pax"
        const val CHILD_SEATS             = "child_seats"
        const val PROMO_CODE              = "promo_code"
        const val OFFERS_COUNT            = "offers_count"
        const val RELEVANT_CARRIERS_COUNT = "relevant_carriers_count"
        const val OFFERS_UPDATED_AT       = "offers_updated_at"

        const val TIME                    = "time"
        const val PAID_SUM                = "paid_sum"
        const val REMAINS_TO_PAY          = "remains_to_pay"
        const val PAID_PERCENTAGE         = "paid_percentage"
        const val PENDING_PAYMENT_ID      = "pending_payment_id"
        const val BOOK_NOW                = "book_now"
        const val BOOK_NOW_EXPIRATION     = "book_now_expiration"
        const val TRANSPORT_TYPE_IDS      = "transport_type_ids"
        const val PASSENGER_OFFERED_PRICE = "passenger_offered_price"
        const val PRICE                   = "price"

        const val PAYMENT_PERCENTAGES     = "payment_percentages"
        const val EDITABLE_FIELDS         = "editable_fields"
    }
}
