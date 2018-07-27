package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.location.Geocoder
import android.preference.PreferenceManager
/*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer

import com.google.maps.GeoApiContext

import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.activity.login.LoginContract
import com.kg.gettransfer.activity.login.LoginPresenter

import com.kg.gettransfer.fragment.CreateTransferFragment
import com.kg.gettransfer.fragment.TransfersFragment

import com.kg.gettransfer.di.googleapi.GeoAutocompleteProvider
import com.kg.gettransfer.di.googleapi.GeoUtils
import com.kg.gettransfer.di.googleapi.GoogleApiClientFactory

import com.kg.gettransfer.di.http.HttpApi
import com.kg.gettransfer.di.http.HttpApiFactory
import com.kg.gettransfer.di.http.ProvideAccessTokenInterceptor

import com.kg.gettransfer.realm.secondary.ZonedDate
*/

import org.koin.dsl.module.module
/*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
*/
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

import timber.log.Timber

/**
 * Koin main module
 */
val AppModule = module {
	// Util
	single { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
	/*
	// Http
	single { RxJava2CallAdapterFactory.create() }
	single {
		val format = "yyyy-MM-dd'T'HH:mm:ssZ"
		val df = SimpleDateFormat(format)
		df.timeZone = TimeZone.getTimeZone("UTC")
		
		val dateTypeAdapter = JsonDeserializer<Date> { json, _, _ ->
			try {
				df.parse(json?.asString)
			} catch(e: java.text.ParseException) {
				Timber.e(e, "Date deserialize error: $json")
				null
			}
		}

        val zonedDateTypeAdapter = JsonDeserializer<ZonedDate> { json, _, _ ->
            try {
                val s = json?.asString ?: return@JsonDeserializer null
                val date = df.parse(s)
                val tz = "GMT" + s.substring(s.length - 5)
                ZonedDate(date, tz)
            } catch (e: java.text.ParseException) {
                Timber.e(e, "Date deserialize error: $json")
                null
            }
        }

        GsonBuilder()
                .setDateFormat(format)
                .registerTypeAdapter(Date::class.java, dateTypeAdapter)
                .registerTypeAdapter(ZonedDate::class.java, zonedDateTypeAdapter)
                .excludeFieldsWithoutExposeAnnotation()
                .create() as Gson
    }
    single { GsonConverterFactory.create(get()) as GsonConverterFactory }

    single { ProvideAccessTokenInterceptor(get(), get(), get()) }
    single { HttpApiFactory.buildHttpClient(get()) as okhttp3.OkHttpClient }
    single { HttpApiFactory.create(get(), get(), get()) }

    single { Session(get()) }

    single { CurrentAccount(get(), get()) }
    single { ProfileModel(get(), get()) }


    // Google api

    single { GoogleApiClientFactory.create(get()) }
    single { GeoAutocompleteProvider() }

    single {
        GeoApiContext.Builder()
                .queryRateLimit(10)
                .apiKey(get<Context>().getString(R.string.geoapikey))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build() as GeoApiContext
    }

    factory { GeoUtils(get(), get()) }

    single { Geocoder(get(), Locale.getDefault()) }

    // Data
    single { DBProvider(get()) }
    factory { get<DBProvider>().create() }

    single { TransfersModel(get(), get(), get()) }

    single { TransportTypes(get(), get(), get()) }

    single { ConfigModel(get()) }


    // Models

    factory { TransferModel(get(), get()) }
    factory { OffersModel(get(), get()) }
    factory { RouteInfoModel(get(), get()) }
    factory { PromoCodeModel(get()) }
    factory { LocationModel(get(), get()) }


    // UI

    factory { CreateTransferFragment() }

    factory { LoginActivity() }
    factory { LoginPresenter(get()) as LoginContract.Presenter }

    factory { TransfersFragment() }
    */
}

val CiceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

/**
 * Module list
 */
val appModules = listOf(AppModule, CiceroneModule)
