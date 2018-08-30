package com.kg.gettransfer.domain.model

data class Transfer(val id: Int,
                    val createdAt: String,
                    val duration: Int?,
                    val distance: Int?,
                    val status: String,
                    val from: CityPoint,
                    val to: CityPoint?,
                    val dateToLocal: String,
                    val dateReturnLocal: String?,
                    val dateRefund: String,
                    val nameSign: String?,
                    val comment: String?,
                    val malinaCard: String?,
                    val flightNumber: String?,
                    val flightNumberReturn: String?,
                    val pax: Int,
                    val childSeats: Int,
                    val offersCount: Int,
                    val relevantCarriersCount: Int,
                    val offersUpdatedAt: String?,
                    val time: Int,
                    val paidSum: Money,
                    val remainsToPay: Money,
                    val paidPercentage: Int,
                    val pendingPaymentId: Int?,
                    val bookNow: Boolean,
                    val bookNowExpiration: String?,
                    val transportTypeIds: List<String>,
                    val passengerOfferedPrice: String?,
                    val price: Money?,
                    val editableFields: List<String>)

data class Money(val default: String?, val preferred: String?)

data class CityPoint(val name: String?, val point: String?, val placeId: String?)