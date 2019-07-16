package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.VehicleInfo

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleInfoEntity(
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String
) {

    companion object {
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
    }
}

fun VehicleInfo.map() = VehicleInfoEntity(name, registrationNumber)
fun VehicleInfoEntity.map() = VehicleInfo(name, registrationNumber)
