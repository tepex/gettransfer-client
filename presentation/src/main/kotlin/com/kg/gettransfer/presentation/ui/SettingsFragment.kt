package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater

import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat

import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

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

import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_settings_field_horizontal_picker.view.field_text
import kotlinx.android.synthetic.main.view_settings_field_switch.view.*
import org.koin.core.KoinComponent

import timber.log.Timber
import com.kg.gettransfer.utilities.LocaleManager
import kotlinx.android.synthetic.main.view_settings_field_vertical_picker.*
import org.koin.android.ext.android.inject

@Suppress("TooManyFunctions")
class SettingsFragment : BaseFragment(), KoinComponent, SettingsView, CurrencyChangedListener {

    private val localeManager: LocaleManager by inject()

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View =
            inflater.inflate(R.layout.fragment_settings, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleText()
    }

    override fun showCurrencyChooser() {
        findNavController().navigate(SettingsFragmentDirections.goToSelectCurrency())
    }

    private fun setTitleText() {
        toolbar.toolbar_title.text = getString(R.string.LNG_MENU_TITLE_SETTINGS)
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
        Utils.setLocalesDialogListener(requireContext(), settingsLanguage, locales) { selected ->
            presenter.changeLocale(selected)
        }

    override fun updateResources(locale: Locale) {
        localeManager.updateResources(requireContext(), locale)
    }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
            Utils.setEndpointsDialogListener(requireContext(), settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCalendarModes(calendarModesKeys: List<String>) {
        val calendarModes = calendarModesKeys.map { getCalendarModeName(it) to it }
        val calendarModesNames = calendarModes.map { it.first }
        Utils.setCalendarModesDialogListener(
                requireContext(),
                settingsCalendarMode,
                calendarModesNames,
                R.string.LNG_CALENDAR_MODE
        ) { presenter.changeCalendarMode(calendarModes[it].second) }
    }

    override fun setDaysOfWeek(daysOfWeek: List<CharSequence>) =
            Utils.setFirstDayOfWeekDialogListener(requireContext(), settingsFirstDayOfWeek, daysOfWeek) { selected ->
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
                        requireContext(),
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
            compoundDrawablePadding = Utils.dpToPxInt(requireContext(), COMPOUND_DRAWABLE_PADDING)
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
        requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        requireActivity().finish()
        Runtime.getRuntime().exit(0)
    }

    private fun getCalendarModeName(calendarModeKey: String) = when (calendarModeKey) {
        Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR -> getString(R.string.LNG_CALENDAR)
        Screens.CARRIER_TRIPS_TYPE_VIEW_LIST -> getString(R.string.LNG_LIST)
        else -> throw UnsupportedOperationException()
    }

    companion object {
        private const val COMPOUND_DRAWABLE_PADDING = 8f
    }
}
