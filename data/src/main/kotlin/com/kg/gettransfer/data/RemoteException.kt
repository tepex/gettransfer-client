package com.kg.gettransfer.data

import com.kg.gettransfer.domain.ApiException

class RemoteException(
    val code: Int,
    val details: String,
    val type: String? = null
) : RuntimeException(details) {

    fun isInvalidToken() = code == INVALID_TOKEN
    fun isNotLoggedIn()  = code == NOT_LOGGED_IN
    fun isConnectionError() = code == NOT_HTTP || code == CONNECTION_TIMED_OUT

    fun map() = ApiException(code, details, type)

    companion object {
        const val NOT_HTTP      = ApiException.NETWORK_ERROR
        const val NO_USER       = ApiException.NO_USER
        const val INVALID_TOKEN = 401
        const val NOT_LOGGED_IN = ApiException.NOT_LOGGED_IN
        const val NOT_FOUND     = ApiException.NOT_FOUND

        const val INTERNAL_SERVER_ERROR = ApiException.INTERNAL_SERVER_ERROR
        const val CONNECTION_TIMED_OUT  = ApiException.CONNECTION_TIMED_OUT
    }
}
