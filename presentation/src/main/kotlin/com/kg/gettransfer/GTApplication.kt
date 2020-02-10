package com.kg.gettransfer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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
import com.kg.gettransfer.service.OneSignalNotificationOpenedHandler

import com.kg.gettransfer.sys.cache.systemCache
import com.kg.gettransfer.sys.data.systemData
import com.kg.gettransfer.sys.remote.systemRemote

import com.kg.gettransfer.utilities.AppLifeCycleObserver
import com.kg.gettransfer.utilities.GTNotificationManager

import com.onesignal.OneSignal

import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush

import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
// import leakcanary.AppWatcher
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import timber.log.Timber

class GTApplication : MultiDexApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        // Display some logs
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
//            setUpLeakCanary(false)
        } else {
            setupAppCenter()
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
                ipApiKey,

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

    private fun setupAppCenter() {
        // don't put App Secret to resources. see this task https://gettransfercom.atlassian.net/browse/GAA-2327
        val appSecret = if (BuildConfig.FLAVOR == "dev") {
            "1f061f58-b7bd-42c3-87ec-d8681d30df48"
        } else {
            "62939f09-f9e0-489c-8ada-1c25d2f30d9d"
        }
        AppCenter.start(this, appSecret, Crashes::class.java)
    }

    private fun setupPushSdk() {
        YandexMetricaPush.init(applicationContext)
        if (BuildConfig.DEBUG) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.ERROR)
        }
        // OneSignal Initialization
        OneSignal.startInit(this)
            .setNotificationOpenedHandler(OneSignalNotificationOpenedHandler(this))
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

    private fun setupAppsFlyer() {
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}

            override fun onAttributionFailure(p0: String?) {}

            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {}

            override fun onConversionDataFail(p0: String?) {}
        }
        AppsFlyerLib.getInstance().init(getString(R.string.appsflyer_api_key), conversionListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(this)
    }

    private fun setupSentry() {
        if (!BuildConfig.DEBUG) {
            Sentry.init(getString(R.string.sentryDsn), AndroidSentryClientFactory(applicationContext))
        }
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
            createNotificationChannel(
                getString(R.string.new_offer_notification_channel_id),
                getString(R.string.new_offer_notification_channel_name)
            )
            createNotificationChannel(GTNotificationManager.OFFER_CHANEL_ID)
            createNotificationChannel(GTNotificationManager.MESSAGE_CHANEL_ID)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(chanelId: String, chanelName: String? = null) {
        val name = chanelName ?: when (chanelId) {
            GTNotificationManager.OFFER_CHANEL_ID   -> getString(R.string.offer_channel_name)
            GTNotificationManager.MESSAGE_CHANEL_ID -> getString(R.string.message_channel_name)
            else              -> throw UnsupportedOperationException()
        }
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(chanelId, name, importance).apply {
            setShowBadge(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
