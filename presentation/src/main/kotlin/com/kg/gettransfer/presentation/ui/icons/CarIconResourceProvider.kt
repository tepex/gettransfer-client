package com.kg.gettransfer.presentation.ui.icons

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.presentation.model.VehicleModel

object CarIconResourceProvider {

    fun getCarIcon(vehicle: VehicleModel) =
            getType(vehicle.transportType.id).getColor(vehicle.color?: "unknown")

}

private fun getType(id: TransportType.ID) =
        when (id) {
            TransportType.ID.BUS         -> BusTransport
            TransportType.ID.BUSINESS,
            TransportType.ID.ECONOMY,
            TransportType.ID.COMFORT,
            TransportType.ID.PREMIUM     -> SedanTransport
            TransportType.ID.LIMOUSINE   -> LimousineTransport
            TransportType.ID.MINIBUS,
            TransportType.ID.VAN         -> MinibusTransport
            TransportType.ID.SUV         -> SuvTransport
            else                         -> SedanTransport
        }

abstract class TransportColor {
    abstract val white:  Int
    abstract val silver: Int
    abstract val grey:   Int

    abstract val black: Int
    abstract val brown: Int
    abstract val beige: Int

    abstract val yellow: Int
    abstract val gold:   Int
    abstract val orange: Int

    abstract val red:    Int
    abstract val purple: Int
    abstract val blue:   Int
    abstract val green:  Int

    fun getColor(color: String) = when (color) {
        "white"  -> white
        "silver" -> silver
        "grey"   -> grey
        "green"  -> green
        "brown"  -> brown
        "beige"  -> beige
        "yellow" -> yellow
        "gold"   -> gold
        "orange" -> orange
        "red"    -> red
        "purple" -> purple
        "blue"   -> blue
        else     -> black
    }
}