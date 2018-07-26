package com.kg.gettransfer.module.http

import okhttp3.OkHttpClient
import org.koin.standalone.KoinComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by denisvakulenko on 06/02/2018.
 */

object HttpApiFactory : KoinComponent {
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