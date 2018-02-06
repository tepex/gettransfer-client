package com.kg.gettransfer.modules.network


import com.kg.gettransfer.modules.network.json.*
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface HttpApi {
    @GET("transport_types")
    fun getTransportTypes()
            : Observable<TransportTypesResponse>


    // --


    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String)
            : Observable<BooleanResponse>


    //  --


    @GET("transfers")
    fun getTransfers()
            : Observable<TransfersResponse>


    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(
            @Body transfer: NewTransferField)
            : Observable<NewTransferCreatedResponse>
}