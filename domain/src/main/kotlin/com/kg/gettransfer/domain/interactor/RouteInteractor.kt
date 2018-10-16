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

    fun getAutocompletePredictions(prediction: String, pointsPair: Pair<Point, Point>?) = 
        geoRepository.getAutocompletePredictions(prediction, pointsPair)

    fun updateDestinationPoint() {
        if(from!!.cityPoint.point == null) from!!.cityPoint.point = geoRepository.getLatLngByPlaceId(from!!.cityPoint.placeId!!)
        if(to!!.cityPoint.point == null) to!!.cityPoint.point = geoRepository.getLatLngByPlaceId(to!!.cityPoint.placeId!!)
    }

    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean) = 
        routeRepository.getRouteInfo(from, to, withPrices, returnWay)
}
