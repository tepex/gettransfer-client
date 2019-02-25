package com.kg.gettransfer.data

import com.kg.gettransfer.domain.ApiException

class RemoteException(val code: Int, val details: String): RuntimeException(details) {
    companion object {
        @JvmField val NOT_HTTP      = ApiException.NETWORK_ERROR
        @JvmField val NO_USER       = ApiException.NO_USER
        @JvmField val INVALID_TOKEN = 401
        @JvmField val NOT_LOGGED_IN = ApiException.NOT_LOGGED_IN
        @JvmField val NOT_FOUND     = ApiException.NOT_FOUND
        
        @JvmField val INTERNAL_SERVER_ERROR = ApiException.INTERNAL_SERVER_ERROR
        @JvmField val CONNECTION_TIMED_OUT  = ApiException.CONNECTION_TIMED_OUT
    }
    
    fun isInvalidToken() = code == INVALID_TOKEN
    fun isNotLoggedIn()  = code == NOT_LOGGED_IN
    fun isConnectionError() = (code == NOT_HTTP || code == CONNECTION_TIMED_OUT)
}
