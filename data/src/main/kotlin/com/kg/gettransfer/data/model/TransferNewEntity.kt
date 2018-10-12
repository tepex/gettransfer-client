package com.kg.gettransfer.data.model

open class TransferNewEntity(val from: CityPointEntity,
                             val to: CityPointEntity,
                             val tripTo: TripEntity,
                             val tripReturn: TripEntity?,
                             val transportTypeIds: List<String>,
                             val pax: Int,
                             val childSeats: Int?,
                             val passengerOfferedPrice: String?,
                             val nameSign: String,
                             val comment: String?,
                             val profile: ProfileEntity,
                             val promoCode: String?)
/* В данный момент не используется
                             val paypalOnly: Boolean) */

open class TripEntity(val date: String, val time: String, val flight: String?)
