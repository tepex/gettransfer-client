package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DayOfWeekModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.utilities.Analytics

@Suppress("TooManyFunctions")
@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    private lateinit var locales: List<LocaleModel>
    private lateinit var endpoints: List<EndpointModel>
    private lateinit var calendarModes: List<String>
    private lateinit var daysOfWeek: List<DayOfWeekModel>

    private var localeWasChanged = false
    private var restart = true

    private var count = 0
    private var startMillis = 0L

    val isBackGroundAccepted get() =
        carrierTripInteractor.bgCoordinatesPermission != CarrierTripsMainView.BG_COORDINATES_REJECTED

    internal var showingFragment: Int? = null

    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()
        initGeneralSettings()
        initProfileSettings()
        initDriverSettings()
        if (BuildConfig.FLAVOR == "dev" || systemInteractor.isDebugMenuShowed) initDebugMenu()
        viewState.hideSomeDividers()
    }

    private fun initGeneralSettings() {
        viewState.initGeneralSettingsLayout()

        viewState.setCurrency(sessionInteractor.currency.map().name)

        viewState.setLocales(locales)
        val locale = sessionInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.language }
        viewState.setLocale(localeModel?.name ?: "", locale.language)

        viewState.setDistanceUnit(sessionInteractor.distanceUnit == DistanceUnit.MI)
    }

    private fun initProfileSettings() {
        viewState.initProfileField(accountManager.isLoggedIn, accountManager.remoteProfile)
        if (accountManager.isLoggedIn) {
            viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
        } else {
            viewState.hideEmailNotifications()
        }
    }

    private fun initDriverSettings() {
        if (accountManager.isLoggedIn && systemInteractor.lastMode == Screens.CARRIER_MODE) {
            viewState.initDriverLayout(isBackGroundAccepted)
            viewState.setCalendarModes(calendarModes)
            viewState.setDaysOfWeek(daysOfWeek)
            viewState.setCalendarMode(systemInteractor.lastCarrierTripsTypeView)
            viewState.setFirstDayOfWeek(daysOfWeek[systemInteractor.firstDayOfWeek - 1].name)
        } else {
            viewState.hideDriverLayout()
        }
    }

    private fun initDebugMenu() {
        viewState.setEndpoints(endpoints)
        viewState.setEndpoint(systemInteractor.endpoint.map())
        viewState.showDebugMenu()
    }

    fun currencyChanged(currency: CurrencyModel) {
        saveGeneralSettings()
        viewState.setCurrency(currency.name)
        viewState.showFragment(CLOSE_FRAGMENT)
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.CURRENCY_PARAM, currency.code)
    }

    fun changeLocale(selected: Int) {
        localeWasChanged = true
        val localeModel = locales[selected]
        sessionInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name, localeModel.locale)
        saveGeneralSettings(true)
        viewState.updateResources(sessionInteractor.locale)
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.LANGUAGE_PARAM, localeModel.name)
    }

    fun onDistanceUnitSwitched(isChecked: Boolean) {
        sessionInteractor.distanceUnit = when (isChecked) {
            true  -> DistanceUnit.MI
            false -> DistanceUnit.KM
        }.apply { analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.UNITS_PARAM, name) }
        saveGeneralSettings()
    }

    fun onEmailNotificationSwitched(isChecked: Boolean) {
        sessionInteractor.isEmailNotificationEnabled = isChecked
        saveGeneralSettings()
    }

    fun onDriverCoordinatesSwitched(checked: Boolean) = carrierTripInteractor.permissionChanged(checked)

    fun changeCalendarMode(selected: String) {
        systemInteractor.lastCarrierTripsTypeView = selected
        viewState.setCalendarMode(selected)
    }

    fun changeFirstDayOfWeek(selected: Int) {
        with(daysOfWeek[selected]) {
            systemInteractor.firstDayOfWeek = delegate.day
            viewState.setFirstDayOfWeek(name)
        }
    }

    fun onAboutAppClicked() {
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
            switchDebugSettings()
        }
    }

    fun switchDebugSettings() {
        if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home") {
            if (systemInteractor.isDebugMenuShowed) {
                systemInteractor.isDebugMenuShowed = false
                viewState.hideDebugMenu()
            } else {
                systemInteractor.isDebugMenuShowed = true
                initDebugMenu()
            }
        }
    }

    fun changeEndpoint(selected: Int) {
        val endpoint = endpoints[selected]
        viewState.setEndpoint(endpoint)
        systemInteractor.endpoint = endpoint.delegate
        utils.launchSuspend {
            viewState.blockInterface(true)
            utils.asyncAwait { accountManager.logout() }
            utils.asyncAwait { sessionInteractor.coldStart() }
            viewState.blockInterface(false)
            restart = true
            router.exit() // Without restarting app
        }
    }

    fun onResetOnboardingClicked() { systemInteractor.isOnboardingShowed = false }

    fun onResetRateClicked() { reviewInteractor.shouldAskRateInMarket = true }

    fun onClearAccessTokenClicked() { systemInteractor.accessToken = "" }

    fun onCurrencyClicked() {
        if (!accountManager.remoteAccount.isBusinessAccount) {
            showingFragment = CURRENCIES_VIEW
            viewState.showFragment(CURRENCIES_VIEW)
        }
    }

    fun onProfileFieldClicked() {
        if (accountManager.isLoggedIn) router.navigateTo(Screens.ProfileSettings())
        else router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, null))
    }

    override fun onBackCommandClick() {
        if (showingFragment != null) {
            viewState.showFragment(CLOSE_FRAGMENT)
            return
        }
        if (localeWasChanged) {
            localeWasChanged = false
            val screen = when (systemInteractor.lastMode) {
                Screens.CARRIER_MODE   -> Screens.CarrierMode
                Screens.PASSENGER_MODE -> Screens.MainPassenger()
                else                   ->
                    throw IllegalArgumentException("Wrong last mode in onBackCommandClick in ${this.javaClass.name}")
            }
            router.backTo(screen)
        } else super.onBackCommandClick()
    }

    fun onShareClick() {
        log.debug("Share action")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun initConfigs() {
        endpoints = systemInteractor.endpoints.map { it.map() }
        locales = systemInteractor.locales.map { it.map() }
        calendarModes = listOf(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR, Screens.CARRIER_TRIPS_TYPE_VIEW_LIST)
        daysOfWeek = GTDayOfWeek.getWeekDays().map { DayOfWeekModel(it) }
        restart = false
    }

    override fun restartApp() { viewState.restartApp() }

    fun onForceCrashClick() {
        error("This is force crash")
    }

    companion object {
        const val CLOSE_FRAGMENT  = 0
        const val CURRENCIES_VIEW = 1

        private const val COUNTS_FOR_DEBUG = 7
        private const val TIME_FOR_DEBUG = 3000L
    }
}
