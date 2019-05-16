package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository
import java.util.Date
import kotlin.math.absoluteValue

class OrderInteractor(private val geoRepository: GeoRepository, private val routeRepository: RouteRepository) {

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
    var user = User(Profile(null, null, null), false)
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
        setNewUser(null)
        nameSign = null
    }

    fun setNewUser(newUser: User?) {
        newUser?.let {
            user = it
            isLoggedIn = true
            return
        }
        user = User(Profile(null, null, null), false)
        isLoggedIn = false
    }

    fun getCurrentAddress() = geoRepository.getCurrentAddress()

    fun getAddressByLocation(isFrom: Boolean, point: Point, pair: Pair<Point, Point>): Result<GTAddress> {
        val result = geoRepository.getAddressByLocation(point, pair)
        if (result.error != null) return result
        if (isFrom) from = result.model else to = result.model
        return result
    }

    fun getAutocompletePredictions(prediction: String, pointsPair: Pair<Point, Point>?) =
            Result(geoRepository.
                    getAutocompletePredictions(prediction, pointsPair)
                    .model.filter { noPointPlaces.none { n -> it.cityPoint.placeId == n.cityPoint.placeId }}) // if result has addresses without point,
                                                                                                              // exclude such from result

    fun updatePoint(isTo: Boolean): Result<Point> {
        (if (isTo) to else from).let {
            return if (it!!.cityPoint.point == null)
                geoRepository
                        .getLatLngByPlaceId(it.cityPoint.placeId!!)
                        .also { pointResult ->
                            it.cityPoint.point = pointResult.model
                        }
            else Result(it.cityPoint.point!!)
        }
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
