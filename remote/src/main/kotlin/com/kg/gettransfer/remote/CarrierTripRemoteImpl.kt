package com.kg.gettransfer.remote

import com.kg.gettransfer.data.CarrierTripRemote

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.remote.mapper.CarrierTripBaseMapper
import com.kg.gettransfer.remote.mapper.CarrierTripMapper

import com.kg.gettransfer.remote.model.CarrierTripBaseModel
import com.kg.gettransfer.remote.model.CarrierTripModel
import com.kg.gettransfer.remote.model.CarrierTripModelWrapper
import com.kg.gettransfer.remote.model.CarrierTripsBaseModel
import com.kg.gettransfer.remote.model.ResponseModel

import org.koin.standalone.get

class CarrierTripRemoteImpl : CarrierTripRemote {
    private val core                  = get<ApiCore>()
    private val carrierTripMapper     = get<CarrierTripMapper>()
    private val carrierTripBaseMapper = get<CarrierTripBaseMapper>()

    override suspend fun getCarrierTrips(): List<CarrierTripBaseEntity> {
        val response: ResponseModel<CarrierTripsBaseModel> = core.tryTwice { core.api.getCarrierTrips() }
        val carrierTrips: List<CarrierTripBaseModel> = response.data!!.trips
        return carrierTrips.map { carrierTripBaseMapper.fromRemote(it) }
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        val response: ResponseModel<CarrierTripModelWrapper> = core.tryTwice(id) { _id -> core.api.getCarrierTrip(_id) }
        val carrierTrip: CarrierTripModel = response.data!!.trip
        return carrierTripMapper.fromRemote(carrierTrip)
    }
}
