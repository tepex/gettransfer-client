package com.kg.gettransfer.domain.model

class GTAddress(var id: String? = null,
                val placeTypes: List<Int>? = null,
                name: String,
                val address: String,
                val primary: String?,
                val secondary: String?,
                var point: Point? = null) {

    val name: String
    get() = address

    companion object {
        @JvmField
        val TYPE_ADMINISTRATIVE_AREA_LEVEL_1 = 1001
        @JvmField
        val TYPE_LOCALITY = 1009
        @JvmField
        val TYPE_STREET_ADDRESS = 1021
    }

    /**
     * Check for concrete address type.
     * [Types][com.google.android.gms.location.places.Place]
     */
    fun isConcreteObject(): Boolean {
        if (placeTypes == null || placeTypes.isEmpty()) return false
        return placeTypes.any { (it > 0 && it < 1000) || it == TYPE_STREET_ADDRESS }
    }

    fun needApproximation(): Boolean {
        if (placeTypes == null || placeTypes.isEmpty()) return false
        return placeTypes.any { it == TYPE_ADMINISTRATIVE_AREA_LEVEL_1 || it == TYPE_LOCALITY }
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val eq = other as GTAddress
        if (id == null || eq.id == null) return name == eq.name
        return id == eq.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()
    override fun toString(): String = "$name {$placeTypes}"
}
