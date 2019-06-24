package com.kg.gettransfer.domain.model

data class GTAddress(
    val cityPoint: CityPoint,
    val placeTypes: List<String>,
    val address: String?,
    val variants: Pair<String?, String?>?
) {

    /**
     * Check for concrete address type.
     * [Types][com.google.android.gms.location.places.Place]
     */
    val lat = cityPoint.point?.latitude
    val lon = cityPoint.point?.longitude

    fun needApproximation() = if (placeTypes.isEmpty()) false else placeTypes.any { it == "route" }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        @Suppress("UNCHECKED_CAST")
        val eq = other as GTAddress
        return if (cityPoint.placeId != null && eq.cityPoint.placeId != null) {
            cityPoint.placeId == eq.cityPoint.placeId
        } else {
            cityPoint.name == eq.cityPoint.name
        }
    }

    override fun hashCode(): Int = cityPoint.placeId?.hashCode() ?: cityPoint.name.hashCode()
    override fun toString(): String = "$cityPoint.name {$placeTypes}"

    companion object {
        const val TYPE_ADMINISTRATIVE_AREA_LEVEL_1 = 1001
        const val TYPE_LOCALITY                    = 1009
        const val TYPE_STREET_ADDRESS              = 1021

        val EMPTY = GTAddress(CityPoint.EMPTY, emptyList<String>(), null, null)

        fun parseAddress(addr: String): Pair<String?, String?> {
            val lastCommaIndex = addr.lastIndexOf(", ")
            return if (lastCommaIndex >= 0) {
                addr.substring(0, lastCommaIndex) to addr.substring(lastCommaIndex + 2, addr.length)
            } else {
                addr to null
            }
        }
    }
}
