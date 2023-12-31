package com.kg.gettransfer.domain.model

import java.util.Locale

data class TransportType(
    val id: ID,
    val paxMax: Int,
    val luggageMax: Int
) {

    enum class ID {
        ECONOMY, COMFORT, PREMIUM, MINIBUS, BUS, HELICOPTER, LIMOUSINE, VIP, BUSINESS, VAN, SUV, UNKNOWN;

        override fun toString() = name.toLowerCase(Locale.US)
    }

    companion object {
        val DEFAULT_LIST = arrayListOf(
            TransportType(ID.ECONOMY, 3, 3),
            TransportType(ID.COMFORT, 3, 3),
            TransportType(ID.BUSINESS, 3, 3),
            TransportType(ID.PREMIUM, 3, 3),
            TransportType(ID.LIMOUSINE, 3, 3),
            TransportType(ID.SUV, 5, 5),
            TransportType(ID.VAN, 8, 6),
            TransportType(ID.MINIBUS, 16, 16),
            TransportType(ID.BUS, 50, 50),
            TransportType(ID.HELICOPTER, 5, 2)
        )

        val BIG_TRANSPORT = arrayListOf(ID.SUV, ID.VAN, ID.HELICOPTER)
        val BUS_TRANSPORT = arrayListOf(ID.MINIBUS, ID.BUS)
    }
}
