package com.kg.gettransfer.domain.model

class GTAddress(val id: String? = null,
	                 val placeTypes: List<Int>? = null,
	                 val address: String,
	                 val point: Point? = null) {

	companion object {
		@JvmField val TYPE_STREET_ADDRESS = 1021
	}
	
	fun hasBuildingNumber(): Boolean {
		if(placeTypes == null || placeTypes.isEmpty())
		{
			
			
			return false
		}
		return placeTypes.contains(TYPE_STREET_ADDRESS)
	}

	override fun equals(other: Any?): Boolean {
		if(this == other) return true
		if(other == null || javaClass != other.javaClass) return false
		val eq = other as GTAddress
		if(id == null || eq.id == null) return address == eq.address
		return id == eq.id
	}
	
	override fun hashCode(): Int = id?.hashCode() ?: address.hashCode()
	override fun toString(): String = "$address {$placeTypes}"
}
