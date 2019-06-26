package com.kg.gettransfer.remote

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.remote.model.CarrierTripBaseModel
import com.kg.gettransfer.remote.model.CarrierTripModel
import com.kg.gettransfer.remote.model.CarrierTripModelWrapper
import com.kg.gettransfer.remote.model.CarrierTripsBaseModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.map
import org.koin.standalone.get

class CarrierTripRemoteImpl : CarrierTripRemote {

    private val core = get<ApiCore>()

    override suspend fun getCarrierTrips(): List<CarrierTripBaseEntity> {
        val response: ResponseModel<CarrierTripsBaseModel> = core.tryTwice { core.api.getCarrierTrips() }
        @Suppress("UnsafeCallOnNullableType")
        val carrierTrips: List<CarrierTripBaseModel> = response.data!!.trips
        return carrierTrips.map { it.map() }
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        val response: ResponseModel<CarrierTripModelWrapper> = core.tryTwice(id) { _id -> core.api.getCarrierTrip(_id) }
        @Suppress("UnsafeCallOnNullableType")
        val carrierTrip: CarrierTripModel = response.data!!.trip
        return carrierTrip.map()
    }
}
