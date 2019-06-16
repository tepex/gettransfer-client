package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.GeoDataStore
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.model.PlaceLocationMapper

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.GeoRepository

import java.util.Locale

import org.koin.standalone.get

class GeoRepositoryImpl(private val geoDataStore: GeoDataStore) : BaseRepository(), GeoRepository {
    private val locationMapper = get<LocationMapper>()
    private val placeLocationMapper = get<PlaceLocationMapper>()

    override val isGpsEnabled: Boolean
        get() = geoDataStore.isGpsEnabled()

    override fun initGeocoder(locale: Locale) = geoDataStore.initGeocoder(locale)

    override fun initGoogleApiClient() = geoDataStore.initGoogleApiClient()

    override fun disconnectGoogleApiClient() = geoDataStore.disconnectGoogleApiClient()

    override suspend fun getCurrentLocation(): Result<Point> {
        return try {
            val locationEntity = geoDataStore.getCurrentLocation()
            Result(locationMapper.fromEntity(locationEntity))
        } catch (e: LocationException) {
            Result(Point.EMPTY_POINT, geoException = ExceptionMapper.map(e))
        }
    }

    override suspend fun getMyLocationByIp(): Result<Point> {
        return try {
            val locationEntity = geoDataStore.getMyLocationByIp()
            Result(locationMapper.fromEntity(locationEntity))
        } catch (e: RemoteException) {
            Result(Point.EMPTY_POINT, ExceptionMapper.map(e))
        }
    }

    override suspend fun getAddressByLocation(point: Point, lang: String): Result<GTAddress> {
        return try {
            val address = geoDataStore.getAddressByLocation(locationMapper.toEntity(point))
            val result = getAutocompletePredictions(address, lang)
            if (result.error != null) Result(GTAddress.EMPTY, result.error)
            result.model.firstOrNull()?.let {
                Result(it.copy(cityPoint = it.cityPoint.copy(point = Point(point.latitude, point.longitude))))
            } ?: Result(GTAddress.EMPTY)
        } catch (e: LocationException) {
            Result(GTAddress.EMPTY, geoException = ExceptionMapper.map(e))
        }
    }


    override suspend fun getAutocompletePredictions(query: String, lang: String): Result<List<GTAddress>> {
        val result = retrieveRemoteEntity { geoDataStore.getAutocompletePredictions(query, lang) }
        return if (result.error == null && !result.entity?.predictions.isNullOrEmpty()) {
            Result(result.entity!!.predictions!!.map {
                GTAddress(
                    CityPoint(
                        it.description,
                        null,
                        it.placeId
                    ),
                    it.types,
                    it.description,
                    GTAddress.parseAddress(it.description)
                )
            })
        } else {// exclude such from result
            Result(emptyList(), result.error?.let { ExceptionMapper.map(it) })
        }
    }

    override suspend fun getPlaceDetails(placeId: String, lang: String): Result<GTAddress> {
        val result = retrieveRemoteEntity { geoDataStore.getPlaceDetails(placeId, lang) }
        return if (result.error == null && result.entity?.result != null) {
            with(result.entity.result) {
                Result(GTAddress(
                    CityPoint(
                        name,
                        placeLocationMapper.fromEntity(location),
                        placeId
                    ),
                    types,
                    "$name, $formattedAddress",
                    Pair(name, formattedAddress)
                ))
            }
        } else {
            Result(GTAddress.EMPTY, result.error?.let { ExceptionMapper.map(it) })
        }
    }
}
