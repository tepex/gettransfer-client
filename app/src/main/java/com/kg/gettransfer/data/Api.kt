package com.kg.gettransfer.data


import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface Api {
    @GET("transport_types")
    fun getTransportTypes(): Observable<Response<List<TransportType>>>

    @GET("/api/transfers")
    fun getTransfers(@Query("api_key") apiKey: String, @Query("transfer[from][name]") from: String): Observable<Response<List<String>>>

    companion object {
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