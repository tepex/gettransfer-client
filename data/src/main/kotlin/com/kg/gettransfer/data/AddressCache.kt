package com.kg.gettransfer.data

import android.location.Location

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

/**
 * @param minDistance — минимальное расстояние кэша
 * @TODO: Задействовать minDistance
 */
class AddressCache(private val minDistance: Int = 0) {
	var address: GTAddress? = null
	var point: Point? = null
	
	fun getAddress(point: Point): GTAddress? {
		return if(checkDistance(point)) address
		else null
	}

	fun isCached(point: Point): Boolean {
		if(this.point == null) return false
		return checkDistance(point)
	}
	
	fun putAddress(point: Point, address: GTAddress) {
		this.point = point
		this.address = address
	}
	
	/**
	 * Проверка превышения минимального расстояния.
	 */
	private fun checkDistance(point: Point): Boolean {
		if(this.point == null) return false
		val lastPoint = this.point
		val distance = FloatArray(2)
		Location.distanceBetween(lastPoint!!.latitude, lastPoint!!.longitude,
			                     point.latitude, point.longitude, distance)
		return distance.get(0) <= minDistance
	}
}
