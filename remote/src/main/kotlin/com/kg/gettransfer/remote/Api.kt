package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.*

import kotlinx.coroutines.experimental.Deferred

import retrofit2.http.*

interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"
        
        const val API_ACCESS_TOKEN  = "/api/access_token"
        const val API_CONFIGS       = "/api/configs"
        const val API_ACCOUNT       = "/api/account"
        const val API_LOGIN         = "/api/login"
        const val API_ROUTE_INFO    = "/api/route_info"
        const val API_CARRIER_TRIPS = "/api/trips"
        const val API_TRANSFERS    = "/api/transfers"
    }

    @GET(API_ACCESS_TOKEN)
    fun accessToken(@Query("api_key") apiKey: String): Deferred<ResponseModel<TokenModel>>

    @GET(API_CONFIGS)
    fun getConfigs(): Deferred<ResponseModel<ConfigsModel>>

    @GET(API_ACCOUNT)
    fun getAccount(): Deferred<ResponseModel<AccountModelWrapper>>

    @PUT(API_ACCOUNT)
    fun putAccount(@Body account: AccountModel): Deferred<ResponseModel<AccountModelWrapper>>

    /* If we are not signed in, don't post request to save account
    @POST("/api/account")
    fun postAccount(@Body account: AccountModel): Deferred<ResponseModel<AccountWrapperModel>>
    */

    @POST(API_LOGIN)
    @FormUrlEncoded
    fun login(@Field("email") email: String,
              @Field("password") password: String): Deferred<ResponseModel<AccountModelWrapper>>

    @GET(API_ROUTE_INFO)
    fun getRouteInfo(@Query("points[]") points: Array<String>,
                     @Query("with_prices") withPrices: Boolean,
                     @Query("return_way") returnWay: Boolean): Deferred<ResponseModel<RouteInfoModel>>

    @GET(API_CARRIER_TRIPS)
    fun getCarrierTrips(): Deferred<ResponseModel<CarrierTripsModel>>

    @GET("$API_CARRIER_TRIPS/{id}")
    fun getCarrierTrip(@Path("id") id: Long): Deferred<ResponseModel<CarrierTripModelWrapper>>
    
    @GET("$API_TRANSFERS/{id}/offers")
    fun getOffers(@Path("id") id: Long): Deferred<ResponseModel<OffersModel>>

	@GET(API_TRANSFERS)
	fun getAllTransfers(): Deferred<ResponseModel<TransfersModel>>

	@GET("$API_TRANSFERS/archive")
	fun getTransfersArchive(): Deferred<ResponseModel<TransfersModel>>

	@GET("$API_TRANSFERS/active")
	fun getTransfersActive(): Deferred<ResponseModel<TransfersModel>>

	@POST(API_TRANSFERS)
	fun postTransfer(@Body transfer: TransferNewWrapperModel): Deferred<ResponseModel<TransferWrapperModel>>
	
	@GET("$API_TRANSFERS/{id}")
	fun getTransfer(@Path("id") id: Long): Deferred<ResponseModel<TransferWrapperModel>>
	
	@POST("$API_TRANSFERS/{id}/cancel")
	fun cancelTransfer(@Path("id") id: Long, @Body reason: ReasonModel): Deferred<ResponseModel<TransferWrapperModel>>
}
