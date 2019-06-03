package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository
import com.kg.gettransfer.domain.repository.SessionRepository
import com.sun.jndi.cosnaming.ExceptionMapper
import java.util.Date
import kotlin.math.absoluteValue

class OrderInteractor(
        private val geoRepository: GeoRepository,
        private val routeRepository: RouteRepository,
        private val sessionRepository: SessionRepository) {

    var from: GTAddress?       = null
    var to: GTAddress?         = null
    var hourlyDuration: Int?   = null   //nullable to check if transfer is hourly
    var duration: Int?         = null
    var orderStartTime: Date?  = null
    var orderReturnTime: Date? = null

    var offeredPrice: Double? = null
    var passengers: Int = MIN_PASSENGERS
    var promoCode = ""
    var flightNumber: String? = null
    var flightNumberReturn: String? = null
    var comment: String? = null
    var selectedTransports: Set<TransportType.ID>? = null
    var nameSign: String? = null
    var isLoggedIn = false

    var noPointPlaces: List<GTAddress> = emptyList()

    fun clearSelectedFields() {
        offeredPrice = null
        passengers = MIN_PASSENGERS
        promoCode = ""
        flightNumber = null
        flightNumberReturn = null
        comment = null
        selectedTransports = null
        nameSign = null
    }

    suspend fun getAddressByLocation(isFrom: Boolean, point: Point): Result<GTAddress> {
        val gtAddress = geoRepository.getAddressByLocation(Location(point.latitude, point.longitude), sessionRepository.account.locale.language)

        if (gtAddress.error != null) return gtAddress
        if (isFrom) from = gtAddress.model else to = gtAddress.model
        return gtAddress
    }

    /*fun getAutocompletePredictions(prediction: String, pointsPair: Pair<Point, Point>?) =
            Result(geoRepository.
                    getAutocompletePredictions(prediction, pointsPair)
                    .model.filter { noPointPlaces.none { n -> it.cityPoint.placeId == n.cityPoint.placeId }})*/ // if result has addresses without placeId,
                                                                                                              // exclude such from result

    suspend fun getAutoCompletePredictions(prediction: String): Result<List<GTAddress>> {
        val result = geoRepository.getAutocompletePredictions(prediction, sessionRepository.account.locale.language)
        return if (result.error == null && !result.model.isNullOrEmpty()) {
            val addresses = result.model
                    .filter { noPointPlaces.none { n -> it.cityPoint.placeId == n.cityPoint.placeId }} // if result has addresses without placeId,
            Result(addresses)                                                                   // exclude such from result
        } else Result(emptyList(), result.error)
    }

    suspend fun updatePoint(isTo: Boolean, placeId: String): Result<GTAddress> {
        val result = geoRepository.getPlaceDetails(placeId, sessionRepository.account.locale.language)
        return if (result.error == null) {
            if (isTo) to = result.model
            else from = result.model
            Result(result.model)
        } else Result(GTAddress.EMPTY, result.error)
    }

    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean, currency: String, dateTime: Date? = null): Result<RouteInfo> {
        val routeInfo = routeRepository.getRouteInfo(from, to, withPrices, returnWay, currency, dateTime)
        duration = routeInfo.model.duration
        return routeInfo
    }

    suspend fun getRouteInfoHourlyTransfer(from: Point, hourlyDuration: Int, currency: String, dateTime: Date? = null): Result<RouteInfo> {
        val routeInfo = routeRepository.getRouteInfo(from, hourlyDuration, currency, dateTime)
        duration = routeInfo.model.duration
        return routeInfo
    }

    fun isAddressesValid() =
            (from != null && (to != null || hourlyDuration != null ))

    fun isDistanceFine() =
            if (from!!.cityPoint.point != null && to!!.cityPoint.point != null)
                (from!!.lat!! - to!!.lat!!).absoluteValue > MIN_LAT_DIFF ||
                        (from!!.lon!! - to!!.lon!!).absoluteValue > MIN_LON_DIFF
    else false
    //0.002 lat
    //0.003 lon

    fun isCanCreateOrder() = (from?.cityPoint != null &&
            ((to?.cityPoint != null && isDistanceFine()) || hourlyDuration != null))

    companion object {
        const val MIN_LAT_DIFF = 0.002
        const val MIN_LON_DIFF = 0.003
        const val MIN_PASSENGERS = 0
    }
}
