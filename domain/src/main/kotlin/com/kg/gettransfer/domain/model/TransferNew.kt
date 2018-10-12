package com.kg.gettransfer.domain.model

data class TransferNew(val from: GTAddress,
                       val to: GTAddress,
                       val tripTo: Trip,
                       val tripReturn: Trip?,
                       val transportTypes: List<String>,
                       val pax: Int,
                       val childSeats: Int?,
                       val passengerOfferedPrice: Int?,
                       val comment: String?,
                       val user: User,
                       val promoCode: String?,
                       val paypalOnly: Boolean)
