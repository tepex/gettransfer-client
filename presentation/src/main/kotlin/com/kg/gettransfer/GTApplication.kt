package com.kg.gettransfer

//import android.app.Application
import android.content.pm.ApplicationInfo
import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.kg.gettransfer.di.appModules

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
		}
		// Start Koin
		startKoin(this, appModules)
	}
}
