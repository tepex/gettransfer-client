package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

import android.webkit.WebViewClient
import androidx.core.view.isVisible

import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.extensions.setUserAgent

import com.kg.gettransfer.presentation.presenter.WebPagePresenter

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.WebPageView
import com.kg.gettransfer.sys.domain.GetMobileConfigsInteractor
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import kotlinx.android.synthetic.main.activity_web_page.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//import leakcanary.AppWatcher

import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class WebPageActivity : MvpAppCompatActivity(), WebPageView {
    private var navigator = SupportAppNavigator(this, Screens.NOT_USED)
    private val navigatorHolder: NavigatorHolder by inject()
    private val worker: WorkerManager by inject { parametersOf("WebPage") }
    private val getMobileConfigs: GetMobileConfigsInteractor by inject()
    private val getPreferences: GetPreferencesInteractor by inject()

    @InjectPresenter
    internal lateinit var presenter: WebPagePresenter

    @ProvidePresenter
    fun createWebPagePresenter() = WebPagePresenter()

    @SuppressLint("SetJavaScriptEnabled")
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)
        initToolbar()
        initWebView()
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

    private fun initToolbar() = with(toolbar) {
        setSupportActionBar(this)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
        }
        with(toolbar_btnBack) {
            isVisible = true
            setOnClickListener { presenter.onBackCommandClick() }
        }
    }

    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object : WebViewClient() {}

        worker.main.launch {
            val endpointUrl = withContext(worker.bg) { getPreferences().getModel() }.endpoint!!.url
            val pathUrl = when (intent.getStringExtra(WebPageView.EXTRA_SCREEN)) {
                WebPageView.SCREEN_LICENSE -> {
                    val termsUrl = withContext(worker.bg) { getMobileConfigs().getModel() }.termsUrl
                    setToolbarTitle(R.string.LNG_RIDE_OFFERT_TITLE)
                    "/$termsUrl"
                }
                WebPageView.SCREEN_REG_CARRIER -> {
                    setToolbarTitle(R.string.LNG_RIDE_CREATE_CARRIER)
                    getString(R.string.api_url_carrier_new)
                }
                WebPageView.SCREEN_CARRIER -> {
                    setToolbarTitle(R.string.LNG_RIDE_CREATE_CARRIER)
                    getString(R.string.api_url_carrier)
                }
                WebPageView.SCREEN_RESTORE_PASS -> {
                    setToolbarTitle(R.string.LNG_LOGIN_RECOVERY_PASSWORD)
                    getString(R.string.api_url_restore_password)
                }
                WebPageView.SCREEN_TRANSFERS -> {
                    setToolbarTitle(titleText = endpointUrl)
                    getString(R.string.api_url_transfers)
                }
                else -> null
            }
            val webViewUrl = SystemUtils.getUrlWithLocale(endpointUrl).let { baseUrl ->
                pathUrl?.let { baseUrl.plus(pathUrl) } ?: baseUrl
            }
            webView.loadUrl(webViewUrl)
        }
    }

    private fun setToolbarTitle(@StringRes titleId: Int? = null, titleText: String? = null) {
        toolbar_title.text = titleText ?: titleId?.let { getString(it) }
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
