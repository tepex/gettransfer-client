package com.kg.gettransfer

import android.content.Intent
import android.content.IntentFilter

import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.kg.gettransfer.di.*

import com.kg.gettransfer.presentation.FileLoggingTree
import com.kg.gettransfer.presentation.NetworkStateChangeReceiver

import com.kg.gettransfer.service.SocketIOService

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
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        registerReceiver(NetworkStateChangeReceiver(), intentFilter)
        // Start Koin
        startKoin(this, listOf(ciceroneModule,
                               geoModule,
                               prefsModule,
                               loggingModule,
                               remoteModule,
                               dataModule,
                               androidModule))

        startService(Intent(this, SocketIOService::class.java))
    }
}
