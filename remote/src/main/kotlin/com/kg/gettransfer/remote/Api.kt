package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.*

import okhttp3.ResponseBody

import retrofit2.http.*

@Suppress("TooManyFunctions", "WildcardImport")
interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"

        const val API_ACCESS_TOKEN = "/api/access_token"
        const val API_ACCOUNT = "/api/account"
        const val API_LOGIN = "/api/login"
        const val API_VERIFICATION_CODE = "/api/account/request_verification_code"
        const val API_CODE_FOR_CHANGE_EMAIL = "/api/account/email_code"
        const val API_CHANGE_EMAIL = "/api/account/change_email"
        const val API_ACCOUNT_LOGIN = "/api/account/login"
        const val API_ACCOUNT_REGISTER = "/api/account"
        const val API_ROUTE_INFO = "/api/route_info"
        const val API_TRANSFERS = "/api/transfers"
        const val API_CREATE_NEW_PAYMENT = "/api/payments"
        const val API_PROMO = "/api/promo_codes/search"
        const val API_RATE_OFFER = "/api/offers/rate"
        const val API_FEEDBACK = "/api/offers"
        const val API_WEBPUSH_TOKENS = "/api/webpush_tokens"
        const val API_MESSAGES = "/api/messages"
        const val API_BRAINTREE_TOKEN = "/payments/braintree/client_token"
        const val API_BRAINTREE_CONFIRM = "/payments/braintree/confirm"
        const val API_VOUCHER = "/api/transfers/voucher/"

        const val API_LOCATION = "/json"

        /*Autocomplete & place*/
        const val API_AUTOCOMPLETE  = "/api/address-lookup"
        const val API_PLACE_DETAILS = "/api/place"
    }

    @GET(API_ACCESS_TOKEN)
    suspend fun accessToken(): ResponseModel<TokenModel>

    @GET(API_ACCOUNT)
    suspend fun getAccount(): ResponseModel<AccountModelWrapper>

    @PUT(API_ACCOUNT)
    suspend fun putAccount(
        @Body account: AccountModelWrapper
    ): ResponseModel<AccountModelWrapper>

    /* If we are not signed in, don't post request to save account
    @POST("/api/account")
    fun postAccount(@Body account: AccountModel): ResponseModel<AccountWrapperModel>
    */

    @POST(API_ACCOUNT_LOGIN)
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String?,
        @Field("phone") phone: String?,
        @Field("password") password: String
    ): ResponseModel<AccountModelWrapper>

    @POST(API_ACCOUNT_REGISTER)
    suspend fun register(
        @Body account: RegistrationAccountEntityWrapper
    ): ResponseModel<AccountModelWrapper>

    @POST(API_VERIFICATION_CODE)
    @FormUrlEncoded
    suspend fun getVerificationCode(
        @Field("email") email: String?,
        @Field("phone") phone: String?
    ): ResponseModel<String?>

    @POST(API_CODE_FOR_CHANGE_EMAIL)
    suspend fun getCodeForChangeEmail(
        @Query("new_email") email: String
    ): ResponseModel<String?>

    @POST(API_CHANGE_EMAIL)
    suspend fun changeEmail(
        @Query("new_email") email: String,
        @Query("code") code: String
    ): ResponseModel<String?>

    @Suppress("LongParameterList")
    @GET(API_ROUTE_INFO)
    suspend fun getRouteInfo(
        @Query("points[]") points: Array<String>,
        @Query("duration") duration: Int?,
        @Query("with_prices") withPrices: Boolean,
        @Query("return_way") returnWay: Boolean,
        @Query("currency") currency: String,
        @Query("date_to") dateTime: String?
    ): ResponseModel<RouteInfoModel>

    @GET("$API_TRANSFERS/{id}/offers")
    suspend fun getOffers(
        @Path("id") id: Long
    ): ResponseModel<OffersModel>

    @GET(API_TRANSFERS)
    suspend fun getAllTransfers(
        @Query("role") role: String,
        @Query("page") page: Int,
        @Query("filtering[status]") status: String?,
        @Query("sorting[field]") sortType: String? = "date_to",
        @Query("sorting[order_by]") sortOrder: String? = "desc"
    ): ResponseModel<TransfersModel>

    @GET("$API_TRANSFERS/archive")
    suspend fun getTransfersArchive(): ResponseModel<TransfersModel>

    @GET("$API_TRANSFERS/active")
    suspend fun getTransfersActive(): ResponseModel<TransfersModel>

    @POST(API_TRANSFERS)
    suspend fun postTransfer(
        @Body transfer: TransferNewWrapperModel
    ): ResponseModel<TransferWrapperModel>

    @GET("$API_TRANSFERS/{id}")
    suspend fun getTransfer(
        @Path("id") id: Long
    ): ResponseModel<TransferWrapperModel>

    @POST("$API_TRANSFERS/{id}/cancel")
    suspend fun cancelTransfer(
        @Path("id") id: Long,
        @Body reason: ReasonModel
    ): ResponseModel<TransferWrapperModel>

    @POST(API_CREATE_NEW_PAYMENT)
    suspend fun createNewPayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<PaymentModel>

    @GET(API_PROMO)
    suspend fun getDiscount(
        @Query("value") code: String
    ): ResponseModel<String>

    @GET("$API_CREATE_NEW_PAYMENT/{status}")
    suspend fun changePaymentStatus(
        @Path("status") status: String,
        @Query("pg_order_id") pgOrderId: Long,
        @Query("without_redirect") withoutRedirect: Boolean
    ): ResponseModel<PaymentStatusWrapperModel>

    @POST("$API_RATE_OFFER/{id}/{type}")
    suspend fun rateOffer(
        @Path("id") id: Long,
        @Path("type") type: String,
        @Body ratingModel: RateToRemote
    ): ResponseModel<RateModel>

    @POST("$API_FEEDBACK/{id}/feedback")
    suspend fun sendFeedBack(
        @Path("id") id: Long,
        @Body passengerComment: FeedBackToRemote
    ): ResponseModel<OfferModel>

    @PUT("$API_WEBPUSH_TOKENS/{provider}/{id}")
    suspend fun registerPushToken(
        @Path("provider") provider: String,
        @Path("id") id: String
    ): ResponseModel<String>

    @DELETE("$API_WEBPUSH_TOKENS/{id}")
    suspend fun unregisterPushToken(
        @Path("id") token: String
    ): ResponseModel<String>

    @GET("$API_MESSAGES/{id}")
    suspend fun getChat(
        @Path("id") transferId: Long
    ): ResponseModel<ChatModel>

    @POST("$API_MESSAGES/{id}")
    suspend fun newMessage(
        @Path("id") transferId: Long,
        @Body message: MessageNewWrapperModel
    ): ResponseModel<MessageWrapperModel>

    @POST("$API_MESSAGES/read/{id}")
    suspend fun readMessage(
        @Path("id") messageId: Long
    ): ResponseModel<MessageWrapperModel>

    @GET(API_LOCATION)
    suspend fun getMyLocation(): LocationModel

    @GET(API_BRAINTREE_TOKEN)
    suspend fun getBraintreeToken(): ResponseModel<BraintreeTokenModel>

    @POST(API_BRAINTREE_CONFIRM)
    @FormUrlEncoded
    suspend fun confirmPaypal(
        @Field("payment_id") paymentId: Long,
        @Field("nonce") nonce: String
    ): ResponseModel<PaymentStatusWrapperModel>

    /*Autocomplete*/
    @GET(API_AUTOCOMPLETE)
    suspend fun getAutocompletePredictions(
        @Query("query") query: String,
        @Query("lang") lang: String
    ): AutocompletePredictionsModel

    @GET(API_PLACE_DETAILS)
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("lang") lang: String
    ): PlaceDetailsResultModel

    @GET("$API_VOUCHER{id}")
    suspend fun downloadVoucher(
        @Path("id") transferId: Long
    ): ResponseBody

    @POST("$API_TRANSFERS/{id}/analytics_sent")
    suspend fun sendAnalytics(
        @Path("id") transferId: Long,
        @Body role: RoleModel
    ): ResponseModel<String>
}
