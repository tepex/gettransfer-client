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

import com.kg.gettransfer.presentation.presenter.WebPagePresenter
import com.kg.gettransfer.presentation.view.WebPageView

import kotlinx.android.synthetic.main.activity_web_page.*
import kotlinx.android.synthetic.main.toolbar.view.*

class WebPageActivity: MvpAppCompatActivity(), WebPageView {
    @InjectPresenter
    internal lateinit var presenter: WebPagePresenter

    private lateinit var screen: String

    @ProvidePresenter
    fun createWebPagePresenter() = WebPagePresenter(screen)

    companion object {
        @JvmField val SCREEN             = "screen"
        @JvmField val SCREEN_LICENSE     = "license_agreement"
        @JvmField val SCREEN_REG_CARRIER = "registration_carrier"
        @JvmField val SCREEN_CARRIER     = "carrier_mode"
    }

    @SuppressLint("SetJavaScriptEnabled")
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_web_page)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient(){}
        screen = intent.getStringExtra(SCREEN)

        super.onCreate(savedInstanceState)
    }

    override fun initActivity(@StringRes title: Int, @StringRes url: Int) {
        (toolbar as Toolbar).toolbar_title.text = getString(title)
        webView.loadUrl(getString(url))
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) webView.goBack()
        else presenter.onBackCommandClick()
    }
}
