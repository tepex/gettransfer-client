package com.kg.gettransfer.presentation.ui

import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.app.MemoryLeakUtils
import android.support.v7.widget.Toolbar

import android.view.View

import android.widget.Toast

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.SettingsPresenter

import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*

import timber.log.Timber

class SettingsActivity : BaseActivity(), SettingsView {

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun getPresenter(): SettingsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_SETTINGS)

        btnSignOut.setOnClickListener { presenter.onLogout() }
        layoutSettingsLogs.setOnClickListener { presenter.onLogsClicked() }
        layoutSettingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        btnSupport.setOnClickListener { presenter.sendEmail(null) }
        rlResetMarketRate.setOnClickListener { presenter.onResetRateClicked() }

        //Not showing some layouts in release
        if (BuildConfig.FLAVOR != "dev") {
            layoutSettingsEndpoint.isVisible = false
            layoutSettingsLogs.isVisible = false
            layoutSettingsResetOnboarding.isVisible = false
            rlResetMarketRate.isVisible = false
        }
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) =
        Utils.setCurrenciesDialogListener(this, layoutSettingsCurrency, currencies) { presenter.changeCurrency(it) }

    override fun setLocales(locales: List<LocaleModel>) =
        Utils.setLocalesDialogListener(this, layoutSettingsLanguage, locales) {
            localeManager.updateResources(this, presenter.changeLocale(it))
            recreate()
        }

    override fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>) =
        Utils.setDistanceUnitsDialogListener(this, layoutSettingsDistanceUnits, distanceUnits) { presenter.changeDistanceUnit(it) }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
        Utils.setEndpointsDialogListener(this, layoutSettingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCurrency(currency: String)         { tvSelectedCurrency.text = currency }
    override fun setLocale(locale: String)             { tvSelectedLanguage.text = locale }
    override fun setDistanceUnit(distanceUnit: String) { tvSelectedDistanceUnits.text = distanceUnit }
    override fun setEndpoint(endpoint: EndpointModel)  { tvSelectedEndpoint.text = endpoint.name }

    override fun setLogoutButtonEnabled(enabled: Boolean) {
        btnSignOut.isVisible = enabled
    }

    override fun restartApp() {
        baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        }
        finish()
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}
