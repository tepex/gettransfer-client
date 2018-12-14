package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleBaseEntity(
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String
) {

    companion object {
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
    }
}
