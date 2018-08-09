package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point

interface LocationRepository {
	suspend fun checkPlayServicesAvailable(): Boolean
	suspend fun getCurrentLocation(): Point?
}
