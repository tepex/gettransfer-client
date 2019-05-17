package com.kg.gettransfer.domain.model

data class GTAddress(
    val cityPoint: CityPoint,
    val placeTypes: List<Int>?,
    val address: String?,
    val primary: String?,
    val secondary: String?
) {

    /**
     * Check for concrete address type.
     * [Types][com.google.android.gms.location.places.Place]
     */
    val lat: Double?
    get() = cityPoint.point?.latitude
    val lon: Double?
    get() = cityPoint.point?.longitude

    fun isConcreteObject() = if (placeTypes == null || placeTypes.isEmpty()) false
        else placeTypes.any { (it in 1..999) || it == TYPE_STREET_ADDRESS }

    fun needApproximation() = if (placeTypes == null || placeTypes.isEmpty()) false
        else placeTypes.any { it == TYPE_ADMINISTRATIVE_AREA_LEVEL_1 || it == TYPE_LOCALITY }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val eq = other as GTAddress
        return if (cityPoint.placeId != null && eq.cityPoint.placeId != null) cityPoint.placeId == eq.cityPoint.placeId
        else cityPoint.name == eq.cityPoint.name
    }

    override fun hashCode(): Int = cityPoint.placeId?.hashCode() ?: cityPoint.name!!.hashCode()
    override fun toString(): String = "$cityPoint.name {$placeTypes}"

    companion object {
        const val TYPE_ADMINISTRATIVE_AREA_LEVEL_1 = 1001
        const val TYPE_LOCALITY                    = 1009
        const val TYPE_STREET_ADDRESS              = 1021

        val EMPTY = GTAddress(CityPoint("", null, null), null, null, null, null)
    }
}
