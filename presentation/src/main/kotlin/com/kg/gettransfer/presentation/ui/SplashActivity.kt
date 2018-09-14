package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.pm.PackageManager
import android.content.Intent

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import java.util.Currency
import java.util.Locale

import kotlinx.coroutines.experimental.Job

import org.koin.android.ext.android.inject

import timber.log.Timber

class SplashActivity: AppCompatActivity() {
	companion object {
		@JvmField val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
		@JvmField val PERMISSION_REQUEST = 2211
	}
	
	private val compositeDisposable = Job()
	private val coroutineContexts: CoroutineContexts by inject()
	private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
	private val apiInteractor: ApiInteractor by inject()
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && 
			(!check(Manifest.permission.ACCESS_FINE_LOCATION) || 
			 !check(Manifest.permission.ACCESS_COARSE_LOCATION))) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST)
			// show splash
			Timber.d("Splash screen")
			return
		}

		Timber.d("Permissions granted!")
		utils.launchAsyncTryCatchFinally({
		    utils.asyncAwait { apiInteractor.coldStart() }
			startActivity(Intent(this@SplashActivity, MainActivity::class.java))
		}, { e -> Utils.showError(this@SplashActivity, false, getString(R.string.err_server, e.message))
			// @TODO: Показать ошибку. Учесть 401 — протухший ключ
		}, { finish() })
	}
	
	@CallSuper
	protected override fun onDestroy() {
		compositeDisposable.cancel()
		super.onDestroy()
	}

	private fun check(permission: String) =
		ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if(requestCode != PERMISSION_REQUEST) return
		if(grantResults.size == 2 && 
			grantResults[0] == PackageManager.PERMISSION_GRANTED &&
			grantResults[1] == PackageManager.PERMISSION_GRANTED) recreate()
		else finish()
	}
}
