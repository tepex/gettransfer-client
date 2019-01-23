package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.remote.model.LocationModel


class LocationMapper: EntityMapper<LocationModel, LocationEntity> {
    override fun fromRemote(type: LocationModel): LocationEntity =
            LocationEntity(type.latitude, type.longitude)

    override fun toRemote(type: LocationEntity): LocationModel {
        throw UnsupportedOperationException()
    }
}