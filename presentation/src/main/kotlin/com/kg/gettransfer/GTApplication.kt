package com.kg.gettransfer

import android.content.Intent

import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.kg.gettransfer.cache.cacheModule
import com.kg.gettransfer.data.dataModule
import com.kg.gettransfer.di.*
import com.kg.gettransfer.remote.remoteModule

import com.kg.gettransfer.presentation.FileLoggingTree

import com.kg.gettransfer.service.SocketIOService
import com.squareup.leakcanary.LeakCanary

import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

import net.hockeyapp.android.CrashManager

import org.koin.android.ext.android.startKoin

import timber.log.Timber

class GTApplication: MultiDexApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        // Display some logs
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        } else CrashManager.register(this)
        if(BuildConfig.FLAVOR == "dev") {
            Timber.plant(FileLoggingTree(applicationContext))
            System.setProperty("kotlinx.coroutines.debug", "on")
            //DELETE CrashManager.register(this)
        }
        // Start Koin
        startKoin(this, listOf(ciceroneModule,
                               geoModule,
                               prefsModule,
                               loggingModule,
                               remoteModule,
                               cacheModule,
                               dataModule,
                               domainModule,
                               androidModule))

        startService(Intent(this, SocketIOService::class.java))
        setupAppMetrica()
        setUpLeakCanary()
    }

    private fun setUpLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun setupAppMetrica() {
        val config = YandexMetricaConfig
                .newConfigBuilder(getString(R.string.appmetrica_api_key))
                .withCrashReporting(false)
                .withLogs()
                .build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}
