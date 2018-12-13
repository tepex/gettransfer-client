package com.kg.gettransfer

import android.app.NotificationChannel
import android.app.NotificationManager

import android.content.Intent

import android.os.Build

import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

import com.kg.gettransfer.cache.cacheModule
import com.kg.gettransfer.data.dataModule
import com.kg.gettransfer.di.*
import com.kg.gettransfer.presentation.FileLoggingTree
import com.kg.gettransfer.remote.remoteModule

import com.squareup.leakcanary.LeakCanary

import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

import net.hockeyapp.android.CrashManager

import org.koin.android.ext.android.startKoin

import timber.log.Timber

class GTApplication : MultiDexApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        // Display some logs
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        } else CrashManager.register(this)
        if (BuildConfig.FLAVOR == "dev") {
            Timber.plant(FileLoggingTree(applicationContext))
            System.setProperty("kotlinx.coroutines.debug", "on")
            //DELETE CrashManager.register(this)
        }
        // Start Koin
        startKoin(this, listOf(
            ciceroneModule,
            geoModule,
            prefsModule,
            loggingModule,
            remoteMappersModule,
            remoteModule,
            cacheModule,
            dataModule,
            domainModule,
            mappersModule,
            androidModule
        ))

        setupAppMetrica()
        //setUpLeakCanary()
        setupFcm()
    }

    private fun setUpLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) LeakCanary.install(this)
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

    private fun setupFcm() {
        FirebaseApp.initializeApp(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.new_offer_notification_channel_id)
            val channelName = getString(R.string.new_offer_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }

        Timber.d("Subscribing to new offers")
        FirebaseMessaging.getInstance().subscribeToTopic("offers").addOnCompleteListener { subscribed ->
            Timber.d("subscribed: ${subscribed.isSuccessful}")

            // Get token
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
                if (!it.isSuccessful) {
                    Timber.w("getInstanceId failed", it.exception)
                    return@OnCompleteListener
                }
                Timber.d("[FCM token]: ${it.result?.token}")
            })
        }
    }
}
