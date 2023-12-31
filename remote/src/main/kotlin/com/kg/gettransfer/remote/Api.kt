package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.*

import okhttp3.ResponseBody

import retrofit2.http.*

@Suppress("TooManyFunctions", "WildcardImport")
interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"
        const val CHECKOUTCOM_HEADER_TOKEN = "Authorization"
        const val AUTH_KEY = "auth_key"

        const val API_ACCESS_TOKEN = "/api/access_token"
        const val API_ACCOUNT = "/api/account"
        const val API_VERIFICATION_CODE = "/api/account/request_verification_code"
        const val API_CONFIRMATION_CODE = "/api/account/request_confirmation_code"
        const val API_CHANGE_CONTACT = "/api/account/change_contact"
        const val API_ACCOUNT_LOGIN = "/api/account/login"
        const val API_ACCOUNT_REGISTER = "/api/account"
        const val API_ROUTE_INFO = "/api/route_info"
        const val API_TRANSFERS = "/api/transfers"
        const val API_PAYMENT = "/api/payments"
        const val API_PAYMENT_PROCESS = "/api/payments/process"
        const val API_PROMO = "/api/promo_codes/search"
        const val API_RATE_OFFER = "/api/offers/rate"
        const val API_FEEDBACK = "/api/offers"
        const val API_MESSAGES = "/api/messages"
        const val API_BRAINTREE_TOKEN = "/payments/braintree/client_token"
        const val API_BRAINTREE_CONFIRM = "/payments/braintree/confirm"
        const val API_VOUCHER = "/api/transfers/voucher/"
        const val API_ONESIGNAL = "/api/account/onesignal_token"
        const val API_SIGN_OUT = "/api/account/sign_out"

        /*Autocomplete & place*/
        const val API_AUTOCOMPLETE  = "/api/address-lookup"
        const val API_PLACE_DETAILS = "/api/place"
    }

    @GET(API_ACCESS_TOKEN)
    suspend fun accessToken(
        @Header(HEADER_TOKEN) token: String?,
        @Query(AUTH_KEY) authKey: String?
    ): ResponseModel<TokenModel>

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
        @Field("email") email: String? = null,
        @Field("phone") phone: String? = null,
        @Field("password") password: String
    ): ResponseModel<AccountModelWrapper>

    @POST(API_SIGN_OUT)
    suspend fun signOut(): ResponseModel<String>

    @POST(API_ACCOUNT_REGISTER)
    suspend fun register(
        @Body account: RegistrationAccountEntityWrapper
    ): ResponseModel<AccountModelWrapper>

    @POST(API_VERIFICATION_CODE)
    @FormUrlEncoded
    suspend fun getVerificationCode(
        @Field("email") email: String? = null,
        @Field("phone") phone: String? = null
    ): ResponseModel<String?>

    @POST(API_CONFIRMATION_CODE)
    suspend fun getConfirmationCode(
        @Query("new_email") email: String? = null,
        @Query("new_phone") phone: String? = null
    ): ResponseModel<String?>

    @POST(API_CHANGE_CONTACT)
    suspend fun changeContact(
        @Query("new_email") email: String? = null,
        @Query("new_phone") phone: String? = null,
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
        @Query("date_to") dateTo: String?,
        @Query("date_return") dateReturn: String?
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

    @POST("$API_TRANSFERS/{id}/restore")
    suspend fun restoreTransfer(
        @Path("id") id: Long
    ): ResponseModel<TransferWrapperModel>

    @POST(API_PAYMENT)
    suspend fun createPlatronPayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<PlatronPaymentModel>

    @POST(API_PAYMENT)
    suspend fun createCheckoutcomPayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<CheckoutcomPaymentModel>

    @POST(".")
    suspend fun getCheckoutcomToken(
        @Body tokenRequest: CheckoutcomTokenRequestModel
    ): CheckoutcomTokenModel

    @POST(API_PAYMENT)
    suspend fun createBraintreePayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<BraintreePaymentModel>

    @POST(API_PAYMENT)
    suspend fun createGooglePayPayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<GooglePayPaymentModel>

    @POST(API_PAYMENT)
    suspend fun createGroundPayment(
        @Body createPayment: PaymentRequestModel
    ): ResponseModel<Unit>

    @POST(API_PAYMENT_PROCESS)
    suspend fun processPayment(
        @Body paymentProcess: StringPaymentProcessRequestModel
    ): ResponseModel<PaymentProcessModel>

    @POST(API_PAYMENT_PROCESS)
    suspend fun processPayment(
        @Body paymentProcess: JsonPaymentProcessRequestModel
    ): ResponseModel<PaymentProcessModel>

    @GET(API_PROMO)
    suspend fun getDiscount(
        @Query("value") code: String
    ): ResponseModel<String>

    @GET("$API_PAYMENT/{status}")
    suspend fun changePaymentStatus(
        @Path("status") status: String,
        @Query("payment_id") paymentId: Long,
        @Query("pg_failure_description") pgFailureDescription: String?,
        @Query("without_redirect") withoutRedirect: Boolean
    ): ResponseModel<PaymentStatusWrappedModel>

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

    @GET("$API_MESSAGES/{id}")
    suspend fun getChat(
        @Path("id") transferId: Long
    ): ResponseModel<ChatModel>

    @GET(".")
    suspend fun getMyLocation(): LocationModel

    @GET(API_BRAINTREE_TOKEN)
    suspend fun getBraintreeToken(): ResponseModel<BraintreeTokenModel>

    @POST(API_BRAINTREE_CONFIRM)
    @FormUrlEncoded
    suspend fun confirmPaypal(
        @Field("payment_id") paymentId: Long,
        @Field("nonce") nonce: String
    ): ResponseModel<PaymentStatusWrappedModel>

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

    @PUT(API_ONESIGNAL)
    suspend fun associatePlayerId(
        @Body params: PlayerIdModel
    ): ResponseModel<String>
}
