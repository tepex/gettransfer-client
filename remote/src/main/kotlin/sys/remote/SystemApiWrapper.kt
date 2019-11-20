package com.kg.gettransfer.sys.remote

import com.google.gson.GsonBuilder
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.remote.PrivateHttpLoggingInterceptor
import com.kg.gettransfer.remote.TransportTypesDeserializer
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.KoinComponent
import org.koin.core.get

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SystemApiWrapper : KoinComponent {

    private val preferences = get<PreferencesCache>()

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
            callFactory { okHttpClient.newCall(it) }
            addConverterFactory(GsonConverterFactory.create(gson))
        }.build().create(SystemApi::class.java)
    }

    fun checkApiInitialization() {
        if (!::api.isInitialized) {
            changeEndpoint(preferences.endpointUrl)
        }
    }
}
