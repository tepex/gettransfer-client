package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository

import java.io.IOException

class RouteInteractor(private val geoRepository: GeoRepository,
                      private val routeRepository: RouteRepository) {
    var from: GTAddress? = null
    var to: GTAddress? = null

    //suspend fun getCurrentLocation() = geoRepository.getCurrentLocation()

    fun getCurrentAddress() = geoRepository.getCurrentAddress()

    fun getAddressByLocation(isFrom: Boolean, point: Point, pair: Pair<Point, Point>): Result<GTAddress> {
        var result = geoRepository.getAddressByLocation(point, pair)
        if(result.error != null) return result
        if(isFrom) from = result.model else to = result.model
        return result
    }

    fun isConcreteObjects() = from?.isConcreteObject() ?: false && to?.isConcreteObject() ?: false

    fun getAutocompletePredictions(prediction: String, pointsPair: Pair<Point, Point>?) = 
        geoRepository.getAutocompletePredictions(prediction, pointsPair)

    fun updateDestinationPoint() {
        if(from!!.cityPoint.point == null) from!!.cityPoint.point = geoRepository.getLatLngByPlaceId(from!!.cityPoint.placeId!!)
        if(to!!.cityPoint.point == null) to!!.cityPoint.point = geoRepository.getLatLngByPlaceId(to!!.cityPoint.placeId!!)
    }

    fun updateStartPoint() {
        if(from!!.cityPoint.point == null) from!!.cityPoint.point = geoRepository.getLatLngByPlaceId(from!!.cityPoint.placeId!!)
    }

    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean) =
        routeRepository.getRouteInfo(from, to, withPrices, returnWay)

    fun addressFieldsNotNull(): Boolean = (from != null && to != null && from != to)
}
