package com.kg.gettransfer.remote

import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.remote.mapper.LocationMapper
import com.kg.gettransfer.remote.model.LocationModel
import org.koin.standalone.get

class GeoRemoteImpl : GeoRemote {
    private val core           = get<ApiCore>()
    private val locationMapper = get<LocationMapper>()

    override suspend fun getMyLocation(): LocationEntity {
        /*return try { locationMapper.fromRemote(core.api.getMyLocation().await()) }
        catch (e: Exception) { throw core.remoteException(e) }*/

        val response: LocationModel = core.tryTwice { core.ipApi.getMyLocation() }
        return locationMapper.fromRemote(response)
    }
}