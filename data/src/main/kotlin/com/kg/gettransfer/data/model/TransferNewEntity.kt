package com.kg.gettransfer.data.model

data class TransferNewEntity(
    val from: CityPointEntity,
    val dest: DestEntity<CityPointEntity, Int>,
    val tripTo: TripEntity,
    val tripReturn: TripEntity?,
    val transportTypeIds: List<String>,
    val pax: Int,
    val childSeats: Int?,
    val passengerOfferedPrice: Int?, // x 100 (in cents)
    val nameSign: String?,
    val comment: String?,
    val user: UserEntity,
    val promoCode: String = ""
/* В данный момент не используется
                             val paypalOnly: Boolean */
) {
    companion object {
        const val ENTITY_NAME             = "transfer"
        const val FROM                    = "from"
        const val TO                      = "to"
        const val TRIP_TO                 = "trip_to"
        const val TRANSPORT_TYPE_IDS      = "transport_type_ids"
        const val PAX                     = "pax"
        const val CHILD_SEATS             = "child_seats"
        const val PASSENGER_OFFERED_PRICE = "passenger_offered_price"
        const val NAME_SIGN               = "name_sign"
        const val COMMENT                 = "comment"
        const val PASSENGER_ACCOUNT       = "passenger_account"
        const val PROMO_CODE              = "promo_code"

        const val TRIP_RETURN             = "trip_return"
        const val DURATION                = "duration"
    }
}

data class TripEntity(
    val date: String,
    val time: String,
    val flight: String?
) {

    companion object {
        const val DATE          = "date"
        const val TIME          = "time"
        const val FLIGHT_NUMBER = "flight_number"
    }
}
