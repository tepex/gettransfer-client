package com.kg.gettransfer.remote.model

import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.AddressComponentEntity
import com.kg.gettransfer.data.model.PlaceDetailsEntity
import com.kg.gettransfer.data.model.PlaceDetailsResultEntity
import com.kg.gettransfer.data.model.PlaceLocationEntity

data class PlaceDetailsResultModel(
    @SerializedName(PlaceDetailsResultEntity.RESULT) val result: PlaceDetailsModel?
)

data class PlaceDetailsModel(
    @SerializedName(PlaceDetailsEntity.ADDRESS_COMPONENTS)   val addressComponents: List<AddressComponentModel>,
    @SerializedName(PlaceDetailsEntity.GT_FORMATTED_ADDRESS) val formattedAddress: String,
    @SerializedName(PlaceDetailsEntity.LOCATION)             val location: PlaceLocationModel,
    @SerializedName(PlaceDetailsEntity.NAME)                 val name: String,
    @SerializedName(PlaceDetailsEntity.TYPES)                val types: List<String>
)

data class AddressComponentModel(
    @SerializedName(AddressComponentEntity.LONG_NAME)  val longName: String,
    @SerializedName(AddressComponentEntity.SHORT_NAME) val shortName: String,
    @SerializedName(AddressComponentEntity.TYPES)      val types: List<String>
)

data class PlaceLocationModel(
    @SerializedName(PlaceLocationEntity.LAT) val lat: Double,
    @SerializedName(PlaceLocationEntity.LNG) val lng: Double
)

fun PlaceLocationModel.map() = PlaceLocationEntity(lat, lng)

fun AddressComponentModel.map() = AddressComponentEntity(longName, shortName, types)

fun PlaceDetailsModel.map() =
    PlaceDetailsEntity(
        addressComponents.map { it.map() },
        formattedAddress,
        location.map(),
        name,
        types
    )

fun PlaceDetailsResultModel.map() = PlaceDetailsResultEntity(result?.let { it.map() })
