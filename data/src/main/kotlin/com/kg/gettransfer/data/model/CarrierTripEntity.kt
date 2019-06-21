package com.kg.gettransfer.data.model

data class CarrierTripEntity(
    override val id: Long,
    override val transferId: Long,
    override val from: CityPointEntity,
    override val to: CityPointEntity?,
    override val dateLocal: String,
    override val duration: Int?,
    override val distance: Int?,
    override val time: Int?,
    override val childSeats: Int,
    override val childSeatsInfant: Int,
    override val childSeatsConvertible: Int,
    override val childSeatsBooster: Int,
    override val comment: String?,
    override val waterTaxi: Boolean,
    override val price: String,
    override val vehicle: VehicleInfoEntity,
    val pax: Int?, /* passengers count */ /*may be null only from cache*/
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /* formatted */ /*may be null only from cache*/
    val remainsToPay: String?, /* formatted */ /*may be null only from cache*/
    val paidPercentage: Int?, /*may be null only from cache*/
    val passengerAccount: PassengerAccountEntity? /*may be null only from cache*/
) : CarrierTripBaseEntity(
    id,
    transferId,
    from,
    to,
    dateLocal,
    duration,
    distance,
    time,
    childSeats,
    childSeatsInfant,
    childSeatsConvertible,
    childSeatsBooster,
    comment,
    waterTaxi,
    price,
    vehicle
) {

    companion object {
        const val ENTITY_NAME_MORE  = "more_trip"
        const val PAX               = "pax"
        const val NAME_SIGN         = "name_sign"
        const val FLIGHT_NUMBER     = "flight_number"
        const val PAID_SUM          = "paid_sum"
        const val REMAINS_TO_PAY    = "remains_to_pay"
        const val PAID_PERCENTAGE   = "paid_percentage"
        const val PASSENGER_ACCOUNT = "passenger_account"
    }
}
