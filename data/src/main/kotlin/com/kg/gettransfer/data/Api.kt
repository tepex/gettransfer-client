package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ApiAccountWrapper
import com.kg.gettransfer.data.model.ApiConfigs
import com.kg.gettransfer.data.model.ApiResponse
import com.kg.gettransfer.data.model.ApiToken
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

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

	@POST("api/login")
	@FormUrlEncoded
	fun login(@Header("X-ACCESS-TOKEN") token: String,
			  @Field("email") email: String,
			  @Field("password") password: String): Deferred<ApiResponse<ApiAccountWrapper>>
}
