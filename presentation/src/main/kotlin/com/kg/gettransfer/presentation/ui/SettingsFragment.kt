package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import java.util.Locale

import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_settings_field_horizontal_picker.view.field_text
import kotlinx.android.synthetic.main.view_settings_field_switch.view.*
import kotlinx.android.synthetic.main.view_settings_field_vertical_picker.*
import org.koin.core.KoinComponent

import timber.log.Timber
import android.view.MotionEvent
import com.kg.gettransfer.utilities.LocaleManager
import org.koin.android.ext.android.inject

@Suppress("TooManyFunctions")
class SettingsFragment : BaseFragment(), KoinComponent, SettingsView {

    private val localeManager: LocaleManager by inject()

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    private var count = 0
    private var startMillis = 0L

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        //TODO set to click on app version
        view.setOnTouchListener { _, event ->
            dispatchTouchEvent(event)
            return@setOnTouchListener true
        }
        return view
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleText()
    }

    override fun showCurrencyChooser() {
        findNavController().navigate(R.id.go_to_select_currency)
    }

    private fun setTitleText() {
        toolbar.toolbar_title.text = getString(R.string.LNG_MENU_TITLE_SETTINGS)
    }

    override fun initGeneralSettingsLayout() {
        Timber.d("current locale: ${Locale.getDefault()}")
        settingsCurrency.setOnClickListener { presenter.onCurrencyClicked() }
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
            setOnClickListener {
                presenter.onProfileFieldClicked()
            }
        }
    }

    override fun initCarrierLayout() {
        layoutCarrierSettings.isVisible = true
        with(settingsCoordinatesInBackground) {
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onDriverCoordinatesSwitched(isChecked)
                }
            }
            switch_button.apply {
                isChecked = presenter.isBackGroundAccepted
                setOnCheckedChangeListener { _, isChecked -> presenter.onDriverCoordinatesSwitched(isChecked) }
            }
        }
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

    override fun setEmailNotifications(isLoggedIn: Boolean, enabled: Boolean) {
        settingsEmailNotif.isVisible = isLoggedIn
        if (isLoggedIn) {
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
    }

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

    override fun setLocales(locales: List<LocaleModel>) =
        Utils.setLocalesDialogListener(requireContext(), settingsLanguage, locales) { selected ->
            localeManager.updateResources(requireContext(), presenter.changeLocale(selected))
        }

    override fun setDaysOfWeek(daysOfWeek: List<CharSequence>) =
        Utils.setFirstDayOfWeekDialogListener(requireContext(), settingsFirstDayOfWeek, daysOfWeek) { selected ->
            presenter.changeFirstDayOfWeek(selected)
        }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
        Utils.setEndpointsDialogListener(requireContext(), settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

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

    override fun setFirstDayOfWeek(dayOfWeek: String) { settingsFirstDayOfWeek.field_text.text = dayOfWeek }

    override fun setEndpoint(endpoint: EndpointModel)  { settingsEndpoint.field_text.text = endpoint.name }

    override fun setCalendarMode(calendarModeKey: String) {
        settingsCalendarMode.field_text.text = getCalendarModeName(calendarModeKey)
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

    private fun dispatchTouchEvent(event: MotionEvent?) {
        if (event?.action == MotionEvent.ACTION_UP) {
            val time = System.currentTimeMillis()

            // if it is the first time, or if it has been more than 3 seconds since the first tap
            // (so it is like a new try), we reset everything
            if (startMillis == 0L || time - startMillis > TIME_FOR_DEBUG) {
                startMillis = time
                count = 1
            } else {
                // it is not the first, and it has been  less than 3 seconds since the first
                count++
            }

            if (count == COUNTS_FOR_DEBUG) {
                count = 0
                presenter.switchDebugSettings()
            }
        }
    }

    companion object {
        private const val COUNTS_FOR_DEBUG = 7
        private const val TIME_FOR_DEBUG = 3000L
        private const val COMPOUND_DRAWABLE_PADDING = 8f
    }
}
