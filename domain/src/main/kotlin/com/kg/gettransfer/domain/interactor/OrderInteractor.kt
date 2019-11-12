package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.RouteInfoHourlyRequest
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.RouteRepository
import com.kg.gettransfer.domain.repository.SessionRepository

import java.util.Date

import kotlin.math.absoluteValue

class OrderInteractor(
    private val geoRepository: GeoRepository,
    private val routeRepository: RouteRepository,
    private val sessionRepository: SessionRepository
) {

    var from: GTAddress?       = null
    var to: GTAddress?         = null
    var hourlyDuration: Int?   = null   // nullable to check if transfer is hourly
    var duration: Int?         = null
    var orderStartTime: Date?  = null
    var orderReturnTime: Date? = null
    var dropfOff: Boolean      = false

    var offeredPrice: Double? = null
    var passengers: Int = DEFAULT_PASSENGERS
    var promoCode = ""
    var flightNumber: String? = null
    var flightNumberReturn: String? = null
    var comment: String? = null
    var selectedTransports: Set<TransportType.ID>? = null
    var nameSign: String? = null
    var isLoggedIn = false

    var noPointPlaces: List<GTAddress> = emptyList()

    private fun clearSelectedFields() {
        offeredPrice = null
        passengers = DEFAULT_PASSENGERS
        promoCode = ""
        flightNumber = null
        flightNumberReturn = null
        comment = null
        selectedTransports = null
        nameSign = null
    }

    fun clear() {
        to              = null
        hourlyDuration  = null
        duration        = null
        orderStartTime  = null
        orderReturnTime = null
        dropfOff        = false

        clearSelectedFields()
    }

    suspend fun getAddressByLocation(isFrom: Boolean, point: Point): Result<GTAddress> {
        val gtAddress = geoRepository.getAddressByLocation(point, sessionRepository.account.locale.language)

        if (gtAddress.error != null) return gtAddress
        if (isFrom) from = gtAddress.model else to = gtAddress.model
        return gtAddress
    }

    suspend fun getAutoCompletePredictions(prediction: String): Result<List<GTAddress>> {
        val result = geoRepository.getAutocompletePredictions(prediction, sessionRepository.account.locale.language)
        return if (result.error == null && !result.model.isNullOrEmpty()) {
            // if result has addresses without placeId, exclude such from result
            val addresses = result.model.filter {
                noPointPlaces.none { n -> it.cityPoint.placeId == n.cityPoint.placeId }
            }
            Result(addresses)
        } else {
            Result(emptyList(), result.error)
        }
    }

    suspend fun updatePoint(isTo: Boolean, placeId: String): Result<GTAddress> {
        val result = geoRepository.getPlaceDetails(placeId, sessionRepository.account.locale.language)
        return if (result.error == null) {
            if (isTo) to = result.model else from = result.model
            Result(result.model)
        } else {
            Result(GTAddress.EMPTY, result.error)
        }
    }

    suspend fun getRouteInfo(request: RouteInfoRequest): Result<RouteInfo> {
        val routeInfo = routeRepository.getRouteInfo(request)
        duration = routeInfo.model.duration
        return routeInfo
    }

    suspend fun getRouteInfoHourlyTransfer(request: RouteInfoHourlyRequest): Result<RouteInfo> {
        val routeInfo = routeRepository.getRouteInfo(request)
        duration = routeInfo.model.duration
        return routeInfo
    }

    fun isAddressesValid() = from != null && (to != null || hourlyDuration != null)

    fun isCanCreateOrder() =
        from?.cityPoint != null && (to?.cityPoint != null || hourlyDuration != null)

    companion object {
        const val DEFAULT_PASSENGERS = 2
    }
}
