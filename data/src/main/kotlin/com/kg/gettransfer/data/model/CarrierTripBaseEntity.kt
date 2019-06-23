package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.CityPoint
import java.text.DateFormat

open class CarrierTripBaseEntity(
    open val id: Long,
    open val transferId: Long,
    open val from: CityPointEntity,
    open val to: CityPointEntity?,
    open val dateLocal: String,
    open val duration: Int?,
    open val distance: Int?,
    open val time: Int?,
    open val childSeats: Int,
    open val childSeatsInfant: Int,
    open val childSeatsConvertible: Int,
    open val childSeatsBooster: Int,
    open val comment: String?,
    open val waterTaxi: Boolean,
    open val price: String, /* formatted, i.e "$10.00" */
    open val vehicle: VehicleInfoEntity
) {

    companion object {
        const val ENTITY_NAME             = "base_trip"
        const val ID                      = "id"
        const val TRANSFER_ID             = "transfer_id"
        const val FROM                    = "from"
        const val TO                      = "to"
        const val DATE_LOCAL              = "date_local"
        const val DURATION                = "duration"
        const val DISTANCE                = "distance"
        const val TIME                    = "time"
        const val CHILD_SEATS             = "child_seats"
        const val CHILD_SEATS_INFANT      = "child_seats_infant"
        const val CHILD_SEATS_CONVERTIBLE = "child_seats_convertible"
        const val CHILD_SEATS_BOOSTER     = "child_seats_booster"
        const val COMMENT                 = "comment"
        const val WATER_TAXI              = "water_taxi"
        const val PRICE                   = "price"
        const val VEHICLE                 = "vehicle"
    }
}

fun CarrierTripBaseEntity.map(dateFormat: DateFormat) =
    CarrierTripBase(
        id,
        transferId,
        from.map(),
        to?.let { it.map() } ?: CityPoint.EMPTY,
        dateFormat.parse(dateLocal),
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
        vehicle.map()
    )
