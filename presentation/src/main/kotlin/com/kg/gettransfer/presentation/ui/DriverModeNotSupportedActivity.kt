package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.DriverModeNotSupportedPresenter
import com.kg.gettransfer.presentation.view.DriverModeNotSupportedView
import kotlinx.android.synthetic.main.activity_driver_mode_not_support.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

class DriverModeNotSupportedActivity : BaseActivity(), DriverModeNotSupportedView {

    @InjectPresenter
    internal lateinit var presenter: DriverModeNotSupportedPresenter

    @ProvidePresenter
    fun createDriverModeNotSupportPresenter() = DriverModeNotSupportedPresenter()

    override fun getPresenter(): DriverModeNotSupportedPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_mode_not_support)
        setupToolbar()
        btn_continue.setOnClickListener { Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package)) }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_ABOUT_DRIVER_APP)
    }
}