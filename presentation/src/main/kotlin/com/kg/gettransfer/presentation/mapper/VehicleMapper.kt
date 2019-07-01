package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Vehicle

import com.kg.gettransfer.presentation.model.VehicleModel

import org.koin.core.KoinComponent
import org.koin.core.get

open class VehicleMapper : KoinComponent {
    private val transportTypeMapper = get<TransportTypeMapper>()
    private val systemInteractor = get<SystemInteractor>()

    fun toView(type: Vehicle) =
            VehicleModel(
                    id = type.id,
                    name = type.name,
                    registrationNumber = type.registrationNumber,
                    year = type.year,
                    color = type.color,
                    transportType = transportTypeMapper.toView(type.transportType),
                    photos = type.photos.map { photo ->
                        if (photo.startsWith(systemInteractor.endpoint.url)) photo
                        else "${systemInteractor.endpoint.url}$photo"
                    }
            )
}
