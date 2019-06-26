package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity

data class CarrierTripCached(
    val base: CarrierTripBaseCached,
    val more: CarrierTripMoreCached
)

@Entity(tableName = CarrierTripEntity.ENTITY_NAME_MORE)
data class CarrierTripMoreCached(
    @PrimaryKey @ColumnInfo(name = CarrierTripBaseEntity.ID) val id: Long,
    @ColumnInfo(name = CarrierTripEntity.PAX)                val pax: Int, /* passengers count */
    @ColumnInfo(name = CarrierTripEntity.NAME_SIGN)          val nameSign: String?,
    @ColumnInfo(name = CarrierTripEntity.FLIGHT_NUMBER)      val flightNumber: String?,
    @ColumnInfo(name = CarrierTripEntity.PAID_SUM)           val paidSum: String, /* formatted */
    @ColumnInfo(name = CarrierTripEntity.REMAINS_TO_PAY)     val remainsToPay: String, /* formatted */
    @ColumnInfo(name = CarrierTripEntity.PAID_PERCENTAGE)    val paidPercentage: Int,
    @Embedded(prefix = CarrierTripEntity.PASSENGER_ACCOUNT)  val passengerAccount: PassengerAccountCached
)

fun CarrierTripEntity.map() =
    CarrierTripCached(
        CarrierTripBaseCached(
            id,
            transferId,
            from.map(),
            to?.map(),
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
            vehicle.map()
        ),
        CarrierTripMoreCached(
            id,
            pax ?: 0,
            nameSign,
            flightNumber,
            paidSum ?: "",
            remainsToPay ?: "",
            paidPercentage ?: 0,
            @Suppress("UnsafeCallOnNullableType")
            passengerAccount!!.map()
        )
    )
