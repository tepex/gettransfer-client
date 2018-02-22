package com.kg.gettransfer.modules.http

import okhttp3.OkHttpClient
import org.koin.standalone.KoinComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by denisvakulenko on 06/02/2018.
 */

object HttpApiFactory : KoinComponent {
    //val BASE_URL = "https://demo.gettransfer.com/api/"
    val BASE_URL = "https://test.gettransfer.com/api/"

    fun create(httpClient: OkHttpClient, rx2call: RxJava2CallAdapterFactory, gsonConverterFactory: GsonConverterFactory): HttpApi {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(rx2call)
                .addConverterFactory(gsonConverterFactory)
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()

        return retrofit.create(HttpApi::class.java)
    }

    fun buildHttpClient(provideAccessTokenInterceptor: ProvideAccessTokenInterceptor) =
            OkHttpClient.Builder()
                    .addInterceptor(provideAccessTokenInterceptor)
                    .build()
}