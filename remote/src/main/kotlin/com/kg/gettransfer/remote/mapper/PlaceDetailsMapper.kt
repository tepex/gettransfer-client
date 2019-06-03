package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AddressComponentEntity
import com.kg.gettransfer.data.model.PlaceDetailsEntity
import com.kg.gettransfer.data.model.PlaceDetailsResultEntity
import com.kg.gettransfer.data.model.PlaceLocationEntity
import com.kg.gettransfer.remote.model.AddressComponentModel
import com.kg.gettransfer.remote.model.PlaceDetailsModel
import com.kg.gettransfer.remote.model.PlaceDetailsResultModel
import com.kg.gettransfer.remote.model.PlaceLocationModel
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

open class PlaceDetailsResultMapper : EntityMapper<PlaceDetailsResultModel, PlaceDetailsResultEntity> {
    private val placeDetailsMapper = get<PlaceDetailsMapper>()

    override fun fromRemote(type: PlaceDetailsResultModel) =
        PlaceDetailsResultEntity(
            result = type.result?.let { placeDetailsMapper.fromRemote(it) }
        )

    override fun toRemote(type: PlaceDetailsResultEntity): PlaceDetailsResultModel {
        throw UnsupportedOperationException()
    }
}


open class PlaceDetailsMapper : EntityMapper<PlaceDetailsModel, PlaceDetailsEntity> {
    private val addressComponentMapper = get<AddressComponentMapper>()
    private val placeLocationMapper = get<PlaceLocationMapper>()

    override fun fromRemote(type: PlaceDetailsModel) =
        PlaceDetailsEntity(
            addressComponents = type.addressComponents.map { addressComponentMapper.fromRemote(it) },
            formattedAddress = type.formattedAddress,
            location = placeLocationMapper.fromRemote(type.location),
            name = type.name,
            types = type.types
        )

    override fun toRemote(type: PlaceDetailsEntity): PlaceDetailsModel {
        throw UnsupportedOperationException()
    }
}


open class AddressComponentMapper : EntityMapper<AddressComponentModel, AddressComponentEntity> {
    override fun fromRemote(type: AddressComponentModel) =
        AddressComponentEntity(
            longName = type.longName,
            shortName = type.shortName,
            types = type.types
        )

    override fun toRemote(type: AddressComponentEntity): AddressComponentModel {
        throw UnsupportedOperationException()
    }
}


open class PlaceLocationMapper : EntityMapper<PlaceLocationModel, PlaceLocationEntity> {
    override fun fromRemote(type: PlaceLocationModel) =
        PlaceLocationEntity(
            lat = type.lat,
            lng = type.lng
        )

    override fun toRemote(type: PlaceLocationEntity): PlaceLocationModel {
        throw UnsupportedOperationException()
    }
}