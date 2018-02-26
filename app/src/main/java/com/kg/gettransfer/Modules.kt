package com.kg.gettransfer


import android.content.SharedPreferences
import android.location.Geocoder
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kg.gettransfer.createtransfer.CreateTransferActivity
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.login.LoginContract
import com.kg.gettransfer.login.LoginPresenter
import com.kg.gettransfer.modules.*
import com.kg.gettransfer.modules.googleapi.GeoAutocompleteProvider
import com.kg.gettransfer.modules.googleapi.GeoUtils
import com.kg.gettransfer.modules.googleapi.GoogleApiClientFactory
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.HttpApiFactory
import com.kg.gettransfer.modules.http.ProvideAccessTokenInterceptor
import com.kg.gettransfer.transfers.TransfersActivity
import org.koin.dsl.module.applicationContext
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


/**
 * Koin main module
 */


val AppModule = applicationContext {
    // Util

    provide { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }


    // Http

    provide { RxJava2CallAdapterFactory.create() }
    provide { GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create() as Gson }
    provide { GsonConverterFactory.create(get()) as GsonConverterFactory }

    provide { ProvideAccessTokenInterceptor(get(), get(), get()) }
    provide { HttpApiFactory.buildHttpClient(get()) as okhttp3.OkHttpClient }
    provide { HttpApiFactory.create(get(), get(), get()) as HttpApi }

    provide { Session(get()) }

    provide { CurrentAccount(get(), get()) }


    // Google api

    provide { GoogleApiClientFactory.create(get()) }
    provide { GeoAutocompleteProvider() }

    factory { GeoUtils(get(), get()) }

    provide { Geocoder(get(), Locale.getDefault()) }


    // Data

    provide { DBProvider(get()) }
    factory { get<DBProvider>().create() }

    provide { Transfers(get(), get(), get()) }

    provide { TransportTypesProvider(get(), get()) }


    // Models

    factory { TransferModel(get(), get()) }


    // UI

    factory { CreateTransferActivity() }

    factory { LoginActivity() }
    factory { LoginPresenter(get()) as LoginContract.Presenter }

    factory { TransfersActivity() }
}


/**
 * Module list
 */

val todoAppModules = listOf(AppModule)

object Properties {
    const val CURRENCY = "CURRENCY"
}

