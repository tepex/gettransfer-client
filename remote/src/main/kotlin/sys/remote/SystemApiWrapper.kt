package com.kg.gettransfer.sys.remote

import com.google.gson.GsonBuilder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import com.kg.gettransfer.remote.PrivateHttpLoggingInterceptor
import com.kg.gettransfer.remote.TransportTypesDeserializer
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.KoinComponent

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SystemApiWrapper : KoinComponent {

    internal lateinit var api: SystemApi
        private set

    internal val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer())
        .registerTypeAdapter(ContactEmailsWrapperModel::class.java, ContactEmailsDeserializer())
        .create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(PrivateHttpLoggingInterceptor())
        cookieJar(CookieJar.NO_COOKIES)
    }.build()

    fun changeEndpoint(endpoint: String) {
        api = Retrofit.Builder().apply {
            baseUrl(endpoint)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(gson))
            addCallAdapterFactory(CoroutineCallAdapterFactory())
        }.build().create(SystemApi::class.java)
    }
}
