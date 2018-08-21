package com.kg.gettransfer.domain.model

class GTAddress(val id: String? = null,
                val placeTypes: List<Int>? = null,
                val name: String,
                val primary: String?,
                val secondary: String?,
                val point: Point? = null) {

	companion object {
		@JvmField val TYPE_STREET_ADDRESS = 1021
	}
	
	/**
	 * Check for concrete address type.
	 * [Types][com.google.android.gms.location.places.Place]
	 */
	fun isConcreteObject(): Boolean {
		if(placeTypes == null || placeTypes.isEmpty()) return false
		return placeTypes.any { (it > 0 && it < 1000) || it == TYPE_STREET_ADDRESS }
	}

	override fun equals(other: Any?): Boolean {
		if(this == other) return true
		if(other == null || javaClass != other.javaClass) return false
		val eq = other as GTAddress
		if(id == null || eq.id == null) return name == eq.name
		return id == eq.id
	}
	
	override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()
	override fun toString(): String = "$name {$placeTypes}"
}
