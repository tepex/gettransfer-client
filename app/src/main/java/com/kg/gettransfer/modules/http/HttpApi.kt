package com.kg.gettransfer.modules.http


import com.kg.gettransfer.modules.http.json.*
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import io.realm.RealmList
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

    @GET("transfers/{id}")
    fun getTransfer(@Path("id") id: String) : Observable<Response<Transfer>>


    @GET("transfers/{id}/offers")
    fun getOffers(@Path("id") id: Int) : Observable<OffersResponse>
}