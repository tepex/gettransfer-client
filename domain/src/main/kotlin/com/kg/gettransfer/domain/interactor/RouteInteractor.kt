package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.AddressRepository
import com.kg.gettransfer.domain.repository.ApiRepository

class RouteInteractor(private val addressRepository: AddressRepository,
                      private val apiRepository: ApiRepository) {
    lateinit var from: GTAddress
    var to: GTAddress? = null 
    
    fun getCurrentAddress() = addressRepository.getCurrentAddress()
    
    fun getAddressByLocation(point: Point): GTAddress {
        from = addressRepository.getAddressByLocation(point)
        return from
    }
    
    fun isConcreteObjects() = from.isConcreteObject() && to?.isConcreteObject() ?: false
    fun getAutocompletePredictions(prediction: String) = addressRepository.getAutocompletePredictions(prediction)
    
    fun updateDestinationPoint() {
        if(to!!.point == null) to!!.point = addressRepository.getLatLngByPlaceId(to!!.id!!)
    }

    suspend fun getRouteInfo(withPrices: Boolean, returnWay: Boolean) = 
        apiRepository.getRouteInfo(from.point.toString(), to!!.point.toString(), withPrices, returnWay)
}
