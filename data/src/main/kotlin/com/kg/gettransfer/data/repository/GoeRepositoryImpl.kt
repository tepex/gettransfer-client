package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.GeoDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.LocationMapper
import com.kg.gettransfer.domain.model.Location
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.GeoRemoteRepository
import org.koin.standalone.get

class GeoRemoteRepositoryImpl(private val geoDataStoreRemote: GeoDataStoreRemote) : BaseRepository(), GeoRemoteRepository {
    private val locationMapper = get<LocationMapper>()

    override suspend fun getMyLocationByIp(): Result<Location> {
        return try {
            //factory.retrieveRemoteDataStore().changeEndpoint(EndpointEntity("", "", API_URL_LOCATION))
            val locationEntity = geoDataStoreRemote.getMyLocation()
            //factory.retrieveRemoteDataStore().changeEndpoint(preferencesCache.endpoint)
            Result(locationMapper.fromEntity(locationEntity))
        } catch (e: RemoteException) {
            Result(Location(null, null), ExceptionMapper.map(e))
        }
    }
}