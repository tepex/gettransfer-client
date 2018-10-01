package com.kg.gettransfer.data

class RemoteException(val code: Int, val details: String): RuntimeException(details) {
    companion object {
        @JvmField val NOT_HTTP      = -1
        @JvmField val NO_USER       = 400
        @JvmField val INVALID_TOKEN = 401
        @JvmField val NOT_LOGGED_IN = 403
        @JvmField val NOT_FOUND     = 404
        @JvmField val UNPROCESSABLE = 422
    }
    
    fun isNoUser() = code == NO_USER
    fun isInvalidToken() = code == INVALID_TOKEN
    fun isNotLoggedIn() = code == NOT_LOGGED_IN
}
