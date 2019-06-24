package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.GeoDataStore
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.GeoRepository
import java.util.Locale

class GeoRepositoryImpl(private val geoDataStore: GeoDataStore) : BaseRepository(), GeoRepository {

    override val isGpsEnabled: Boolean
        get() = geoDataStore.isGpsEnabled()

    override fun initGeocoder(locale: Locale) = geoDataStore.initGeocoder(locale)

    override fun initGoogleApiClient() = geoDataStore.initGoogleApiClient()

    override fun disconnectGoogleApiClient() = geoDataStore.disconnectGoogleApiClient()

    override suspend fun getCurrentLocation(): Result<Point> {
        return try {
            val locationEntity = geoDataStore.getCurrentLocation()
            Result(locationEntity.map())
        } catch (e: LocationException) {
            Result(Point.EMPTY, geoException = e.map())
        }
    }

    override suspend fun getMyLocationByIp(): Result<Point> {
        return try {
            val locationEntity = geoDataStore.getMyLocationByIp()
            Result(locationEntity.map())
        } catch (e: RemoteException) {
            Result(Point.EMPTY, e.map())
        }
    }

    override suspend fun getAddressByLocation(point: Point, lang: String): Result<GTAddress> {
        return try {
            val address = geoDataStore.getAddressByLocation(point.map())
            val result = getAutocompletePredictions(address, lang)
            if (result.error != null) Result(GTAddress.EMPTY, result.error)
            result.model.firstOrNull()?.let {
                Result(it.copy(cityPoint = it.cityPoint.copy(point = Point(point.latitude, point.longitude))))
            } ?: Result(GTAddress.EMPTY)
        } catch (e: LocationException) {
            Result(GTAddress.EMPTY, geoException = e.map())
        }
    }

    override suspend fun getAutocompletePredictions(query: String, lang: String): Result<List<GTAddress>> {
        val result = retrieveRemoteEntity { geoDataStore.getAutocompletePredictions(query, lang) }
        return if (result.error == null && !result.entity?.predictions.isNullOrEmpty()) {
            @Suppress("UnsafeCallOnNullableType")
            Result(result.entity!!.predictions!!.map { entity ->
                GTAddress(
                    CityPoint(
                        entity.description,
                        null,
                        entity.placeId
                    ),
                    entity.types ?: emptyList(),
                    entity.description,
                    GTAddress.parseAddress(entity.description)
                )
            })
        } else { // exclude such from result
            Result(emptyList(), result.error?.map())
        }
    }

    override suspend fun getPlaceDetails(placeId: String, lang: String): Result<GTAddress> {
        val result = retrieveRemoteEntity { geoDataStore.getPlaceDetails(placeId, lang) }
        return if (result.error == null && result.entity?.result != null) {
            with(result.entity.result) {
                Result(GTAddress(
                    CityPoint(
                        name,
                        location.map(),
                        placeId
                    ),
                    types,
                    "$name, $formattedAddress",
                    name to formattedAddress
                ))
            }
        } else {
            Result(GTAddress.EMPTY, result.error?.map())
        }
    }
}
