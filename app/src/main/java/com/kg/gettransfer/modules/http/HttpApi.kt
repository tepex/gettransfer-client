package com.kg.gettransfer.modules.http


import com.kg.gettransfer.modules.http.json.*
import com.kg.gettransfer.realm.AccountInfo
import com.kg.gettransfer.realm.Config
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


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
    fun putAccount(@Body accountInfo: AccountInfo): Observable<BaseResponse<AccountField>>


    @GET("profiles")
    fun getProfile(
            @Query("current_profile") profile: String = "passenger")
            : Observable<BaseResponse<ProfileInfo>>


    //  --


    @GET("transfers")
    fun getTransfers(): Observable<TransfersResponse>


    @GET("transfers/{id}")
    fun getTransfer(@Path("id") id: Int): Observable<BaseResponse<TransferField>>

    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(@Body transfer: NewTransferField): retrofit2.Call<NewTransferCreatedResponse>

    @POST("transfers/{id}/cancel")
    fun postCancelTransfer(@Path("id") id: Int): Observable<BaseResponse<TransferField>>

    @POST("transfers/{id}/restore")
    fun postRestoreTransfer(@Path("id") id: Int): Observable<BaseResponse<TransferField>>


    @GET("transfers/{id}/offers")
    fun getOffers(@Path("id") id: Int): Observable<OffersResponse>


    // --


    @GET("transport_types_prices")
    fun getPrice(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("return_way") back: Boolean,
            @Query("metric_value") distance: Int,
            @Query("hourly") hourly: Boolean = false)
            : Observable<BaseResponse<Map<String, PriceRange>>>

    @GET("transport_types_prices")
    fun getPrice(
            @Query("points[]") points: Array<String>,
            @Query("date_to") date: String,
            @Query("metric_value") timeInSeconds: Int,
            @Query("hourly") hourly: Boolean = true)
            : Observable<BaseResponse<Map<String, PriceRange>>>


    // --


    @GET("promo_codes")
    fun checkPromo(@Query("value") code: String): Observable<Response<BaseResponse<String>>>


    // --


    @FormUrlEncoded
    @POST("payments")
    fun payment(
            @Field("transfer_id") transfer: Int,
            @Field("offer_id") offer: Int,
            @Field("gateway_id") gate: String,
            @Field("percentage") password: Int)
            : Observable<BaseResponse<Payment>>
}