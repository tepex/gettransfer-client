package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.VehicleEntity

data class VehicleCached(
    @PrimaryKey @ColumnInfo(name = VehicleEntity.ID) val id: Long,
    @ColumnInfo(name = VehicleEntity.NAME) val name: String,
    @ColumnInfo(name = VehicleEntity.MODEL) val model: String,
    @ColumnInfo(name = VehicleEntity.REGISTRATION_NUMBER) val registrationNumber: String?,
    @ColumnInfo(name = VehicleEntity.YEAR) val year: Int,
    @ColumnInfo(name = VehicleEntity.COLOR) val color: String?,
    /* Dirty hack. Splitting TransportType.id and Vehicle.id */
    @ColumnInfo(name = VehicleEntity.TRANSPORT_TYPE_ID) val transportTypeId: String,
    @ColumnInfo(name = TransportTypeEntity.PAX_MAX) val paxMax: Int,
    @ColumnInfo(name = TransportTypeEntity.LUGGAGE_MAX) val luggageMax: Int,
    @ColumnInfo(name = VehicleEntity.PHOTOS) val photos: StringList
)

fun VehicleCached.map() =
    VehicleEntity(
        id,
        name,
        model,
        registrationNumber,
        year,
        color,
        transportTypeId,
        paxMax,
        luggageMax,
        photos.list
    )

fun VehicleEntity.map() =
    VehicleCached(
        id,
        name,
        model,
        registrationNumber,
        year,
        color,
        transportTypeId,
        paxMax,
        luggageMax,
        StringList(photos)
    )
