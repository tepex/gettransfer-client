package com.kg.gettransfer

import android.app.Application
import android.support.multidex.MultiDexApplication
import android.content.pm.ApplicationInfo
import android.support.annotation.CallSuper

import com.kg.gettransfer.module.TransfersModel

import org.koin.core.Koin
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

import timber.log.Timber

class GTApplication: MultiDexApplication() {
	@CallSuper
	override fun onCreate() {
		super.onCreate()
		// Display some logs
		if(BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
		// Start Koin
		startKoin(this, appModules)
	}
}
