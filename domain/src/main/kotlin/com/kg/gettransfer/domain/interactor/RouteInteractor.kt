package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.GeoRepository

class RouteInteractor(private val repository: GeoRepository) {
    var from: GTAddress? = null
    var to: GTAddress? = null 

    suspend fun getCurrentLocation(utils: AsyncUtils): Point {
        val result = utils.asyncAwait { repository.getCurrentLocation() }
        if(result.error != null) throw result.error
        return result.result as Point
    }

    fun getCurrentAddress() = repository.getCurrentAddress()

    fun getAddressByLocation(point: Point): GTAddress {
        from = repository.getAddressByLocation(point)
        return from!!
    }

    fun isConcreteObjects() = from?.isConcreteObject() ?: false && to?.isConcreteObject() ?: false

    fun getAutocompletePredictions(prediction: String) = repository.getAutocompletePredictions(prediction)

    fun updateDestinationPoint() {
        if(to!!.point == null) to!!.point = repository.getLatLngByPlaceId(to!!.id!!)
    }
}
