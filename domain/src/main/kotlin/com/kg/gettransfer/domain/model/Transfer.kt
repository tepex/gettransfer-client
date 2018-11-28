package com.kg.gettransfer.domain.model

import java.util.Date






/* Align to line:10 */
data class Transfer(val id: Long,
                    val createdAt: Date,
                    val duration: Int?,
                    val distance: Int?,
                    var status: String,
                    val from: CityPoint,
                    val to: CityPoint?,
                    val dateToLocal: Date,
                    val dateReturnLocal: Date?,
                    val dateRefund: Date?,

                    val nameSign: String?,
                    val comment: String?,
                    val malinaCard: String?,
                    val flightNumber: String?,
                    val flightNumberReturn: String?,
                    val pax: Int,
                    val childSeats: Int,
                    val offersCount: Int,
                    val relevantCarriersCount: Int,
                    val offersUpdatedAt: Date?,

                    val time: Int,
                    val paidSum: Money?,
                    val remainsToPay: Money?,
                    val paidPercentage: Int,
                    val pendingPaymentId: Int?,
                    val bookNow: Boolean,
                    val bookNowExpiration: String?,
                    val transportTypeIds: List<String>,
                    val passengerOfferedPrice: String?,
                    val price: Money?,

                    val editableFields: List<String>) {
    companion object {
        @JvmField val STATUS_NEW           = "new"
        @JvmField val STATUS_PERFORMED     = "performed"
        @JvmField val STATUS_COMPLETED     = "completed"
        @JvmField val STATUS_CANCELED      = "canceled"
        @JvmField val STATUS_NOT_COMPLETED = "not_completed"
        @JvmField val STATUS_REJECTED      = "rejected"
        @JvmField val STATUS_DRAFT         = "draft"
        @JvmField val STATUS_PENDING       = "pending_confirmation"
        @JvmField val STATUS_OUTDATED      = "outdated"

        @JvmField val STATUS_CATEGORY_ACTIVE     = "active_status"
        @JvmField val STATUS_CATEGORY_CONFIRMED  = "confirmed_status"
        @JvmField val STATUS_CATEGORY_UNFINISHED = "unfinished_status"
        @JvmField val STATUS_CATEGORY_FINISHED   = "finished_status"
    }

    val checkOffers = status == STATUS_PERFORMED ||
                      status == STATUS_PENDING ||
                      status == STATUS_COMPLETED ||
                      status == STATUS_NOT_COMPLETED

    fun checkStatusCategory(): String {
        return when(status) {
            STATUS_NEW           -> STATUS_CATEGORY_ACTIVE
            STATUS_DRAFT         -> STATUS_CATEGORY_ACTIVE
            STATUS_PERFORMED     -> STATUS_CATEGORY_CONFIRMED
            STATUS_OUTDATED      -> STATUS_CATEGORY_UNFINISHED
            STATUS_CANCELED      -> STATUS_CATEGORY_UNFINISHED
            STATUS_REJECTED      -> STATUS_CATEGORY_UNFINISHED
            else                 -> STATUS_CATEGORY_FINISHED
        }
    }
}
