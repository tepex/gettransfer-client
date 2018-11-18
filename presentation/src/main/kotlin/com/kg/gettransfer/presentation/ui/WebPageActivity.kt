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
        webView.webViewClient = object: WebViewClient() {}
        
        when(intent.getStringExtra(WebPageView.EXTRA_SCREEN)) {
            WebPageView.SCREEN_LICENSE     -> initActivity(R.string.LNG_RIDE_OFFERT_TITLE, R.string.licence_agreement_url)
            WebPageView.SCREEN_REG_CARRIER -> initActivity(R.string.LNG_RIDE_CREATE_CARRIER, R.string.registration_carrier_url)
            WebPageView.SCREEN_CARRIER     -> initActivity(R.string.LNG_RIDE_CREATE_CARRIER, R.string.carrier_mode)
        }
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
