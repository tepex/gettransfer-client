package com.kg.gettransfer.domain.model

data class VehicleInfo(
    val name: String,
    val registrationNumber: String
) {

    companion object {
        val EMPTY = VehicleInfo("", "")
    }
}
