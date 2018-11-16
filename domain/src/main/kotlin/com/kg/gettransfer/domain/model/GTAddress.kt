package com.kg.gettransfer.domain.model

data class GTAddress(val cityPoint: CityPoint,
                     val placeTypes: List<Int>?,
                     val address: String?,
                     val primary: String?,
                     val secondary: String?) {

    companion object {
        @JvmField
        val TYPE_ADMINISTRATIVE_AREA_LEVEL_1 = 1001
        @JvmField
        val TYPE_LOCALITY = 1009
        @JvmField
        val TYPE_STREET_ADDRESS = 1021
        
        @JvmField
        val EMPTY = GTAddress(CityPoint(null, null, null), null, null, null, null)
    }

    /**
     * Check for concrete address type.
     * [Types][com.google.android.gms.location.places.Place]
     */
     
    fun isConcreteObject(): Boolean {
        if(placeTypes == null || placeTypes.isEmpty()) return false
        return placeTypes.any { (it > 0 && it < 1000) || it == TYPE_STREET_ADDRESS }
    }

    fun needApproximation(): Boolean {
        if(placeTypes == null || placeTypes.isEmpty()) return false
        return placeTypes.any { it == TYPE_ADMINISTRATIVE_AREA_LEVEL_1 || it == TYPE_LOCALITY }
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other == null || javaClass != other.javaClass) return false
        val eq = other as GTAddress
        if(cityPoint.placeId == null || eq.cityPoint.placeId == null) return cityPoint.name == eq.cityPoint.name
        return cityPoint.placeId == eq.cityPoint.placeId
    }

    override fun hashCode(): Int = cityPoint.placeId?.hashCode() ?: cityPoint.name!!.hashCode()
    override fun toString(): String = "$cityPoint.name {$placeTypes}"
}
