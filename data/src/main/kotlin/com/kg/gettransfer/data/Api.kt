package com.kg.gettransfer.data

<<<<<<< HEAD
import com.kg.gettransfer.data.model.*

import kotlinx.coroutines.experimental.Deferred

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Api {
	@GET("/api/access_token")
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ApiResponse<ApiToken>>
	
	@GET("/api/configs")
	fun configs(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiConfigs>>
=======
interface Api {
>>>>>>> added retrofit and other
}
