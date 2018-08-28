package com.kg.gettransfer.data.repository

import retrofit2.HttpException

class ApiException(val e: Exception): RuntimeException(e) {
    private var code: Int = 0
    
    companion object {
        @JvmField val NO_USER       = 400
        @JvmField val INVALID_TOKEN = 401
        @JvmField val NOT_LOGGED_IN = 403
    }
    
    init {
    }
    
    fun isNoUser(): Boolean = code == NO_USER
    fun isNotLoggedIn(): Boolean = code == NOT_LOGGED_IN
}
