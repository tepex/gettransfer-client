package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

import timber.log.Timber

class SettingsActivity: MvpAppCompatActivity(), SettingsView {
    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter
    
	private val apiInteractor: ApiInteractor by inject()
	private val coroutineContexts: CoroutineContexts by inject()

    @ProvidePresenter
	fun createSettingsPresenter(): SettingsPresenter = SettingsPresenter(coroutineContexts, apiInteractor)


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
    
    override fun setDistanceUnits(distanceUnits: List<String>) {
        Utils.setDistanceUnitsDialogListener(this, layoutSettingsDistanceUnits, distanceUnits) { 
            selected -> presenter.changeDistanceUnit(selected) 
        }
    }
    
    override fun setCurrency(currency: String)         { tvSelectedCurrency.text = currency }
    override fun setLocale(locale: String)             { tvSelectedLanguage.text = locale }
    override fun setDistanceUnit(distanceUnit: String) { tvSelectedDistanceUnits.text = distanceUnit }
    
    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}
