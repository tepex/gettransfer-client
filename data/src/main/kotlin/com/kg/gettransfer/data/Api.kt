package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*

import kotlinx.coroutines.experimental.Deferred

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Api {
	@GET("/api/access_token")
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ApiResponse<ApiToken>>
	
	@GET("/api/configs")
	fun getConfigs(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiConfigs>>

	@GET("/api/account")
	fun getAccount(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiAccountWrapper>>
	
	/* If we are not signed in, don't post request to save account
	@POST("/api/account")
	fun postAccount(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiAccountWrapper>>
	
	@PUT("/api/account")
	fun putAccount(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiAccountWrapper>>
	*/
}
