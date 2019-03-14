package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v7.widget.Toolbar

import android.webkit.WebViewClient

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setUserAgent

import com.kg.gettransfer.presentation.presenter.WebPagePresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.WebPageView

import kotlinx.android.synthetic.main.activity_web_page.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class WebPageActivity: MvpAppCompatActivity(), WebPageView {
    var navigator = SupportAppNavigator(this, Screens.NOT_USED)
    val navigatorHolder: NavigatorHolder by inject()

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
        webView.webViewClient = object: WebViewClient() {}

        when(intent.getStringExtra(WebPageView.EXTRA_SCREEN)) {
            WebPageView.SCREEN_LICENSE      -> initActivity(R.string.LNG_RIDE_OFFERT_TITLE, WebPageView.INIT_WITH_STRING, createLicenceUrl())
            WebPageView.SCREEN_REG_CARRIER  -> initActivity(R.string.LNG_RIDE_CREATE_CARRIER, R.string.registration_carrier_url)
            WebPageView.SCREEN_CARRIER      -> initActivity(R.string.LNG_RIDE_CREATE_CARRIER, R.string.carrier_mode)
            WebPageView.SCREEN_RESTORE_PASS -> initActivity(R.string.LNG_LOGIN_RECOVERY_PASSWORD, WebPageView.INIT_WITH_STRING, createRestorePassUrl())
            WebPageView.SCREEN_TRANSFERS    -> initActivity(R.string.api_url_prod, WebPageView.INIT_WITH_STRING, createTransferUrl())
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

    override fun initActivity(@StringRes title: Int, @StringRes url: Int, strUrl: String?) {
        (toolbar as Toolbar).toolbar_title.text = getString(title)
        strUrl?.let {
            webView.loadUrl(it)
            return
        }
        webView.loadUrl(getString(url))
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) webView.goBack()
        else presenter.onBackCommandClick()
    }

    private fun createLicenceUrl() =
            SystemUtils.gtUrlWithLocale(this)
                    .plus(presenter.termsUrl)

    private fun createRestorePassUrl() =
            SystemUtils.gtUrlWithLocale(this)
                    .plus(getString(R.string.api_restore_password))

    private fun createTransferUrl() =
            SystemUtils.gtUrlWithLocale(this)
                    .plus(getString(R.string.api_url_transfers))

}
