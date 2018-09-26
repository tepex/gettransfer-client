package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository

class RouteInteractor(private val geoRepository: GeoRepository,
                      private val routeRepository: RouteRepository) {
    var from: GTAddress? = null
    var to: GTAddress? = null 

    suspend fun getCurrentLocation(utils: AsyncUtils): Point {
        val result = utils.asyncAwait { geoRepository.getCurrentLocation() }
        if(result.error != null) throw result.error
        return result.result as Point
    }

    fun getCurrentAddress() = geoRepository.getCurrentAddress()

    fun getAddressByLocation(point: Point): GTAddress {
        from = geoRepository.getAddressByLocation(point)
        return from!!
    }

    fun isConcreteObjects() = from?.isConcreteObject() ?: false && to?.isConcreteObject() ?: false

    fun getAutocompletePredictions(prediction: String) = geoRepository.getAutocompletePredictions(prediction)

    fun updateDestinationPoint() {
        if(to!!.point == null) to!!.point = geoRepository.getLatLngByPlaceId(to!!.id!!)
    }

    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) = 
        routeRepository.getRouteInfo(from, to, withPrices, returnWay)
}
