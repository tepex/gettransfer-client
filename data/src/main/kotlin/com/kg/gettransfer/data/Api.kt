package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*

import kotlinx.coroutines.experimental.Deferred

import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
	@GET("/api/access_token")
	fun accessToken(@Query("api_key") apiKey: String): Deferred<Response<Token>>
}
