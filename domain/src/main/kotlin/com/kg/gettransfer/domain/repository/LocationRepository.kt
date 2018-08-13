package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.LocationResult

interface LocationRepository {
	fun checkPlayServicesAvailable(): Boolean
	suspend fun getCurrentLocation(): LocationResult
}
