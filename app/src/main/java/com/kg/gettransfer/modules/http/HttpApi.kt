package com.kg.gettransfer.modules.http


import com.kg.gettransfer.modules.http.json.*
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface HttpApi {
    @GET("transport_types")
    fun getTransportTypes(): Observable<TransportTypesResponse>


    // --


    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String)
            : Observable<BooleanResponse>

    @GET("profiles")
    fun getProfile(
            @Query("current_profile") profile: String = "passenger")
            : Observable<Response<ProfileInfo>>


    //  --


    @GET("transfers")
    fun getTransfers(): Observable<TransfersResponse>


    @GET("transfers/{id}")
    fun getTransfer(@Path("id") id: Int): Observable<Response<TransferField>>

    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(@Body transfer: NewTransferField): retrofit2.Call<NewTransferCreatedResponse>

    @POST("transfers/{id}/cancel")
    fun postCancelTransfer(@Path("id") id: Int): Observable<Response<TransferField>>

    @POST("transfers/{id}/restore")
    fun postRestoreTransfer(@Path("id") id: Int): Observable<Response<TransferField>>


    @GET("transfers/{id}/offers")
    fun getOffers(@Path("id") id: Int): Observable<OffersResponse>


    // --


    @GET("transport_types_prices")
    fun getPrice(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("metric_value") distance: Int,
            @Query("return_way") back: Boolean,
            @Query("hourly") hourly: Boolean = false)
            : Observable<Response<Map<String, PriceRange>>>

    @GET("transport_types_prices")
    fun getPrice(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("metric_value") timeInSeconds: Int,
            @Query("hourly") hourly: Boolean = true)
            : Observable<Response<Map<String, PriceRange>>>


    // --


    @GET("promo_codes")
    fun checkPromo(@Query("value") code: String): Observable<Response<String>>


    // --


    @FormUrlEncoded
    @POST("payments")
    fun payment(
            @Field("transfer_id") transfer: Int,
            @Field("offer_id") offer: Int,
            @Field("gateway_id") gate: String,
            @Field("percentage") password: Int)
            : Observable<Response<Payment>>
}