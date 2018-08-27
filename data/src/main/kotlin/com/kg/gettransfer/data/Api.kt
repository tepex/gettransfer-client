package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"
    }
    
	@GET("/api/access_token")
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ApiResponse<ApiToken>>
	
	@GET("/api/configs")
	fun getConfigs(@Header(HEADER_TOKEN) token: String): Deferred<ApiResponse<ApiConfigs>>

	@GET("/api/account")
	fun getAccount(@Header(HEADER_TOKEN) token: String): Deferred<ApiResponse<ApiAccountWrapper>>

	@GET("/api/route_info")
	fun getRouteInfo(@Header("X-ACCESS-TOKEN") token: String,
					 @Query("points[]") points: Array<String>,
					 @Query("with_prices") withPrices: Boolean,
					 @Query("return_way") returnWay: Boolean): Deferred<ApiResponse<ApiRouteInfo>>
	/* If we are not signed in, don't post request to save account
	@POST("/api/account")
	fun postAccount(@Header(HEADER_TOKEN) token: String): Deferred<ApiResponse<ApiAccountWrapper>>
	*/
	
	@PUT("/api/account")
	@FormUrlEncoded
	fun putAccount(@Header(HEADER_TOKEN) token: String, @Body account: ApiAccount): Deferred<ApiResponse<ApiAccountWrapper>>

	@POST("api/login")
	@FormUrlEncoded
	fun login(@Header(HEADER_TOKEN) token: String,
			  @Field(ACCOUNT_EMAIL) email: String,
			  @Field(ACCOUNT_PASSWORD) password: String): Deferred<ApiResponse<ApiAccountWrapper>>
}
