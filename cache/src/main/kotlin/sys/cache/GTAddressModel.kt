package com.kg.gettransfer.sys.cache

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.GTAddressEntity

import kotlinx.serialization.Serializable

@Serializable
data class GTAddressModel(
    @ColumnInfo(name = GTAddressEntity.LAT) val lat: Double?,
    @ColumnInfo(name = GTAddressEntity.LON) val lon: Double?,
    @ColumnInfo(name = GTAddressEntity.ADDRESS) val address: String,
    @ColumnInfo(name = GTAddressEntity.PLACE_TYPES) val placeTypes: PlaceTypesModelList,
    @ColumnInfo(name = GTAddressEntity.VARIANT1) val variant1: String?,
    @ColumnInfo(name = GTAddressEntity.VARIANT2) val variant2: String?
)

@Serializable
data class AddressHistoryList(val list: List<GTAddressModel>)

@Serializable
data class PlaceTypesModelList(val list: List<String>)

fun GTAddressModel.map() =
    GTAddressEntity(
        lat = lat,
        lon = lon,
        address = address,
        placeTypes = placeTypes.list,
        variants = if (variant1 != null || variant2 != null) variant1 to variant2 else null
    )

fun GTAddressEntity.map() =
    GTAddressModel(
        lat = lat,
        lon = lon,
        address = address,
        placeTypes = PlaceTypesModelList(placeTypes),
        variant1 = variants?.first,
        variant2 = variants?.second
    )
