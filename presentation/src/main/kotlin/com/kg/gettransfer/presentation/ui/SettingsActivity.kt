package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_settings_field.view.*
import java.lang.UnsupportedOperationException

class SettingsActivity : BaseActivity(), SettingsView {

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun getPresenter(): SettingsPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_SETTINGS)
        Utils.dpToPxInt(this, 8f).let {
            settingsBtnLogout.btnName.setPadding(0, it, 0, 0)
            settingsBtnSupport.btnName.setPadding(0, it, 0, 0)
        }
    }

    @SuppressLint("CommitTransaction")
    override fun showCurrenciesFragment(show: Boolean) {
        with(supportFragmentManager.beginTransaction()) {
            if (show) {
                presenter.currenciesFragmentIsShowing = true
                add(R.id.currenciesFragment, SelectCurrencyFragment())
            }
            else {
                presenter.currenciesFragmentIsShowing = false
                supportFragmentManager.fragments.firstOrNull()?.let { remove(it) }
            }
        }?.commit()
        setTitleText(show)
    }

    private fun setTitleText(currenciesFragmentIsShowing: Boolean) {
        toolbar.toolbar_title.text = getString(if (currenciesFragmentIsShowing) R.string.LNG_CURRENCIES_CHOOSE else R.string.LNG_MENU_TITLE_SETTINGS)
    }

    override fun initGeneralSettingsLayout() {
        settingsCurrency.setOnClickListener { showCurrenciesFragment(true) }
        settingsBtnLogout.setOnClickListener { presenter.onLogout() }
        settingsBtnSupport.setOnClickListener { presenter.sendEmail(null, null) }
    }

    override fun initCarrierLayout() {
        layoutCarrierSettings.isVisible = true
        with(settingsCoordinatesInBackground) {
            setOnClickListener {
                with(it.switch_button) {
                    isChecked = !isChecked
                    presenter.onDriverCoordinatesSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = presenter.isBackGroundAccepted
                setOnCheckedChangeListener { _, isChecked ->
                    presenter.onDriverCoordinatesSwitched(isChecked)
                }
            }
        }
    }

    override fun initDebugLayout() {
        layoutDebugSettings.isVisible = true
        settingsLogs.setOnClickListener { presenter.onLogsClicked() }
        settingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        settingsResetRate.setOnClickListener { presenter.onResetRateClicked() }
        settingsClearAccessToken.setOnClickListener { presenter.onClearAccessTokenClicked() }
    }

    override fun setDistanceUnit(inMiles: Boolean) {
        with(settingsDistanceUnit) {
            setOnClickListener {
                with(it.switch_button) {
                    isChecked = !isChecked
                    presenter.onDistanceUnitSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = inMiles
                setOnCheckedChangeListener { _, isChecked ->
                    presenter.onDistanceUnitSwitched(isChecked)
                }
            }
        }
    }

    override fun setCalendarModes(calendarModesKeys: List<String>) {
        val calendarModes = calendarModesKeys.map { Pair<CharSequence, String>(getCalendarModeName(it), it) }
        val calendarModesNames = calendarModes.map { it.first }
        Utils.setCalendarModesDialogListener(this, settingsCalendarMode, calendarModesNames, R.string.LNG_CALENDAR_MODE) {
            presenter.changeCalendarMode(calendarModes[it].second)
        }
    }

    /*override fun setCurrencies(currencies: List<CurrencyModel>) =
            Utils.setCurrenciesDialogListener(this, settingsCurrency, currencies) { presenter.changeCurrency(it) }*/

    override fun setLocales(locales: List<LocaleModel>) =
            Utils.setLocalesDialogListener(this, settingsLanguage, locales) {
                localeManager.updateResources(this, presenter.changeLocale(it))
            }

    /*override fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>) =
            Utils.setDistanceUnitsDialogListener(this, layoutSettingsDistanceUnits, distanceUnits) { presenter.changeDistanceUnit(it) }*/

    override fun setDaysOfWeek(daysOfWeek: List<CharSequence>) =
            Utils.setFirstDayOfWeekDialogListener(this, settingsFirstDayOfWeek, daysOfWeek) { presenter.changeFirstDayOfWeek(it) }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
            Utils.setEndpointsDialogListener(this, settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCurrency(currency: String, hideCurrenciesFragment: Boolean) {
        settingsCurrency.field_text.text = currency
        if(hideCurrenciesFragment) showCurrenciesFragment(false)
    }

    override fun setLocale(locale: String, code: String) {
        settingsLanguage.field_text.text = locale
        with(settingsLanguage.field_text) {
            text = locale
            setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this@SettingsActivity, Utils.getLanguageImage(code)), null)
            compoundDrawablePadding = Utils.dpToPxInt(this@SettingsActivity, 8f)
        }
    }
    /*override fun setDistanceUnit(distanceUnit: String) { tvSelectedDistanceUnits.text = distanceUnit }*/
    override fun setFirstDayOfWeek(dayOfWeek: String) { settingsFirstDayOfWeek.field_text.text = dayOfWeek }
    override fun setEndpoint(endpoint: EndpointModel)  { settingsEndpoint.field_text.text = endpoint.name }
    override fun setCalendarMode(calendarModeKey: String) { settingsCalendarMode.field_text.text = getCalendarModeName(calendarModeKey) }

    override fun setLogoutButtonEnabled(enabled: Boolean) {
        layoutSettingsBtnLogout.isVisible = enabled
    }

    override fun restartApp() {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(it)
        }
        finish()
        Runtime.getRuntime().exit(0)
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }

    private fun getCalendarModeName(calendarModeKey: String) =
            when (calendarModeKey) {
                Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR -> getString(R.string.LNG_CALENDAR)
                Screens.CARRIER_TRIPS_TYPE_VIEW_LIST -> getString(R.string.LNG_LIST)
                else -> throw UnsupportedOperationException()
            }
}
