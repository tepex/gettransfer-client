package com.kg.gettransfer.data.model

data class CarrierTripEntity(
    val id: Long,
    val transferId: Long,
    val from: CityPointEntity,
    var to: CityPointEntity,
    val dateLocal: String,
    val duration: Int?,
    val distance: Int,
    val time: Int,
    val childSeats: Int,
    val comment: String?,
    var waterTaxi: Boolean,
    val price: String, /* formatted, i.e "$10.00" */
    val vehicleBase: VehicleBaseEntity,
    val pax: Int?, /* passengers count */
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /* formatted */
    val remainToPay: String?, /* formatted */
    val paidPercentage: Int?,
    val passengerAccount: PassengerAccountEntity?
) {
    companion object {
        const val ENTITY_NAME       = "trip"
        const val ID                = "id"
        const val TRANSFER_ID       = "transfer_id"
        const val FROM              = "from"
        const val TO                = "to"
        const val DATE_LOCAL        = "local"
        const val DURATION          = "duration"
        const val DISTANCE          = "distance"
        const val TIME              = "time"
        const val CHILD_SEATS       = "child_seats"
        const val COMMENT           = "comment"
        const val WATER_TAXI        = "water_taxi"
        const val PRICE             = "price"
        const val VEHICLE           = "vehicle"
        const val PAX               = "pax"
        const val NAME_SIGN         = "name_sign"
        const val FLIGHT_NUMBER     = "flight_number"
        const val PAID_SUM          = "paid_sum"
        const val REMAINS_TO_PAY    = "remains_to_pay"
        const val PAID_PERCENTAGE   = "paid_percentage"
        const val PASSENGER_ACCOUNT = "passenger_account"
    }
}

data class PassengerAccountEntity(val profile: ProfileEntity, var lastSeen: String) {
    companion object {
        const val LAST_SEEN = "last_seen"
    }
}
