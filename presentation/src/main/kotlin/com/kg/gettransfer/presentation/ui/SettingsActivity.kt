package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener

import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import java.util.Locale

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_settings_field_horizontal_picker.view.field_text
import kotlinx.android.synthetic.main.view_settings_field_switch.view.*
import kotlinx.android.synthetic.main.view_settings_field_vertical_picker.*

import timber.log.Timber

@Suppress("TooManyFunctions")
class SettingsActivity : BaseActivity(), SettingsView, CurrencyChangedListener {

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun getPresenter(): SettingsPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        @Suppress("UnsafeCast")
        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_SETTINGS, hasBackAction = false)
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
                    // SettingsPresenter.PASSWORD_VIEW   -> add(R.id.currenciesFragment, ChangePasswordFragment())
                    else -> throw UnsupportedOperationException()
                }
                /*add(R.id.currenciesFragment, when(showingView){
                    SettingsPresenter.CURRENCIES_VIEW -> SelectCurrencyFragment()
                    SettingsPresenter.PASSWORD_VIEW   -> ChangePasswordFragment()
                    else -> throw UnsupportedOperationException()
                })*/
            } else {
                presenter.showingFragment = null
                supportFragmentManager.fragments.firstOrNull()?.let { remove(it) }
            }
        }?.commitAllowingStateLoss()
        setTitleText(showingView)
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) = transaction.apply {
        val anim = if (opens) R.anim.enter_from_right else R.anim.exit_to_right
        setCustomAnimations(anim, anim)
    }

    private fun setTitleText(showingView: Int) {
        toolbar.toolbar_title.text = getString(when (showingView) {
            // SettingsPresenter.PASSWORD_VIEW   -> R.string.LNG_LOGIN_PASSWORD_SECTION
            SettingsPresenter.CURRENCIES_VIEW -> R.string.LNG_CURRENCIES_CHOOSE
            else -> R.string.LNG_MENU_TITLE_SETTINGS
        })
    }

    override fun initGeneralSettingsLayout() {
        Timber.d("current locale: ${Locale.getDefault()}")
        settingsCurrency.setOnClickListener { presenter.onCurrencyClicked() }

        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        versionApp.text = String.format(getString(R.string.app_version), versionName, versionCode)
        shareBtn.setOnClickListener { presenter.onShareClick() }
        layoutAboutApp.setOnClickListener { presenter.onAboutAppClicked() }
    }

    override fun initProfileField(isLoggedIn: Boolean, profile: Profile) {
        with(settingsProfile) {
            if (isLoggedIn) {
                titleText.text = profile.fullName ?: getString(R.string.LNG_PROFILE)
                subtitleText.isVisible = !profile.email.isNullOrEmpty() || !profile.phone.isNullOrEmpty()
                subtitleText.text = profile.email ?: profile.phone ?: ""
            } else {
                titleText.text = getString(R.string.LNG_LOGIN_LOGIN_TITLE)
                subtitleText.isVisible = false
            }
            this.setOnClickListener {
                presenter.onProfileFieldClicked()
            }
        }
    }

    override fun setEmailNotifications(enabled: Boolean) {
        with(settingsEmailNotif) {
            isVisible = true
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onEmailNotificationSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = enabled
                setOnCheckedChangeListener { _, isChecked -> presenter.onEmailNotificationSwitched(isChecked) }
            }
        }
    }

    override fun hideEmailNotifications() {
        settingsEmailNotif.isVisible = false
    }

    override fun initDriverLayout(isBackGroundCoordinatesAccepted: Boolean) {
        layoutCarrierSettings.isVisible = true
        with(settingsCoordinatesInBackground) {
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onDriverCoordinatesSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = isBackGroundCoordinatesAccepted
                setOnCheckedChangeListener { _, isChecked -> presenter.onDriverCoordinatesSwitched(isChecked) }
            }
        }
    }

    override fun hideDriverLayout() {
        layoutCarrierSettings.isVisible = false
    }

    override fun showDebugMenu() {
        layoutDebugSettings.isVisible = true
        settingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        settingsResetRate.setOnClickListener { presenter.onResetRateClicked() }
        settingsClearAccessToken.setOnClickListener { presenter.onClearAccessTokenClicked() }
        forceCrash.setOnClickListener { presenter.onForceCrashClick() }
    }

    override fun hideDebugMenu() {
        layoutDebugSettings.isVisible = false
    }

    override fun setLocales(locales: List<LocaleModel>) =
        Utils.setLocalesDialogListener(this, settingsLanguage, locales) { selected ->
            presenter.changeLocale(selected)
        }

    override fun updateResources(locale: Locale) {
        localeManager.updateResources(this, locale)
    }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
            Utils.setEndpointsDialogListener(this, settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCalendarModes(calendarModesKeys: List<String>) {
        val calendarModes = calendarModesKeys.map { getCalendarModeName(it) to it }
        val calendarModesNames = calendarModes.map { it.first }
        Utils.setCalendarModesDialogListener(
                this,
                settingsCalendarMode,
                calendarModesNames,
                R.string.LNG_CALENDAR_MODE
        ) { presenter.changeCalendarMode(calendarModes[it].second) }
    }

    override fun setDaysOfWeek(daysOfWeek: List<CharSequence>) =
            Utils.setFirstDayOfWeekDialogListener(this, settingsFirstDayOfWeek, daysOfWeek) { selected ->
                presenter.changeFirstDayOfWeek(selected)
            }

    override fun setCurrency(currency: String) { settingsCurrency.field_text.text = currency }

    override fun setLocale(locale: String, code: String) {
        settingsLanguage.field_text.text = locale
        val langIconParams = LanguageDrawer.LanguageLayoutParamsRes.SETTINGS
        with(settingsLanguage.field_text) {
            text = locale
            setCompoundDrawables(
                null,
                null,
                    ContextCompat.getDrawable(
                        this@SettingsActivity,
                        Utils.getLanguageImage(code)
                    )?.apply {
                        setBounds(
                            0,
                            0,
                            resources.getDimensionPixelSize(langIconParams.width),
                            resources.getDimensionPixelSize(langIconParams.height))
                    },
                null
            )
            compoundDrawablePadding = Utils.dpToPxInt(this@SettingsActivity, COMPOUND_DRAWABLE_PADDING)
        }
    }

    override fun setCalendarMode(calendarModeKey: String) {
        settingsCalendarMode.field_text.text = getCalendarModeName(calendarModeKey)
    }

    override fun setFirstDayOfWeek(dayOfWeek: String) { settingsFirstDayOfWeek.field_text.text = dayOfWeek }

    override fun setEndpoint(endpoint: EndpointModel)  { settingsEndpoint.field_text.text = endpoint.name }

    override fun setDistanceUnit(inMiles: Boolean) {
        with(settingsDistanceUnit) {
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onDistanceUnitSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = inMiles
                setOnCheckedChangeListener { _, isChecked -> presenter.onDistanceUnitSwitched(isChecked) }
            }
        }
    }

    override fun currencyChanged(currency: CurrencyModel) {
        presenter.currencyChanged(currency)
    }

    override fun hideSomeDividers() {
        if (!settingsEmailNotif.isVisible) settingsDistanceUnit.hideDivider()
        if (!layoutCarrierSettings.isVisible) settingsEmailNotif.hideDivider()
    }

    override fun restartApp() {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
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

    companion object {
        private const val COMPOUND_DRAWABLE_PADDING = 8f
    }
}
