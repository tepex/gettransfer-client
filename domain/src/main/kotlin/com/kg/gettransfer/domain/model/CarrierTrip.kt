package com.kg.gettransfer.domain.model

data class CarrierTrip(
    val base: CarrierTripBase,
    val pax: Int?, /*may be null only from cache*/
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /*may be null only from cache*/
    val remainsToPay: String?, /*may be null only from cache*/
    val paidPercentage: Int?, /*may be null only from cache*/
    val passengerAccount: PassengerAccount? /*may be null only from cache*/
) {

    companion object {
        val EMPTY = CarrierTrip(
            base             = CarrierTripBase.EMPTY,
            pax              = 0,
            nameSign         = null,
            flightNumber     = null,
            paidSum          = "",
            remainsToPay     = "",
            paidPercentage   = 0,
            passengerAccount = PassengerAccount.EMPTY
        )
    }
}
