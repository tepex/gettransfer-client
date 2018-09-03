package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"
        
        const val API_ACCESS_TOKEN = "/api/access_token"
        const val API_CONFIGS      = "/api/configs"
        const val API_ACCOUNT      = "/api/account"
        const val API_ROUTE_INFO   = "/api/route_info"
        const val API_LOGIN        = "/api/login"
        const val API_TRANSFERS    = "/api/transfers"
		const val API_TRANSFERS_ARCHIVE = "/api/transfers/archive"
		const val API_TRANSFERS_ACTIVE  = "/api/transfers/active"
		const val API_OFFERS       = "/offers"
    }
    
	@GET(API_ACCESS_TOKEN)
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ApiResponse<ApiToken>>
	
	@GET(API_CONFIGS)
	fun getConfigs(): Deferred<ApiResponse<ApiConfigs>>

	@GET(API_ACCOUNT)
	fun getAccount(): Deferred<ApiResponse<ApiAccountWrapper>>

	@PUT(API_ACCOUNT)
	fun putAccount(@Body account: ApiAccount): Deferred<ApiResponse<ApiAccountWrapper>>

	/* If we are not signed in, don't post request to save account
	@POST("/api/account")
	fun postAccount(@Body account: ApiAccount): Deferred<ApiResponse<ApiAccountWrapper>>
	*/
	
	@POST(API_LOGIN)
	@FormUrlEncoded
	fun login(@Field(ACCOUNT_EMAIL) email: String,
			  @Field(ACCOUNT_PASSWORD) password: String): Deferred<ApiResponse<ApiAccountWrapper>>
	
	@GET(API_ROUTE_INFO)
	fun getRouteInfo(
					 @Query("points[]") points: Array<String>,
					 @Query("with_prices") withPrices: Boolean,
					 @Query("return_way") returnWay: Boolean): Deferred<ApiResponse<ApiRouteInfo>>
	
	@GET(API_TRANSFERS)
	fun getAllTransfers(): Deferred<ApiResponse<ApiTransfers>>

	@GET("$API_TRANSFERS/{id}")
	fun getTransfer(@Path("id") id: Long): Deferred<ApiResponse<ApiTransferWrapper>>


	@GET(API_TRANSFERS_ARCHIVE)
	fun getTransfersArchive(): Deferred<ApiResponse<ApiTransfers>>

	@GET(API_TRANSFERS_ACTIVE)
	fun getTransfersActive(): Deferred<ApiResponse<ApiTransfers>>

	@GET("$API_TRANSFERS/{id}$API_OFFERS")
	fun getOffers(@Path("id") id: Long): Deferred<ApiResponse<ApiOffers>>
}
