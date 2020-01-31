package com.kg.gettransfer

import androidx.annotation.CallSuper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication

import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

import com.google.firebase.FirebaseApp

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

        setupFirebase()
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
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

    private fun setupAppsFlyer() {
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map { Timber.d("onAppOpen_attribute: ${it.key} = ${it.value}") }
            }

            override fun onAttributionFailure(error: String?) {
                Timber.e("error onAttributionFailure :  $error")
            }

            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Timber.i("conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Timber.e("error onAttributionFailure :  $error")
            }
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

    private fun setupFirebase() {
        FirebaseApp.initializeApp(this)
    }
}
