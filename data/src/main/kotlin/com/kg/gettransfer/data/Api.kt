package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ApiAccount
import com.kg.gettransfer.data.model.ApiConfigs
import com.kg.gettransfer.data.model.ApiResponse
import com.kg.gettransfer.data.model.ApiToken
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
	@GET("/api/access_token")
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ApiResponse<ApiToken>>
	
	@GET("/api/configs")
	fun configs(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiConfigs>>

	@POST("api/login")
	fun login(@Header("X-ACCESS-TOKEN") token: String): Deferred<ApiResponse<ApiAccount>>
}
