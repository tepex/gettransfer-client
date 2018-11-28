package com.kg.gettransfer.data.model

open class TransferNewEntity(val from: CityPointEntity,
                             val to: CityPointEntity?,
                             val duration: Int?,
                             val tripTo: TripEntity,
                             val tripReturn: TripEntity?,
                             val transportTypeIds: List<String>,
                             val pax: Int,
                             val childSeats: Int?,
                             val passengerOfferedPrice: Int?, // x 100 (in cents)
                             val nameSign: String?,
                             val comment: String?,
                             val user: UserEntity,
                             val promoCode: String = "")
/* В данный момент не используется
                             val paypalOnly: Boolean) */

open class TripEntity(val date: String, val time: String, val flight: String?)
