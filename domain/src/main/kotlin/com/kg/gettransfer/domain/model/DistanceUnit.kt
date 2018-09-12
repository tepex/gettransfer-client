package com.kg.gettransfer.domain.model

import kotlin.math.roundToInt

/**
 * Kilometers & miles
 */
sealed class DistanceUnit(val name: String) {
    object Km: DistanceUnit(KM)
    object Mi: DistanceUnit(MI)
    
    companion object {
        @JvmField val KM = "km"
        @JvmField val MI = "mi"
        
        fun parse(str: String?): DistanceUnit {
            if(str == MI) return Mi
            else return Km
        }
    
        fun km2Mi(km: Int) = (km.toDouble() / 1.609344).roundToInt()
    }
    
    override fun toString() = name
}
