package com.kg.gettransfer


import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.activity.login.LoginContract
import com.kg.gettransfer.activity.login.LoginPresenter
import com.kg.gettransfer.fragment.CreateTransferFragment
import com.kg.gettransfer.fragment.TransfersFragment
import com.kg.gettransfer.modules.*
import com.kg.gettransfer.modules.googleapi.GeoAutocompleteProvider
import com.kg.gettransfer.modules.googleapi.GeoUtils
import com.kg.gettransfer.modules.googleapi.GoogleApiClientFactory
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.HttpApiFactory
import com.kg.gettransfer.modules.http.ProvideAccessTokenInterceptor
import org.koin.dsl.module.applicationContext
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Koin main module
 */


val AppModule = applicationContext {
    // Util

    bean { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }


    // Http

    bean { RxJava2CallAdapterFactory.create() }
    bean {
        val format = "yyyy-MM-dd'T'HH:mm:ssZ"
        val gson = GsonBuilder()
                .setDateFormat(format)
                .create()
        val dateTypeAdapter = gson.getAdapter(Date::class.java)
        GsonBuilder()
                .setDateFormat(format)
                .registerTypeAdapter(Date::class.java, dateTypeAdapter.nullSafe())
                .create() as Gson
    }
    bean { GsonConverterFactory.create(get()) as GsonConverterFactory }

    bean { ProvideAccessTokenInterceptor(get(), get(), get()) }
    bean { HttpApiFactory.buildHttpClient(get()) as okhttp3.OkHttpClient }
    bean { HttpApiFactory.create(get(), get(), get()) as HttpApi }

    bean { Session(get()) }

    bean { CurrentAccount(get(), get()) }
    bean { ProfileModel(get(), get()) }


    // Google api

    bean { GoogleApiClientFactory.create(get()) }
    bean { GeoAutocompleteProvider() }

    bean {
        GeoApiContext.Builder()
                .queryRateLimit(10)
                .apiKey(get<Context>().getString(R.string.geoapikey))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build() as GeoApiContext
    }

    factory { GeoUtils(get(), get()) }

    bean { Geocoder(get(), Locale.getDefault()) }


    // Data

    bean { DBProvider(get()) }
    factory { get<DBProvider>().create() }

    bean { TransfersModel(get(), get(), get()) }

    bean { TransportTypes(get(), get()) }


    // Models

    factory { TransferModel(get(), get()) }
    factory { PricesPreviewModel(get(), get()) }
    factory { PromoCodeModel(get()) }


    // UI

    factory { CreateTransferFragment() }

    factory { LoginActivity() }
    factory { LoginPresenter(get()) as LoginContract.Presenter }

    factory { TransfersFragment() }
}


/**
 * Module list
 */

val appModules = listOf(AppModule)


