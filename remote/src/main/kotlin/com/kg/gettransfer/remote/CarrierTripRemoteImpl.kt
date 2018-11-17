package com.kg.gettransfer.remote

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.remote.mapper.CarrierTripMapper

import com.kg.gettransfer.remote.model.CarrierTripModel
import com.kg.gettransfer.remote.model.CarrierTripModelWrapper
import com.kg.gettransfer.remote.model.CarrierTripsModel
import com.kg.gettransfer.remote.model.ResponseModel

import org.koin.standalone.get

class CarrierTripRemoteImpl: CarrierTripRemote {
    private val core   = get<ApiCore>()
    private val mapper = get<CarrierTripMapper>()

    override suspend fun getCarrierTrips(): List<CarrierTripEntity> {
        val response: ResponseModel<CarrierTripsModel> = core.tryTwice { core.api.getCarrierTrips() }
        val carrierTrips: List<CarrierTripModel> = response.data!!.trips
        return carrierTrips.map { mapper.fromRemote(it) }
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        val response: ResponseModel<CarrierTripModelWrapper> = core.tryTwice(id, { _id -> core.api.getCarrierTrip(_id) })
        val carrierTrip: CarrierTripModel = response.data!!.trip
        return mapper.fromRemote(carrierTrip)
    }
}
