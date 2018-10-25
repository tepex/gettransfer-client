package com.kg.gettransfer.data

class RemoteException(val code: Int, val details: String): RuntimeException(details) {
    companion object {
        @JvmField val NOT_HTTP      = -1
        @JvmField val INVALID_TOKEN = 401
        
        @JvmField val INTERNAL_SERVER_ERROR = 500
        @JvmField val CONNECTION_TIMED_OUT  = 522
    }
    
    fun isInvalidToken() = code == INVALID_TOKEN
}
