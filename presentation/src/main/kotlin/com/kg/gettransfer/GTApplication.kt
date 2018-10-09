package com.kg.gettransfer

import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication

import com.kg.gettransfer.di.*
import com.kg.gettransfer.presentation.FileLoggingTree

import org.koin.android.ext.android.startKoin

import timber.log.Timber

class GTApplication: MultiDexApplication() {

//class GTApplication: Application() {
	@CallSuper
	override fun onCreate() {
		super.onCreate()
		// Display some logs
		if(BuildConfig.FLAVOR == "dev") {
			//Timber.plant(Timber.DebugTree())
			Timber.plant(FileLoggingTree(applicationContext))
			System.setProperty("kotlinx.coroutines.debug", "on")
		}
		// Start Koin
		startKoin(this, listOf(appModule, ciceroneModule, domainModule, androidModule))
	}
}
