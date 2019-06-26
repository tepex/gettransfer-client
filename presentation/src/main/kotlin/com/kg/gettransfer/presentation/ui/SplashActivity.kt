package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper

import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.BuildConfig
import net.hockeyapp.android.CrashManager
import net.hockeyapp.android.CrashManagerListener
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.SplashPresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SplashView
import com.kg.gettransfer.utilities.AppLifeCycleObserver
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

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
        presenter.onLaunchContinue()

        Timber.d(getString(R.string.title_starting_session))
    }

    override fun checkLaunchType() {
        /* Check PUSH notification */
        intent.extras?.let {
            if (it.containsKey("new_offer")) {
                presenter.enterByPush()
                return
            }
        }
    }

    override fun initBuildConfigs(logsProvider: SplashPresenter.LateAccessLogs) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        if (!BuildConfig.DEBUG) {
            CrashManager.register(applicationContext,
                    object : CrashManagerListener() { override fun getDescription() = logsProvider.getLog() }
            )
        }
    }

    override fun onNeedAppUpdateInfo() {
        updateAppDialogIsShowed = true
        Utils.showAlertUpdateApp(this) {
            if (it) redirectToUpdateApp()
            else presenter.startApp()
        }
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        if (updateAppDialogIsShowed) presenter.startApp()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    private fun redirectToUpdateApp() {
        val url = getString(R.string.market_link) + getString(R.string.app_package)
        startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                })
    }

    override fun dispatchAppState() {
        val intent = Intent(AppLifeCycleObserver.APP_STATE).apply { putExtra(AppLifeCycleObserver.STATUS, true) }
        Handler().postDelayed({ LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent) }, 1000)
    }

    private fun checkIsTaskRoot(): Boolean {
        return if(!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != Intent.ACTION_MAIN) {
            finish()
            true
        } else false
    }

}
