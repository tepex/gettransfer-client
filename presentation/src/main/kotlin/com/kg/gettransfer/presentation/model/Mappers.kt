package com.kg.gettransfer.presentation.model

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.*

import java.util.Currency
import java.util.Locale

object Mappers {
    fun point2LatLng(point: Point) = LatLng(point.latitude, point.longitude)
    fun latLng2Point(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)
    
    fun getTransportTypesModels(transportTypes: List<TransportType>, prices: Map<String, String>) = 
        transportTypes.map {
            val id = it.id
            val nameRes = R.string::class.members.find( { it.name == "transport_type_$id" } )
            val nameId = (nameRes?.call() as Int?) ?: R.string.transport_type_unknown
            val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$id" } )
            val imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
            TransportTypeModel(id, nameId, imageId, it.paxMax, it.luggageMax, prices.get(id)!!)
        }
    
    fun getCurrenciesModels(currencies: List<Currency>) = currencies.map { CurrencyModel(it) }
    
    fun getRouteModel(distance: Int?, distanceUnit: DistanceUnit, polyLines: List<String>, route: Pair<GTAddress, GTAddress>) = 
        RouteModel(distance, distanceUnit, polyLines, route)
}
