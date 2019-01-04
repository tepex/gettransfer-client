package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository

class RouteInteractor(private val geoRepository: GeoRepository, private val routeRepository: RouteRepository) {

    var from: GTAddress?     = null
    var to: GTAddress?       = null
    var hourlyDuration: Int? = null   //nullable to check if transfer is hourly

    //suspend fun getCurrentLocation() = geoRepository.getCurrentLocation()

    fun getCurrentAddress() = geoRepository.getCurrentAddress()

    fun getAddressByLocation(isFrom: Boolean, point: Point, pair: Pair<Point, Point>): Result<GTAddress> {
        val result = geoRepository.getAddressByLocation(point, pair)
        if (result.error != null) return result
        if (isFrom) from = result.model else to = result.model
        return result
    }

    fun isConcreteObjects() = from?.isConcreteObject() ?: false && to?.isConcreteObject() ?: false

    fun getAutocompletePredictions(prediction: String, pointsPair: Pair<Point, Point>?) =
        geoRepository.getAutocompletePredictions(prediction, pointsPair)

    fun updateDestinationPoint(): Result<Point> {
        var result = updateStartPoint()
        if (result.error != null) return result

        if (to!!.cityPoint.point == null) {
            to!!.cityPoint.placeId?.let { // GAA-479 TODO: Check this case!
                result = geoRepository.getLatLngByPlaceId(it)
                if (result.error != null) return result
                to!!.cityPoint.point = result.model
            }
        }
        return result
    }

    fun updateStartPoint(): Result<Point> {
        if (from!!.cityPoint.point == null) {
            val result = geoRepository.getLatLngByPlaceId(from!!.cityPoint.placeId!!)
            if (result.error != null) return result
            from!!.cityPoint.point = result.model
        }
        return Result(from!!.cityPoint.point!!)
    }

    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean, currency: String) =
        routeRepository.getRouteInfo(from, to, withPrices, returnWay, currency)

    fun addressFieldsNotNull() = (from != null && to != null && from != to)
}
