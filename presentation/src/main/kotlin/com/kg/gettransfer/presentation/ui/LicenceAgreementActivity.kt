package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar
import android.webkit.WebViewClient
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LicenceAgreementPresenter
import com.kg.gettransfer.presentation.view.LicenceAgreementView
import kotlinx.android.synthetic.main.activity_licence_agreement.*
import kotlinx.android.synthetic.main.toolbar.view.*

class LicenceAgreementActivity: MvpAppCompatActivity(), LicenceAgreementView {
    @InjectPresenter
    internal lateinit var presenter: LicenceAgreementPresenter

    companion object {
        @JvmStatic val LICENCE_AGREEMENT_URL = "https://gettransfer.com/en/terms_of_use"
    }

    @SuppressLint("SetJavaScriptEnabled")
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_licence_agreement)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
        //(toolbar as Toolbar).setTitle(R.string.licence_agreement)
        (toolbar as Toolbar).toolbar_title.setText(R.string.licence_agreement)

        wvLicenceAgreement.settings.javaScriptEnabled = true
        wvLicenceAgreement.webViewClient = object: WebViewClient(){}
        wvLicenceAgreement.loadUrl(LICENCE_AGREEMENT_URL)
    }

    override fun onBackPressed() {
        if(wvLicenceAgreement.canGoBack()) wvLicenceAgreement.goBack()
        else presenter.onBackCommandClick()
    }
}