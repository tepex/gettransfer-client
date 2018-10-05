package com.kg.gettransfer.presentation.ui

import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v7.widget.Toolbar

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*

class SettingsActivity: BaseActivity(), SettingsView {
    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter
    
    @ProvidePresenter
	fun createSettingsPresenter(): SettingsPresenter = SettingsPresenter(coroutineContexts, router, systemInteractor)
	
	protected override var navigator = object: BaseNavigator(this) {
        @CallSuper
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if (intent != null) return intent

            when (screenKey) {
                Screens.SHARE_LOGS -> return Intent(context, LogsActivity::class.java)
            }
            return null
        }
    }
	
	override fun getPresenter(): SettingsPresenter = presenter
	
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.nav_settings_title)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
        
        btnSignOut.setOnClickListener { presenter.onLogout() }
        layoutSettingsLogs.setOnClickListener { presenter.onLogsClicked() }

        //Not showing some layouts in release
        if(BuildConfig.FLAVOR != "dev"){
            layoutSettingsEndpoint.visibility = View.GONE
            layoutSettingsLogs.visibility = View.GONE
        }
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) {
        Utils.setCurrenciesDialogListener(this, layoutSettingsCurrency, currencies) { 
            selected -> presenter.changeCurrency(selected) 
        }
    }
    
    override fun setLocales(locales: List<LocaleModel>) {
        Utils.setLocalesDialogListener(this, layoutSettingsLanguage, locales) { 
            selected -> presenter.changeLocale(selected) 
        }
    }
    
    override fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>) {
        Utils.setDistanceUnitsDialogListener(this, layoutSettingsDistanceUnits, distanceUnits) { 
            selected -> presenter.changeDistanceUnit(selected) 
        }
    }

    override fun setEndpoints(urls: List<String>) {
        Utils.setEndpointsDialogListener(this, layoutSettingsEndpoint, urls) {
            selected -> presenter.changeEndpoint(selected)
        }
    }
    
    override fun setCurrency(currency: String)         { tvSelectedCurrency.text = currency }
    override fun setLocale(locale: String)             { tvSelectedLanguage.text = locale }
    override fun setDistanceUnit(distanceUnit: String) { tvSelectedDistanceUnits.text = distanceUnit }
    override fun setEndpoint(endpoint: String)         { tvSelectedEndpoint.text = endpoint }
    
    override fun setLogoutButtonEnabled(enabled: Boolean) {
        if(enabled) btnSignOut.visibility = View.VISIBLE else btnSignOut.visibility = View.GONE
    }

    override fun restartApp() {
        finish()
        val intent = baseContext.packageManager.getLaunchIntentForPackage( baseContext.packageName);
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
