package com.kg.gettransfer.domain.model

import java.util.Date





/* Align to line:9 */
data class Transfer(
    val id: Long,
    val createdAt: Date,
    val duration: Int?,
    val distance: Int?,
    var status: Status,
    val from: CityPoint,
    val to: CityPoint?,
    val dateToLocal: Date,
    val dateReturnLocal: Date?,
    val dateRefund: Date?,
/* ================================================== */
    val nameSign: String?,
    val comment: String?,
    val malinaCard: String?,
    val flightNumber: String?,
    val flightNumberReturn: String?,
    val pax: Int,
    val childSeats: Int,
    val offersCount: Int,
    val relevantCarriersCount: Int,
    /* offersUpdatedAt */
/* ================================================== */
    val time: Int?,
    val paidSum: Money?,
    val remainsToPay: Money?,
    val paidPercentage: Int,
    val pendingPaymentId: Int?,
    val bookNow: Boolean,
    val bookNowExpiration: String?,
    val transportTypeIds: List<String>,
    val passengerOfferedPrice: String?,
    val price: Money?,
/* ================================================== */
    val paymentPercentages: List<Int>,
    val editableFields: List<String> /* not used */
) {

    enum class Status(val checkOffers: Boolean) {
        NEW(false),
        PERFORMED(true),
        COMPLETED(true),
        CANCELED(false),
        NOT_COMPLETED(true),
        REJECTED(false),
        DRAFT(false),
        PENDING_CONFIRMATION(true),
        OUTDATED(false);
    }


    companion object {
        @JvmField val STATUS_PENDING_ID_IN_RES      = "pending"
        @JvmField val STATUS_NOTCOMPLETED_ID_IN_RES = "notcomleted"

        @JvmField val STATUS_CATEGORY_ACTIVE     = "active_status"
        @JvmField val STATUS_CATEGORY_CONFIRMED  = "confirmed_status"
        @JvmField val STATUS_CATEGORY_UNFINISHED = "unfinished_status"
        @JvmField val STATUS_CATEGORY_FINISHED   = "finished_status"
    }

    fun checkStatusCategory(): String {
        return when (status) {
            Status.NEW       -> STATUS_CATEGORY_ACTIVE
            Status.DRAFT     -> STATUS_CATEGORY_ACTIVE
            Status.PERFORMED -> STATUS_CATEGORY_CONFIRMED
            Status.OUTDATED  -> STATUS_CATEGORY_UNFINISHED
            Status.CANCELED  -> STATUS_CATEGORY_UNFINISHED
            Status.REJECTED  -> STATUS_CATEGORY_UNFINISHED
            else             -> STATUS_CATEGORY_FINISHED
        }
    }
}
