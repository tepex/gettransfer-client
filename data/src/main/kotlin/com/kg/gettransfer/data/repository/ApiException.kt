package com.kg.gettransfer.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

import com.kg.gettransfer.data.model.ApiResponse

import retrofit2.HttpException

import timber.log.Timber

class ApiException(val e: Exception): RuntimeException(e) {
    var code: Int = NOT_HTTP
    var details: String? = null
    
    companion object {
        @JvmField val NOT_HTTP      = -1
        @JvmField val NO_USER       = 400
        @JvmField val INVALID_TOKEN = 401
        @JvmField val NOT_LOGGED_IN = 403
        @JvmField val NOT_FOUND     = 404
        @JvmField val UNPROCESSABLE = 422
    }
    
    init {
        if(e is HttpException) {
            code = e.code()
            val json = e.response().errorBody()?.string()
            val type = GsonBuilder().create().fromJson(json, ApiResponse::class.java) 
            details = type.error?.details?.toString()
        }
    }
    
    fun isNoUser(): Boolean = code == NO_USER
    fun isInvalidToken(): Boolean = code == INVALID_TOKEN
    fun isNotLoggedIn(): Boolean = code == NOT_LOGGED_IN
}
