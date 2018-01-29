package com.kg.gettransfer.network


import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface Api {
    @GET("transport_types")
    fun getTransportTypes(): Observable<TransportTypesResponse>

    @GET("access_token")
    fun getAccessToken(@Query("api_key") apiToken: String): Observable<Response<AccessToken>>

    @POST("/api/transfers")
    fun getTransfers(
            @Header("X-ACCESS-TOKEN") token: String,
            @Body transfer: TransferPOJO
    ): Observable<TransferResponse>

    companion object {
        val API_KEY = "23be10dcf06f280a4c0f8dca95434803"

        fun create(): Api {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://demo.gettransfer.com/api/")
                    .build()

            return retrofit.create(Api::class.java)
        }
    }
}