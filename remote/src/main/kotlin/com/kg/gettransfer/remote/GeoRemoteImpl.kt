package com.kg.gettransfer.remote

import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.model.AutocompletePredictionsEntity
import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.data.model.PlaceDetailsResultEntity
import com.kg.gettransfer.remote.mapper.AutocompletePredictionsMapper
import com.kg.gettransfer.remote.mapper.LocationMapper
import com.kg.gettransfer.remote.mapper.PlaceDetailsResultMapper
import com.kg.gettransfer.remote.model.AutocompletePredictionsModel
import com.kg.gettransfer.remote.model.LocationModel
import com.kg.gettransfer.remote.model.PlaceDetailsResultModel
import org.koin.standalone.get

class GeoRemoteImpl : GeoRemote {
    private val core           = get<ApiCore>()
    private val locationMapper = get<LocationMapper>()
    private val autocompletePredictionsMapper = get<AutocompletePredictionsMapper>()
    private val placeDetailsResultMapper = get<PlaceDetailsResultMapper>()

    override suspend fun getMyLocationByIp(): LocationEntity {
        /*return try { locationMapper.fromRemote(core.api.getMyLocationByIp().await()) }
        catch (e: Exception) { throw core.remoteException(e) }*/

        val response: LocationModel = core.tryTwice { core.ipApi.getMyLocation() }
        return locationMapper.fromRemote(response)
    }

    override suspend fun getAutocompletePredictions(query: String, lang: String): AutocompletePredictionsEntity {
        val response: AutocompletePredictionsModel = core.tryTwice { core.api.getAutocompletePredictions(query, lang) }
        return autocompletePredictionsMapper.fromRemote(response)
    }

    override suspend fun getPlaceDetails(placeId: String, lang: String): PlaceDetailsResultEntity {
        val response: PlaceDetailsResultModel =  core.tryTwice { core.api.getPlaceDetails(placeId, lang) }
        return placeDetailsResultMapper.fromRemote(response)
    }
}