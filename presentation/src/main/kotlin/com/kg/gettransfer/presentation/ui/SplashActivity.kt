package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.coroutines.Job

import org.koin.android.ext.android.inject

import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    companion object {
        @JvmField val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        @JvmField val PERMISSION_REQUEST = 2211
    }

    private val compositeDisposable = Job()
    private val coroutineContexts: CoroutineContexts by inject()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    private val systemInteractor: SystemInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Permissions: ${systemInteractor.locationPermissionsGranted}")
        if (systemInteractor.locationPermissionsGranted == null &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            (!check(Manifest.permission.ACCESS_FINE_LOCATION) || !check(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST)
                // show splash
                Timber.d("Splash screen")
                return
        }

        reviewInteractor.shouldAskRateInMarket = shouldAskForRateApp()

        if(checkIsTaskRoot()) return

        Timber.d(getString(R.string.title_starting_session))
        Timber.d("Permissions granted!")

        /* Check PUSH notification */
        intent.extras?.let {
            if (it.containsKey("new_offer")) {
                startActivity(Intent(this, RequestsActivity::class.java))
                return
            }
        }

        utils.launchSuspend {
            val result = utils.asyncAwait { systemInteractor.coldStart() }

            if (result.error != null) {
                Timber.e(result.error!!)
                val msg = if (result.error!!.code == ApiException.NETWORK_ERROR)
                    getString(R.string.LNG_NETWORK_ERROR) else getString(R.string.err_server, result.error!!.details)
                Utils.showError(this@SplashActivity, true, msg) {
                    startActivity(Intent(this@SplashActivity, SettingsActivity::class.java))
                }
            }
            else {
                if (!systemInteractor.isOnboardingShowed) {
                    systemInteractor.isOnboardingShowed = true
                    startActivity(Intent(this@SplashActivity, AboutActivity::class.java)
                            .putExtra(AboutView.EXTRA_OPEN_MAIN, true).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
                }
                else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    /*when (systemInteractor.lastMode) {
                        Screens.CARRIER_MODE -> startActivity(Intent(this@SplashActivity, CarrierTripsActivity::class.java))
                        Screens.PASSENGER_MODE -> startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        else -> startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }*/
                }
                finish()
            }
        }
    }

    private fun checkIsTaskRoot(): Boolean {
        return if(!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != Intent.ACTION_MAIN) {
            finish()
            true
        } else false
    }

    @CallSuper
    protected override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    private fun check(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST) return
        systemInteractor.locationPermissionsGranted = (grantResults.size == 2 &&
                                                       grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                                       grantResults[1] == PackageManager.PERMISSION_GRANTED)
        recreate()
        /*
        if(grantResults.size == 2 &&
           grantResults[0] == PackageManager.PERMISSION_GRANTED &&
           grantResults[1] == PackageManager.PERMISSION_GRANTED) recreate()
        else finish()
        */
    }
    private fun shouldAskForRateApp() =
            when (systemInteractor.appEntersForMarketRate) {
                3    -> true
                9    -> true
                18   -> true
                else -> false
            }
}
