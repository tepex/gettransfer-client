package com.kg.gettransfer.modules.http


import com.kg.gettransfer.modules.http.json.*
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import retrofit2.http.*
import java.util.*


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


    //  --


    @GET("transfers")
    fun getTransfers(): Observable<TransfersResponse>


    @GET("transfers/{id}")
    fun getTransfer(@Path("id") id: Int): Observable<Response<Transfer>>

    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(@Body transfer: NewTransferField): retrofit2.Call<NewTransferCreatedResponse>

    @POST("transfers/{id}/cancel")
    fun postCancelTransfer(@Path("id") id: Int): Observable<Response<Transfer>>

    @POST("transfers/{id}/restore")
    fun postRestoreTransfer(@Path("id") id: Int): Observable<Response<Transfer>>


    @GET("transfers/{id}/offers")
    fun getOffers(@Path("id") id: Int): Observable<OffersResponse>


    // --


    @GET("transport_types_prices")
    fun getPrice(
            @Query("points[]") points: Array<String>,
            @Query("hourly") hourly: Boolean,
            @Query("metric_value") distance: Int,
            @Query("return_way") back: Boolean,
            @Query("date_to") date: Date)
            : Observable<Response<Map<Int, PricesPreview>>>


    // --


    @GET("promo_codes")
    fun checkPromo(@Query("value") code: String): Observable<Response<String>>
}