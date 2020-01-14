package com.kg.gettransfer.remote

import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.model.AutocompletePredictionsEntity
import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.data.model.PlaceDetailsResultEntity
import com.kg.gettransfer.remote.model.AutocompletePredictionsModel
import com.kg.gettransfer.remote.model.LocationModel
import com.kg.gettransfer.remote.model.PlaceDetailsResultModel
import com.kg.gettransfer.remote.model.map
import org.koin.core.get

class GeoRemoteImpl : GeoRemote {

    private val core = get<ApiCore>()

    override suspend fun getMyLocationByIp(ipAddress: String): LocationEntity {
        val response: LocationModel = core.tryTwice { core.ipApi.getMyLocation(ipAddress) }
        return response.map()
    }

    override suspend fun getAutocompletePredictions(query: String, lang: String): AutocompletePredictionsEntity {
        val response: AutocompletePredictionsModel = core.tryTwice { core.api.getAutocompletePredictions(query, lang) }
        return response.map()
    }

    override suspend fun getPlaceDetails(placeId: String, lang: String): PlaceDetailsResultEntity {
        val response: PlaceDetailsResultModel =  core.tryTwice { core.api.getPlaceDetails(placeId, lang) }
        return response.map()
    }
}
