package com.kg.gettransfer.module.http


import com.kg.gettransfer.module.http.json.*
import com.kg.gettransfer.realm.Config
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


typealias ObservableResponse<T> = Observable<Response<BaseResponse<T>>>


interface HttpApi {
    @GET("configs")
    fun getConfig(): Observable<BaseResponse<Config>>


    // --


    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String)
            : Observable<BaseResponse<AccountField>>

    @GET("account")
    fun getAccount(): Observable<BaseResponse<AccountField>>

    @PUT("account")
    @Headers("Content-Type: application/json")
    fun putAccount(@Body accountInfo: AccountField): Observable<BaseResponse<AccountField>>


    @GET("profiles")
    fun getProfile(
            @Query("current_profile") profile: String = "passenger")
            : Observable<BaseResponse<Profiles>>


    //  --


    @GET("transfers")
    fun getTransfers(): Observable<TransfersResponse>


    @GET("transfers/{id}")
    fun getTransfer(@Path("id") id: Int): ObservableResponse<TransferField>

    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(@Body transfer: NewTransferField): retrofit2.Call<NewTransferCreatedResponse>

    @POST("transfers/{id}/cancel")
    fun postCancelTransfer(@Path("id") id: Int): ObservableResponse<TransferField>

    @POST("transfers/{id}/restore")
    fun postRestoreTransfer(@Path("id") id: Int): ObservableResponse<TransferField>


    @GET("transfers/{id}/offers")
    fun getOffers(@Path("id") id: Int): ObservableResponse<OffersField>


    // --


    @GET("route_info")
    fun getRouteInfo(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("return_way") back: Boolean,
            @Query("with_prices") with_prices: Boolean = true)
            : Observable<BaseResponse<RouteInfoRaw>>

    @GET("transport_types_prices")
    fun getPrices(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("metric_value") timeInSeconds: Int,
            @Query("hourly") hourly: Boolean = true)
            : Observable<BaseResponse<Map<String, PriceRange>>>


    // --


    @GET("promo_codes")
    fun checkPromo(@Query("value") code: String): ObservableResponse<String>


    // --


    @FormUrlEncoded
    @POST("payments")
    fun payment(
            @Field("transfer_id") transfer: Int,
            @Field("offer_id") offer: Int,
            @Field("gateway_id") gate: String,
            @Field("percentage") percentage: Int)
            : Observable<BaseResponse<Payment>>

    @GET("ping")
    fun ping(
            @Query("payment_id") payment: Int?,
            @Query("transfer_id") transfer: Int?,
            @Query("transfer_ids[]") transfers: Array<Int>?)
            : Observable<BaseResponse<PingResponse>>
}