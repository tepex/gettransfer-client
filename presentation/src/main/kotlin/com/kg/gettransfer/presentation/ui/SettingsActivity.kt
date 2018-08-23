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

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        val distanceUnits = arrayOf("Километры (km)", "Милли (ml)")

        //setDefaultTextInFields(currencies[0], languagies[0], 0)

        layoutSettingsCurrency.setOnClickListener { showDialogChangeCurrency() }
        layoutSettingsLanguage.setOnClickListener { showDialogChangeLanguage() }
        layoutSettingsDistanceUnits.setOnClickListener { showDialogChangeDistanceUnit() }
    }

    private fun setDefaultTextInFields(textCurrency:String, textLanguage: String, whichDistanceUnit: Int){
        /*
        presenter.changeCurrency(textCurrency)
        presenter.changeLanguage(textLanguage)
        presenter.changeDistanceUnit(whichDistanceUnit)
        */
    }

    private fun showDialogChangeCurrency() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.currency)
        builder.setItems(presenter.currencies.toTypedArray()) { _, which -> presenter.changeCurrency(which) }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private fun showDialogChangeLanguage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.application_language)
        builder.setItems(presenter.locales.toTypedArray()) { _, which -> presenter.changeLanguage(which) }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private fun showDialogChangeDistanceUnit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.distance_units)
        builder.setItems(presenter.configs.supportedDistanceUnits.toTypedArray()) { _, which -> presenter.changeDistanceUnit(which) }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }

    override fun setSettingsCurrency(textSelectedCurrency: String) {
        tvSelectedCurrency.text = textSelectedCurrency
    }

    override fun setSettingsLanguage(textSelectedLanguage: String) {
        tvSelectedLanguage.text = textSelectedLanguage
    }

    override fun setSettingsDistanceUnits(textSelectedDistanceUnit: String) {
        tvSelectedDistanceUnits.text = textSelectedDistanceUnit
    }
}