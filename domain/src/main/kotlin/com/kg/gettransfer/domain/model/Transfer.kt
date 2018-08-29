package com.kg.gettransfer.domain.model

data class Transfer(val id: Long,
                       val createdAt: String,
                       val duration: Int?,
                       val distance: Int?,
                       val status: String,
                       val from: ApiCityPoint,
                       val to: ApiCityPoint?,
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
                       val paidSum: ApiMoney,
                       val remainsToPay: ApiMoney,
                       val paidPercentage: Int,
                       val pendingPaymentId: Long?,
                       val bookNow: Boolean,
                       val bookNowExpiration: String?,
                       val transportTypeIds: List<String>,
                       val passengerOfferedPrice: String,
                       val price: ApiMoney,
                       val editableFields: List<String>)

data class ApiMoney(val default: String, val preferred: String?)

data class ApiCityPoint(val name: String, val point: String, val placeId: String)