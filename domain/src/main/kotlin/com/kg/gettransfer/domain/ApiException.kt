package com.kg.gettransfer.domain

class ApiException(val code: Int, val details: String, val type: String? = null) : RuntimeException(details) {
    companion object {
        @JvmField val APP_ERROR = 0
        @JvmField val NETWORK_ERROR = -1
        @JvmField val GEOCODER_ERROR = -2
        @JvmField val NO_USER       = 400
        @JvmField val NOT_LOGGED_IN = 403
        @JvmField val NOT_FOUND     = 404

        @JvmField val UNPROCESSABLE = 422

        @JvmField val INTERNAL_SERVER_ERROR   = 500
        @JvmField val CONNECTION_TIMED_OUT    = 522

        @JvmField val PHONE_REQUIRED_FOR_PAYMENT = 422
    }

    fun isNoUser() = code == NO_USER
    fun isNotLoggedIn() = code == NOT_LOGGED_IN
    fun isNotFound() = code == NOT_FOUND
    fun isAccountExistError() = type == "account_exists"
    fun isPhoneTaken() = type == "phone_taken"
}
