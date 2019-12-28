package com.kg.gettransfer.domain.model

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.GeoException

data class Result<M>(
    val model: M,
    val error: ApiException? = null,
    val fromCache: Boolean = false,
    val cacheError: DatabaseException? = null,
    val geoException: GeoException? = null
) {

    fun isError()     = error != null
    fun isSuccess()   = if (error == null) model else null    // get data if no need to handle error
    fun hasData()     = if (error == null || fromCache) model else null
    fun isDataError() = if (error != null && !fromCache) error else null
    fun isGeoError()       = geoException != null
}
