package com.kg.gettransfer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import androidx.annotation.CallSuper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication

import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

import com.kg.gettransfer.cache.cacheModule
import com.kg.gettransfer.data.dataModule
import com.kg.gettransfer.di.*

import com.kg.gettransfer.remote.remoteModule
import com.kg.gettransfer.remote.socketModule

import com.kg.gettransfer.sys.cache.systemCache
import com.kg.gettransfer.sys.data.systemData
import com.kg.gettransfer.sys.remote.systemRemote

import com.kg.gettransfer.utilities.AppLifeCycleObserver
import com.kg.gettransfer.utilities.CustomCrashManagerListener

import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush

import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
//import leakcanary.AppWatcher
import net.hockeyapp.android.CrashManager

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import timber.log.Timber

class GTApplication : MultiDexApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        // Display some logs
        if (BuildConfig.DEBUG) {
//           Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
//            setUpLeakCanary(false)
        } else {
            CrashManager.register(this, getString(R.string.hockeyAppId), CustomCrashManagerListener())
        }
        if (BuildConfig.FLAVOR == "dev") {
//            Timber.plant(FileLoggingTree(applicationContext))
            System.setProperty("kotlinx.coroutines.debug", "on")
        }
        // Start Koin
        startKoin {
            androidContext(this@GTApplication)
            modules(listOf(
                ciceroneModule,
                geoModule,
                prefsModule,
                remoteModule,
                cacheModule,
                dataModule,
                encryptModule,
                domainModule,

                endpoints,
                systemCache,
                systemRemote,
                systemData,
                systemDomain,
                systemPresentation,

                mappersModule,
                androidModule,
                socketModule
            ))
        }

        setupFcm()
        setupAppMetrica()
        setupSentry()
        setupAppsFlyer()
        setupPushSdk()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeCycleObserver(applicationContext))
    }

    private fun setupPushSdk() = YandexMetricaPush.init(applicationContext)

    private fun setupAppsFlyer() {
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}

            override fun onAttributionFailure(p0: String?) {}

            override fun onInstallConversionDataLoaded(p0: MutableMap<String, String>?) {}

            override fun onInstallConversionFailure(p0: String?) {}
        }
        AppsFlyerLib.getInstance().init(getString(R.string.appsflyer_api_key), conversionListener, applicationContext)
        AppsFlyerLib.getInstance().enableUninstallTracking(getString(R.string.sender_id))
        AppsFlyerLib.getInstance().startTracking(this)
    }

    private fun setupSentry() {
        Sentry.init(getString(R.string.sentryDsn), AndroidSentryClientFactory(applicationContext))
    }

//    private fun setUpLeakCanary(enable: Boolean) {
//        AppWatcher.config = AppWatcher.config.copy(enable)
//    }

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

            /*
            // Get token
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
                if (!it.isSuccessful) {
                    Timber.w("getInstanceId failed", it.exception)
                    return@OnCompleteListener
                }
                Timber.d("[FCM token]: ${it.result?.token}")
            })
            */
        }
    }
}
