package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.CarrierTripBaseEntity

@Entity(tableName = CarrierTripBaseEntity.ENTITY_NAME)
data class CarrierTripBaseCached(
        @PrimaryKey @ColumnInfo(name = CarrierTripBaseEntity.ID)          val id: Long,
        @ColumnInfo(name = CarrierTripBaseEntity.TRANSFER_ID)             val transferId: Long,
        @Embedded(prefix = CarrierTripBaseEntity.FROM)                    val from: CityPointCached,
        @Embedded(prefix = CarrierTripBaseEntity.TO)                      val to: CityPointCached?,
        @ColumnInfo(name = CarrierTripBaseEntity.DATE_LOCAL)              val dateLocal: String,
        @ColumnInfo(name = CarrierTripBaseEntity.DURATION)                val duration: Int?,
        @ColumnInfo(name = CarrierTripBaseEntity.DISTANCE)                val distance: Int?,
        @ColumnInfo(name = CarrierTripBaseEntity.TIME)                    val time: Int?,
        @ColumnInfo(name = CarrierTripBaseEntity.CHILD_SEATS)             val childSeats: Int,
        @ColumnInfo(name = CarrierTripBaseEntity.CHILD_SEATS_INFANT)      val childSeatsInfant: Int,
        @ColumnInfo(name = CarrierTripBaseEntity.CHILD_SEATS_CONVERTIBLE) val childSeatsConvertible: Int,
        @ColumnInfo(name = CarrierTripBaseEntity.CHILD_SEATS_BOOSTER)     val childSeatsBooster: Int,
        @ColumnInfo(name = CarrierTripBaseEntity.COMMENT)                 val comment: String?,
        @ColumnInfo(name = CarrierTripBaseEntity.WATER_TAXI)              val waterTaxi: Boolean,
        @ColumnInfo(name = CarrierTripBaseEntity.PRICE)                   val price: String, /* formatted, i.e "$10.00" */
        @Embedded(prefix = CarrierTripBaseEntity.VEHICLE)                 val vehicle: VehicleInfoCached
)