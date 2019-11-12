package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

import androidx.appcompat.widget.Toolbar

import android.webkit.WebViewClient

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.extensions.setUserAgent

import com.kg.gettransfer.presentation.presenter.WebPagePresenter

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.WebPageView

import com.kg.gettransfer.sys.presentation.ConfigsManager

import kotlinx.android.synthetic.main.activity_web_page.*
import kotlinx.android.synthetic.main.toolbar.view.*

import kotlinx.coroutines.launch
//import leakcanary.AppWatcher

import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class WebPageActivity : MvpAppCompatActivity(), WebPageView {
    var navigator = SupportAppNavigator(this, Screens.NOT_USED)
    val navigatorHolder: NavigatorHolder by inject()
    private val worker: WorkerManager by inject { parametersOf("WebPage") }
    private val configsManager: ConfigsManager by inject()

    @InjectPresenter
    internal lateinit var presenter: WebPagePresenter

    @ProvidePresenter
    fun createWebPagePresenter() = WebPagePresenter()

    @SuppressLint("SetJavaScriptEnabled")
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_page)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object : WebViewClient() {}

        worker.main.launch {
            when (intent.getStringExtra(WebPageView.EXTRA_SCREEN)) {
                WebPageView.SCREEN_LICENSE -> initActivity(
                    R.string.LNG_RIDE_OFFERT_TITLE,
                    SystemUtils.getUrlWithLocale(configsManager.getPreferences().endpoint!!.url).plus("/${configsManager.getMobileConfigs().termsUrl}")
                )
                WebPageView.SCREEN_REG_CARRIER -> initActivity(
                    R.string.LNG_RIDE_CREATE_CARRIER,
                    SystemUtils.getUrlWithLocale(configsManager.getPreferences().endpoint!!.url).plus(getString(R.string.api_url_carrier_new))
                )
                WebPageView.SCREEN_CARRIER -> initActivity(
                    R.string.LNG_RIDE_CREATE_CARRIER,
                    SystemUtils.getUrlWithLocale(configsManager.getPreferences().endpoint!!.url).plus(getString(R.string.api_url_carrier))
                )
                WebPageView.SCREEN_RESTORE_PASS -> initActivity(
                    R.string.LNG_LOGIN_RECOVERY_PASSWORD,
                    SystemUtils.getUrlWithLocale(configsManager.getPreferences().endpoint!!.url).plus(getString(R.string.api_url_restore_password))
                )
                WebPageView.SCREEN_TRANSFERS -> initActivity(
                    null,
                    SystemUtils.getUrlWithLocale(configsManager.getPreferences().endpoint!!.url).plus(getString(R.string.api_url_transfers)),
                    configsManager.getPreferences().endpoint!!.url
                )
            }
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    @CallSuper
    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun initActivity(@StringRes title: Int?, strUrl: String, stringTitle: String?) {
        (toolbar as Toolbar).toolbar_title.text = stringTitle ?: title?.let { getString(it) }
        webView.loadUrl(strUrl)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            presenter.onBackCommandClick()
            navigatorHolder.removeNavigator()
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }
}
