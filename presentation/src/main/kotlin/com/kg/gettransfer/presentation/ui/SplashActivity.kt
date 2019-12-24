package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler

import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.SplashPresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SplashView

import com.kg.gettransfer.utilities.AppLifeCycleObserver
// import leakcanary.AppWatcher

import java.util.Locale

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

import timber.log.Timber

class SplashActivity : MvpAppCompatActivity(), SplashView {
    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @ProvidePresenter
    fun createSplashPresenter() = SplashPresenter()

    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator = SupportAppNavigator(this, Screens.NOT_USED)

    private var updateAppDialogIsShowed = false

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (checkIsTaskRoot()) {
            return
        }
        setStatusBarColor(R.color.colorPrimary)
        presenter.onLaunchContinue()

        Timber.d(getString(R.string.title_starting_session))
    }

    override fun checkLaunchType() {
        /* Check PUSH notification */
        intent.extras?.let { bundle ->
            if (bundle.containsKey("new_offer")) {
                presenter.enterByPush()
                return
            }
        }
    }

    override fun onNeedAppUpdateInfo() {
        updateAppDialogIsShowed = true

        AlertDialog.Builder(this).apply {
            setTitle(R.string.LNG_NEW_VERSION_UPDATE)
            setPositiveButton(R.string.LNG_UPDATE)  { _, _ -> redirectToUpdateApp() }
            setNegativeButton(R.string.LNG_CANCEL)  { _, _ -> presenter.startApp() }
            setOnCancelListener { presenter.startApp() }
            show()
        }
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        if (updateAppDialogIsShowed) {
            presenter.startApp()
        }
    }

    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun redirectToUpdateApp() {
        val url = getString(R.string.market_link) + getString(R.string.app_market_package)
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
    }

    override fun dispatchAppState(locale: Locale) {
        val intent = Intent(AppLifeCycleObserver.APP_STATE).apply { putExtra(AppLifeCycleObserver.STATUS, true) }
        Handler().postDelayed(
            { LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent) },
            DELAY_TO_SEND_BROADCAST
        )
    }

    private fun checkIsTaskRoot(): Boolean {
        return if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != Intent.ACTION_MAIN) {
            finish()
            true
        } else {
            false
        }
    }

    private fun setStatusBarColor(@ColorRes color: Int) {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        private const val DELAY_TO_SEND_BROADCAST = 1000L
    }
}
