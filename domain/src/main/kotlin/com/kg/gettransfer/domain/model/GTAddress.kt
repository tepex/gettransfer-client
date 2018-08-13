package com.kg.gettransfer.domain.model

data class GTAddress(var address: String) {
	override fun equals(other: Any?): Boolean {
		if(this == other) return true
		if(other == null || javaClass != other.javaClass) return false
		return (other as GTAddress).address == address
	}
	
	override fun hashCode(): Int = address.hashCode()
}
