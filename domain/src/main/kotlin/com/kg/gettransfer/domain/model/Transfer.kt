package com.kg.gettransfer.domain.model

import java.util.Date





/* Align to line:9 */
data class Transfer(
    override val id: Long,
    val createdAt: Date,
    val duration: Int?,
    val distance: Int?,
    var status: Status,
    val from: CityPoint,
    val to: CityPoint?,
    val dateToLocal: Date,
    val dateReturnLocal: Date?,
    val flightNumber: String?,
/* ================================================== */
    val flightNumberReturn: String?,
    val transportTypeIds: List<TransportType.ID>,
    val pax: Int,
    val bookNow: TransportType.ID?,
    val time: Int?,
    val nameSign: String?,
    val comment: String?,
    val childSeats: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
/* ================================================== */
    val childSeatsBooster: Int,
    val promoCode: String?,
    val passengerOfferedPrice: String?,
    val price: Money?,
    val paidSum: Money?,
    val remainsToPay: Money?,
    val paidPercentage: Int,
    val watertaxi: Boolean,
    val bookNowOffers: Map<TransportType.ID, BookNowOffer>,
    val offersCount: Int,
/* ================================================== */
    val relevantCarriersCount: Int,
    /* offersUpdatedAt */
    val dateRefund: Date?,
    val paypalOnly: Boolean?,
    val carrierMainPhone: String?,
    val pendingPaymentId: Int?,
    val analyticsSent: Boolean,
    val rubPrice: Double?,
    val refundedPrice: Money?,
    val campaign: String?,
/* ================================================== */
    val editableFields: List<String>?, /* not used */
    val airlineCard: String?,
    val paymentPercentages: List<Int>?,
    val unreadMessagesCount: Int
) : Entity() {

    fun checkStatusCategory() = when (status) {
        Status.NEW       -> STATUS_CATEGORY_ACTIVE
        Status.DRAFT     -> STATUS_CATEGORY_ACTIVE
        Status.PERFORMED -> STATUS_CATEGORY_CONFIRMED
        Status.OUTDATED  -> STATUS_CATEGORY_UNFINISHED
        Status.CANCELED  -> STATUS_CATEGORY_UNFINISHED
        Status.REJECTED  -> STATUS_CATEGORY_UNFINISHED
        else             -> STATUS_CATEGORY_FINISHED
    }

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

    fun isCompletedTransfer() = status == Status.NOT_COMPLETED || status == Status.COMPLETED

    companion object {
        const val STATUS_CATEGORY_ACTIVE     = "active_status"
        const val STATUS_CATEGORY_CONFIRMED  = "confirmed_status"
        const val STATUS_CATEGORY_UNFINISHED = "unfinished_status"
        const val STATUS_CATEGORY_FINISHED   = "finished_status"

        fun List<Transfer>.filterActive() = filter {
            it.status == Status.NEW ||
            it.status == Status.DRAFT ||
            it.status == Status.PERFORMED
        }

        fun List<Transfer>.filterCompleted() = filter {
            it.status == Status.COMPLETED || it.status == Status.NOT_COMPLETED
        }

        fun List<Transfer>.filterArchived() = filter {
            it.status != Status.COMPLETED || it.status != Status.NOT_COMPLETED
        }
    }
}
