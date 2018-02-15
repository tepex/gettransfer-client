package com.kg.gettransfer


import com.kg.gettransfer.cabinet.TransfersListActivity
import com.kg.gettransfer.createtransfer.CreateTransferActivity
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.login.LoginContract
import com.kg.gettransfer.login.LoginPresenter
import com.kg.gettransfer.modules.*
import com.kg.gettransfer.modules.network.HttpApi
import com.kg.gettransfer.modules.network.HttpApiFactory
import com.kg.gettransfer.modules.network.ProvideAccessTokenInterceptor
import com.kg.gettransfer.views.GeoAutocompleteProvider
import org.koin.dsl.module.applicationContext
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Koin main module
 */

val AppModule = applicationContext {
    // Http

    provide { RxJava2CallAdapterFactory.create() }
    provide { GsonConverterFactory.create() }

    provide { ProvideAccessTokenInterceptor(get(), get(), get()) }
    provide { HttpApiFactory.buildHttpClient(get()) as okhttp3.OkHttpClient }
    provide { HttpApiFactory.create(get(), get(), get()) as HttpApi }

    provide { Session() }

    provide { CurrentAccount(get(), get()) }


    // Google api

    provide { GoogleApiClientFactory.create(get()) }
    provide { GeoAutocompleteProvider() }


    // Data

    provide { DBProvider(get()) }
    factory { get<DBProvider>().create() }

    provide { Transfers(get(), get(), get()) }

    provide { TransportTypesProvider(get(), get()) }

    factory { GeoAutocompleteAsync(get()) }


    // UI

    factory { CreateTransferActivity() }

    factory { LoginActivity() }
    factory { LoginPresenter(get()) as LoginContract.Presenter }

    factory { TransfersListActivity() }
}


/**
 * Module response
 */

val todoAppModules = listOf(AppModule)

object Properties {
    const val CURRENCY = "CURRENCY"
}

