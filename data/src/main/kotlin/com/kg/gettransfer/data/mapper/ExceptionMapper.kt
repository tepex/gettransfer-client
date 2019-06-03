package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.GeoException

object ExceptionMapper {
    fun map(e: RemoteException) = ApiException(e.code, e.details, e.type)
    fun map(e: CacheException) = DatabaseException(e.code, e.details)
    fun map(e: LocationException) = GeoException(e.code, e.details)
}
