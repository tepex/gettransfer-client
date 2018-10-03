package com.kg.gettransfer

import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.kg.gettransfer.di.*
import com.kg.gettransfer.presentation.FileLoggingTree

import org.koin.android.ext.android.startKoin

import timber.log.Timber

class GTApplication: MultiDexApplication() {
<<<<<<< HEAD
    @CallSuper
    override fun onCreate() {
        super.onCreate()
        // Display some logs
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        }
        if(BuildConfig.FLAVOR == "dev") {
            Timber.plant(FileLoggingTree(applicationContext))
            System.setProperty("kotlinx.coroutines.debug", "on")
        }
        // Start Koin
        startKoin(this, listOf(appModule,
                               ciceroneModule,
                               geoModule,
                               prefsModule,
                               systemModule,
                               domainModule,
                               androidModule))
    }
}
