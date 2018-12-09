package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleEntity(
<<<<<<< HEAD
    @SerialName(ID) val id: Long,
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String,
=======
    @SerialName(VEHICLE_BASE) val vehicleBase: VehicleBaseEntity,
>>>>>>> refactor(model): VehicleBase
    @SerialName(YEAR) val year: Int,
    @SerialName(COLOR) val color: String?,
    /* Dirty hack. Splitting TransportType.id and Vehicle.id */
    @SerialName(TRANSPORT_TYPE_ID) val transportTypeId: String,
    @SerialName(TransportTypeEntity.PAX_MAX) val paxMax: Int,
    @SerialName(TransportTypeEntity.LUGGAGE_MAX) val luggageMax: Int,
    @SerialName(PHOTOS) val photos: List<String>
) {

    companion object {
<<<<<<< HEAD
        const val ID                  = "id"
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
=======
        const val VEHICLE_BASE        = "vehicle_base"
>>>>>>> refactor(model): VehicleBase
        const val YEAR                = "year"
        const val COLOR               = "color"
        const val TRANSPORT_TYPE_ID   = "transport_type_id"
        const val PHOTOS              = "photos"
    }
}
