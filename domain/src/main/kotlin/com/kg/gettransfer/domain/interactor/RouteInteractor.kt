package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.AddressRepository
import com.kg.gettransfer.domain.repository.ApiRepository

class RouteInteractor(private val addressRepository: AddressRepository,
                      private val apiRepository: ApiRepository) {
    lateinit var route: Pair<GTAddress, GTAddress>
    private lateinit var routeInfo: RouteInfo
    
    val currentAddress = addressRepository.getCurrentAddress()
    val prices = routeInfo.prices!!.map { it.tranferId to it.min }.toMap()
    val distance = routeInfo.distance
    val polyLines = routeInfo.polyLines
    
    fun getAddressByLocation(point: Point) = addressRepository.getAddressByLocation(point)
}
