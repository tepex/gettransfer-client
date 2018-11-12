package com.kg.gettransfer.data.mapper

//import com.kg.gettransfer.data.NetworkNotAvailableException
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.domain.ApiException
//import com.kg.gettransfer.domain.InternetNotAvailableException

object ExceptionMapper {
    fun map(e: RemoteException) = ApiException(e.code, e.details)
//    fun map(e: NetworkNotAvailableException) = InternetNotAvailableException()
}
