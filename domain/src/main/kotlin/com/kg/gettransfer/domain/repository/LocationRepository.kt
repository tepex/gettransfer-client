package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point

interface LocationRepository {
	fun checkPlayServicesAvailable(): Boolean
	fun getCurrentLocation(): Point?
}
