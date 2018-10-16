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
        const val API_CARRIER_TRIPS = "/api/trips"
        const val API_CREATE_NEW_PAYMENT = "/api/payments"
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
	
	/*
	@GET(API_TRANSFERS)
	fun getAllTransfers(): Deferred<ApiResponse<ApiTransfers>>

	@GET("$API_TRANSFERS/{id}")
	fun getTransfer(@Path("id") id: Long): Deferred<ApiResponse<ApiTransferWrapper>>

	@GET(API_TRANSFERS_ARCHIVE)
	fun getTransfersArchive(): Deferred<ApiResponse<ApiTransfers>>

	@GET(API_TRANSFERS_ACTIVE)
	fun getTransfersActive(): Deferred<ApiResponse<ApiTransfers>>

	@POST(API_TRANSFERS)
	fun postTransfer(@Body transfer: ApiTransferWrapper): Deferred<ApiResponse<ApiTransferWrapper>>
	
	@POST("$API_TRANSFERS/{id}/cancel")
	fun cancelTransfer(@Path("id") id: Long, @Body reason: ApiReason): Deferred<ApiResponse<ApiTransferWrapper>>
	*/

	@GET(API_CARRIER_TRIPS)
	fun getCarrierTrips(): Deferred<ApiResponse<ApiCarrierTrips>>

	@GET("$API_CARRIER_TRIPS/{id}")
	fun getCarrierTrip(@Path("id") id: Long): Deferred<ApiResponse<ApiCarrierTripWrapper>>

	@POST(API_CREATE_NEW_PAYMENT)
	fun createNewPayment(@Body createPaymentEntity: ApiCreatePaymentEntity): Deferred<ApiResponse<ApiPaymentResult>>

	@GET("$API_CREATE_NEW_PAYMENT/{status}")
	fun changePaymentStatus(@Path("status") status: String, @Body payment: ApiPayment)
}
