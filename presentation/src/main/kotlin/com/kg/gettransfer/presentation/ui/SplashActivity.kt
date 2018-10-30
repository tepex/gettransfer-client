package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.content.Context

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

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.presentation.Screens

import kotlinx.coroutines.Job

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
    private val systemInteractor: SystemInteractor by inject()

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

        if (checkIsTaskRoot()) return

        Timber.d(getString(R.string.title_starting_session))
        Timber.d("Permissions granted!")
        utils.launchAsyncTryCatch({
            utils.asyncAwait { systemInteractor.coldStart() }
            when(systemInteractor.lastMode) {
                Screens.CARRIER_MODE -> startActivity(Intent(this@SplashActivity, CarrierTripsActivity::class.java))
                Screens.PASSENGER_MODE -> startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                else -> startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }, { e ->
            Utils.showError(this@SplashActivity, true, getString(R.string.err_server, e.message)) {
                startActivity(Intent(this@SplashActivity, SettingsActivity::class.java))
            }
            // @TODO: Показать ошибку. Учесть 401 — протухший ключ
        })
    }

    private fun checkIsTaskRoot(): Boolean {
        if(!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != Intent.ACTION_MAIN) {
            finish()
            return true
        }
        return false
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
