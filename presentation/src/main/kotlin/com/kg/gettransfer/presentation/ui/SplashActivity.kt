package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.AboutView
import kotlinx.coroutines.Job
import net.hockeyapp.android.CrashManager
import net.hockeyapp.android.CrashManagerListener
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.Screens

class SplashActivity : AppCompatActivity() {
    companion object {
        @JvmField val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        @JvmField val PERMISSION_REQUEST = 2211
        val EXTRA_TRANSFER_ID = "transfer_id"
        val EXTRA_RATE = "rate"
        val EXTRA_SHOW_RATE = "show_rate"
    }

    private val compositeDisposable = Job()
    private val coroutineContexts: CoroutineContexts by inject()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    private val systemInteractor: SystemInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        if (!BuildConfig.DEBUG) {
            CrashManager.register(applicationContext, object : CrashManagerListener() {
                override fun getDescription() = systemInteractor.logs
            })
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
            /*val result = utils.asyncAwait { systemInteractor.coldStart() }

            if (result.error != null) {
                Timber.e(result.error!!)
                val msg = if (result.error!!.code == ApiException.NETWORK_ERROR)
                    getString(R.string.LNG_NETWORK_ERROR) else getString(R.string.err_server, result.error!!.details)
                Utils.showError(this@SplashActivity, true, msg) {
                    openNextScreen()
                }
            }
            else {
                openNextScreen()
                finish()
            }*/
            utils.asyncAwait { systemInteractor.coldStart() }
            openNextScreen()
            finish()
        }
    }

    private fun openNextScreen(){
        if (!systemInteractor.isOnboardingShowed) {
            systemInteractor.isOnboardingShowed = true
            startActivity(Intent(this@SplashActivity, AboutActivity::class.java)
                    .putExtra(AboutView.EXTRA_OPEN_MAIN, true).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
        else {
            when (systemInteractor.lastMode) {
                Screens.CARRIER_MODE -> startActivity(Intent(this@SplashActivity, CarrierTripsMainActivity::class.java))
                Screens.PASSENGER_MODE -> {
                    val transferId = intent.getLongExtra(SplashActivity.EXTRA_TRANSFER_ID, 0)
                    val rate = intent.getIntExtra(SplashActivity.EXTRA_RATE, 0)
                    val showRate = intent.getBooleanExtra(SplashActivity.EXTRA_SHOW_RATE, false)

                    startActivity(Intent(this@SplashActivity, MainActivity::class.java)
                            .apply {
                                putExtra(EXTRA_TRANSFER_ID, transferId)
                                putExtra(EXTRA_RATE, rate)
                                putExtra(EXTRA_SHOW_RATE, showRate)
                            })
                }
                else -> startActivity(Intent(this@SplashActivity, MainActivity::class.java))
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

    private fun shouldAskForRateApp() =
            when (systemInteractor.appEntersForMarketRate) {
                3    -> true
                9    -> true
                18   -> true
                else -> false
            }
}
