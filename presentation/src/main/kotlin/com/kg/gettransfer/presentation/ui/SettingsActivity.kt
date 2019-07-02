package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import android.view.MotionEvent

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.ui.custom.SettingsFieldPicker
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_settings_field_picker.view.*
import kotlinx.android.synthetic.main.view_settings_field_switch.view.*

class SettingsActivity : BaseActivity(), SettingsView {

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    private var count = 0
    private var startMillis = 0L

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
    override fun showFragment(showingView: Int) {
        with(supportFragmentManager.beginTransaction()) {
            val show = showingView != SettingsPresenter.CLOSE_FRAGMENT
            setAnimation(show, this)
            if (show) {
                presenter.showingFragment = showingView
                when (showingView) {
                    SettingsPresenter.CURRENCIES_VIEW -> add(R.id.currenciesFragment, SelectCurrencyFragment())
                    //SettingsPresenter.PASSWORD_VIEW   -> add(R.id.currenciesFragment, ChangePasswordFragment())
                    else -> throw UnsupportedOperationException()
                }
                /*add(R.id.currenciesFragment, when(showingView){
                    SettingsPresenter.CURRENCIES_VIEW -> SelectCurrencyFragment()
                    SettingsPresenter.PASSWORD_VIEW   -> ChangePasswordFragment()
                    else -> throw UnsupportedOperationException()
                })*/
            }
            else {
                presenter.showingFragment = null
                supportFragmentManager.fragments.firstOrNull()?.let { remove(it) }
            }
        }?.commit()
        setTitleText(showingView)
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) =
            transaction.apply {
                val anim = if(opens) R.anim.enter_from_right else R.anim.exit_to_right
                setCustomAnimations(anim, anim)
            }

    private fun setTitleText(showingView: Int) {
        toolbar.toolbar_title.text = getString(when (showingView) {
            //SettingsPresenter.PASSWORD_VIEW   -> R.string.LNG_LOGIN_PASSWORD_SECTION
            SettingsPresenter.CURRENCIES_VIEW -> R.string.LNG_CURRENCIES_CHOOSE
            else -> R.string.LNG_MENU_TITLE_SETTINGS
        })

    }

    override fun initGeneralSettingsLayout() {
        settingsCurrency.setOnClickListener { presenter.onCurrencyClicked() }
        settingsBtnLogout.setOnClickListener { presenter.onLogout() }
        settingsBtnSupport.setOnClickListener { presenter.sendEmail(null, null) }
    }

    /*override fun initLoggedInUserSettings(profile: ProfileModel) {
        profile.phone?.let { initInfoField(it, settingsUserPhone) }
        profile.email?.let { initInfoField(it, settingsUserEmail) }

        settingsUserPassword.isVisible = true
        settingsUserPassword.setOnClickListener { presenter.onPasswordClicked() }
    }*/

    override fun initProfileField() {
        settingsProfile.isVisible = true
        settingsProfile.setOnClickListener { presenter.onProfileFieldClicked() }
    }

    private fun initInfoField(text: String, field: SettingsFieldPicker){
        if (text.isNotEmpty()) {
            field.apply {
                field_text.text = text
                field_chevron.isVisible = false
                isVisible = true
            }
        }
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

    override fun showDebugMenu() {
        layoutDebugSettings.isVisible = true
        settingsLogs.setOnClickListener { presenter.onLogsClicked() }
        settingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        settingsResetRate.setOnClickListener { presenter.onResetRateClicked() }
        settingsClearAccessToken.setOnClickListener { presenter.onClearAccessTokenClicked() }
    }

    override fun hideDebugMenu() {
        layoutDebugSettings.isVisible = false
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

    override fun setEmailNotifications(enabled: Boolean) {
        settingsEmailNotif.isVisible = true
        with(settingsEmailNotif) {
            isVisible = true
            setOnClickListener {
                with(it.switch_button) {
                    isChecked = !isChecked
                    presenter.onEmailNotificationSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = enabled
                setOnCheckedChangeListener { _, isChecked ->
                    presenter.onEmailNotificationSwitched(isChecked)
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

    override fun setLocales(locales: List<LocaleModel>) =
            Utils.setLocalesDialogListener(this, settingsLanguage, locales) {
                localeManager.updateResources(this, presenter.changeLocale(it))
            }

    override fun setDaysOfWeek(daysOfWeek: List<CharSequence>) =
            Utils.setFirstDayOfWeekDialogListener(this, settingsFirstDayOfWeek, daysOfWeek) { presenter.changeFirstDayOfWeek(it) }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
            Utils.setEndpointsDialogListener(this, settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCurrency(currency: String) { settingsCurrency.field_text.text = currency }

    override fun setLocale(locale: String, code: String) {
        settingsLanguage.field_text.text = locale
        with(settingsLanguage.field_text) {
            text = locale
            setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this@SettingsActivity, Utils.getLanguageImage(code)), null)
            compoundDrawablePadding = Utils.dpToPxInt(this@SettingsActivity, 8f)
        }
    }

    override fun setFirstDayOfWeek(dayOfWeek: String) { settingsFirstDayOfWeek.field_text.text = dayOfWeek }

    override fun setEndpoint(endpoint: EndpointModel)  { settingsEndpoint.field_text.text = endpoint.name }

    override fun setCalendarMode(calendarModeKey: String) {
        settingsCalendarMode.field_text.text = getCalendarModeName(calendarModeKey)
    }

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

    private fun getCalendarModeName(calendarModeKey: String) = when (calendarModeKey) {
        Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR -> getString(R.string.LNG_CALENDAR)
        Screens.CARRIER_TRIPS_TYPE_VIEW_LIST -> getString(R.string.LNG_LIST)
        else -> throw UnsupportedOperationException()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val time = System.currentTimeMillis()

            //if it is the first time, or if it has been more than 3 seconds since the first tap
            // (so it is like a new try), we reset everything
            if (startMillis == 0L || (time - startMillis > TIME_FOR_DEBUG)) {
                startMillis = time
                count = 1
            }
            //it is not the first, and it has been  less than 3 seconds since the first
            else count++

            if (count == COUNTS_FOR_DEBUG) {
                count = 0
                presenter.switchDebugSettings()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val COUNTS_FOR_DEBUG = 7
        private const val TIME_FOR_DEBUG = 3000L
    }
}
