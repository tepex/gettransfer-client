package com.kg.gettransfer.domain.model

data class TransportType(
    val id: ID,
    val paxMax: Int,
    val luggageMax: Int
) {

    enum class ID {
        ECONOMY, COMFORT, PREMIUM, MINIBUS, BUS, HELICOPTER, LIMOUSINE, BUSINESS, VAN, SUV, UNKNOWN;

        override fun toString() = name.toLowerCase()

        companion object {
            fun parse(id: String): ID {
                return try {
                    enumValueOf<ID>(id.toUpperCase())
                } catch(e: IllegalArgumentException) { UNKNOWN }
            }
        }
    }
}
