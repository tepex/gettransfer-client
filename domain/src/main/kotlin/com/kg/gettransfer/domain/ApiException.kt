package com.kg.gettransfer.domain

class ApiException(val code: Int, val details: String): RuntimeException(details) {
    companion object {
        @JvmField val NO_USER       = 400
        @JvmField val NOT_LOGGED_IN = 403
        @JvmField val NOT_FOUND     = 404
        @JvmField val UNPROCESSABLE = 422
    }
    
    fun isNoUser() = code == NO_USER
    fun isNotLoggedIn() = code == NOT_LOGGED_IN
}
