package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.domain.ApiException

object ExceptionMapper {
    fun map(e: RemoteException) = ApiException(e.code, e.details)
}
