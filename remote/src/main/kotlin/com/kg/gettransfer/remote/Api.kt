package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.*

import kotlinx.coroutines.Deferred

import retrofit2.http.*

interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"

        const val API_ACCESS_TOKEN       = "/api/access_token"
        const val API_CONFIGS            = "/api/configs"
        const val API_ACCOUNT            = "/api/account"
        const val API_LOGIN              = "/api/login"
        const val API_ROUTE_INFO         = "/api/route_info"
        const val API_CARRIER_TRIPS      = "/api/trips"
        const val API_TRANSFERS          = "/api/transfers"
        const val API_CREATE_NEW_PAYMENT = "/api/payments"
        const val API_PROMO              = "/api/promo_codes/search"
        const val API_RATE_OFFER         = "/api/offers/rate"
        const val API_FEEDBACK           = "/api/offers"
        const val API_WEBPUSH_TOKENS     = "/api/webpush_tokens"
        const val API_MESSAGES           = "/api/messages"
        const val API_BRAINTREE_TOKEN    = "/payments/braintree/client_token"
        const val API_BRAINTREE_CONFIRM  = "/payments/braintree/confirm"

        const val MOBILE_CONFIGS         = "/mobile/mobile.conf"

        const val API_LOCATION           = "/json"
    }

    @GET(API_ACCESS_TOKEN)
    fun accessToken(): Deferred<ResponseModel<TokenModel>>

    @GET(API_CONFIGS)
    fun getConfigs(): Deferred<ResponseModel<ConfigsModel>>

    @GET(API_ACCOUNT)
    fun getAccount(): Deferred<ResponseModel<AccountModelWrapper>>

    @PUT(API_ACCOUNT)
    fun putAccount(
        @Body account: AccountModelWrapper
    ): Deferred<ResponseModel<AccountModelWrapper>>

    /* If we are not signed in, don't post request to save account
    @POST("/api/account")
    fun postAccount(@Body account: AccountModel): Deferred<ResponseModel<AccountWrapperModel>>
    */

    @POST(API_LOGIN)
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Deferred<ResponseModel<AccountModelWrapper>>

    @GET(API_ROUTE_INFO)
    fun getRouteInfo(
        @Query("points[]") points: Array<String>,
        @Query("with_prices") withPrices: Boolean,
        @Query("return_way") returnWay: Boolean,
        @Query("currency") currency: String
    ): Deferred<ResponseModel<RouteInfoModel>>

    @GET(API_CARRIER_TRIPS)
    fun getCarrierTrips(): Deferred<ResponseModel<CarrierTripsBaseModel>>

    @GET("$API_CARRIER_TRIPS/{id}")
    fun getCarrierTrip(
        @Path("id") id: Long
    ): Deferred<ResponseModel<CarrierTripModelWrapper>>

    @GET("$API_TRANSFERS/{id}/offers")
    fun getOffers(
        @Path("id") id: Long
    ): Deferred<ResponseModel<OffersModel>>

    @GET(API_TRANSFERS)
    fun getAllTransfers(): Deferred<ResponseModel<TransfersModel>>

    @GET("$API_TRANSFERS/archive")
    fun getTransfersArchive(): Deferred<ResponseModel<TransfersModel>>

    @GET("$API_TRANSFERS/active")
    fun getTransfersActive(): Deferred<ResponseModel<TransfersModel>>

    @POST(API_TRANSFERS)
    fun postTransfer(
        @Body transfer: TransferNewWrapperModel
    ): Deferred<ResponseModel<TransferWrapperModel>>

    @GET("$API_TRANSFERS/{id}")
    fun getTransfer(
        @Path("id") id: Long,
        @Query("role") role: String
    ): Deferred<ResponseModel<TransferWrapperModel>>

    @POST("$API_TRANSFERS/{id}/cancel")
    fun cancelTransfer(
        @Path("id") id: Long,
        @Body reason: ReasonModel
    ): Deferred<ResponseModel<TransferWrapperModel>>

    @POST(API_CREATE_NEW_PAYMENT)
    fun createNewPayment(
        @Body createPayment: PaymentRequestModel
    ): Deferred<ResponseModel<PaymentModel>>

    @GET(API_PROMO)
    fun getDiscount(
        @Query("value") code: String
    ): Deferred<ResponseModel<String>>

    @GET("$API_CREATE_NEW_PAYMENT/{status}")
    fun changePaymentStatus(
        @Path("status") status: String,
        @Query("pg_order_id") pgOrderId: Long,
        @Query("without_redirect") withoutRedirect: Boolean
    ): Deferred<ResponseModel<PaymentStatusWrapperModel>>

    @POST("$API_RATE_OFFER/{id}/{type}")
    fun rateOffer(
        @Path("id") id: Long,
        @Path("type") type: String,
        @Body ratingModel: RateToRemote
    ): Deferred<ResponseModel<RateModel>>

    @POST("$API_FEEDBACK/{id}/feedback")
    fun sendFeedBack(
        @Path("id") id: Long,
        @Body passengerComment: FeedBackToRemote
    ): Deferred<ResponseModel<OfferModel>>

    @PUT("$API_WEBPUSH_TOKENS/{provider}/{id}")
    fun registerPushToken(
        @Path("provider") provider: String,
        @Path("id") id: String
    ): Deferred<ResponseModel<String>>

    @DELETE("$API_WEBPUSH_TOKENS/{id}")
    fun unregisterPushToken(
        @Path("id") token: String
    ): Deferred<ResponseModel<String>>

    @GET(MOBILE_CONFIGS)
    fun getMobileConfigs(): Deferred<MobileConfig>

    @GET("$API_MESSAGES/{id}")
    fun getChat(
            @Path("id") transferId: Long
    ): Deferred<ResponseModel<ChatModel>>

    @POST("$API_MESSAGES/{id}")
    fun newMessage(
            @Path("id") transferId: Long,
            @Body message: MessageNewWrapperModel
    ): Deferred<ResponseModel<MessageWrapperModel>>

    @POST("$API_MESSAGES/read/{id}")
    fun readMessage(
            @Path("id") messageId: Long
    ): Deferred<ResponseModel<MessageWrapperModel>>

    @GET(API_LOCATION)
    fun getMyLocation(): Deferred<LocationModel>

    @GET(API_BRAINTREE_TOKEN)
    fun getBraintreeToken(): Deferred<ResponseModel<BraintreeTokenModel>>

    @POST(API_BRAINTREE_CONFIRM)
    @FormUrlEncoded
    fun confirmPaypal(
            @Field("payment_id") paymentId: Long,
            @Field("nonce") nonce: String) : Deferred<ResponseModel<PaymentStatusWrapperModel>>
}
