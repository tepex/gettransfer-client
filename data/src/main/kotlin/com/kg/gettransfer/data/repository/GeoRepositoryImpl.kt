package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.GeoDataStore
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.model.PlaceLocationMapper
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.GeoRepository
import org.koin.standalone.get
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GeoRepositoryImpl(private val geoDataStore: GeoDataStore) : BaseRepository(), GeoRepository {
    private val locationMapper = get<LocationMapper>()
    private val placeLocationMapper = get<PlaceLocationMapper>()

    override val isGpsEnabled: Boolean
        get() = geoDataStore.isGpsEnabled()

    override fun initGeocoder(locale: Locale) = geoDataStore.initGeocoder(locale)

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
            val gtAddress = result.model.firstOrNull()
            if (gtAddress != null) {
                gtAddress.cityPoint.point = Point(point.latitude, point.longitude)
                Result(gtAddress)
            } else Result(GTAddress.EMPTY, ApiException(ApiException.NOT_FOUND, "List is empty"))
        } catch (e: LocationException) {
            Result(GTAddress.EMPTY, geoException = ExceptionMapper.map(e))
        }
    }


    override suspend fun getAutocompletePredictions(query: String, lang: String): Result<List<GTAddress>> {
        val result = retrieveRemoteEntity { geoDataStore.getAutocompletePredictions(query, lang) }
        if (result.error == null && !result.entity?.predictions.isNullOrEmpty()) {
            val addresses = result.entity!!.predictions!!.map {
                GTAddress(
                    CityPoint(
                        it.description,
                        null,
                        it.placeId
                    ),
                    it.types,
                    it.description,
                    null,
                    null
                ).apply { setPrimaryAndSecondary() }
            }
            return Result(addresses)
        } else {// exclude such from result
            return Result(emptyList(), result.error?.let { ExceptionMapper.map(it) })
        }
    }

    override suspend fun getPlaceDetails(placeId: String, lang: String): Result<GTAddress> {
        val result = retrieveRemoteEntity { geoDataStore.getPlaceDetails(placeId, lang) }
        return if (result.error == null && result.entity?.result != null) {
            result.entity.result.let {
                val gtAddress =
                    GTAddress(
                        CityPoint(
                            it.name,
                            placeLocationMapper.fromEntity(it.location),
                            placeId
                        ),
                        it.types,
                        it.name.plus(", ").plus(it.formattedAddress),
                        it.name,
                        it.formattedAddress
                    )
                Result(gtAddress)
            }
        } else {
            Result(GTAddress.EMPTY, result.error?.let { ExceptionMapper.map(it) })
        }
    }
}