package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.CarrierTripsMainView

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.utilities.Analytics

import java.util.Locale

import org.koin.core.get

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    private lateinit var locales: List<LocaleModel>
    private lateinit var endpoints: List<EndpointModel>
    private lateinit var calendarModes: List<String>
    private lateinit var daysOfWeek: List<DayOfWeekModel>

    private val reviewInteractor = get<ReviewInteractor>()

    private var localeWasChanged = false
    private var restart = true
    val isDriverMode get() =
        systemInteractor.lastMode == Screens.CARRIER_MODE
    val isBackGroundAccepted get() =
        carrierTripInteractor.bgCoordinatesPermission != CarrierTripsMainView.BG_COORDINATES_REJECTED

    internal var showingFragment: Int? = null

    companion object {
        const val CLOSE_FRAGMENT  = 0
        const val CURRENCIES_VIEW = 1
    }

    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()
        initGeneralSettings()
        if (accountManager.isLoggedIn) {
            viewState.initProfileField()
            viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
        }
        if (isDriverMode) initCarrierSettings()

        initDebugMenu()
    }

    private fun initDebugMenu() {
        if (BuildConfig.FLAVOR == "dev") {
            showDebugMenu()
        } else if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home") {
            if (systemInteractor.isDebugMenuShowed)
                showDebugMenu()
        }
    }

    private fun initGeneralSettings() {
        viewState.initGeneralSettingsLayout()

        viewState.setCurrency(sessionInteractor.currency.map().name)

        viewState.setLocales(locales)
        val locale = sessionInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.language }
        viewState.setLocale(localeModel?.name ?: "", locale.language)

        viewState.setDistanceUnit(sessionInteractor.distanceUnit == DistanceUnit.MI)
        viewState.setLogoutButtonEnabled(accountManager.hasAccount)
    }

    private fun initCarrierSettings() {
        viewState.initCarrierLayout()

        viewState.setCalendarModes(calendarModes)
        if (systemInteractor.lastCarrierTripsTypeView.isEmpty()) {
            systemInteractor.lastCarrierTripsTypeView = Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR
        }
        viewState.setCalendarMode(systemInteractor.lastCarrierTripsTypeView)

        viewState.setDaysOfWeek(daysOfWeek)
        viewState.setFirstDayOfWeek(daysOfWeek[systemInteractor.firstDayOfWeek - 1].name)
    }

    fun switchDebugSettings() {
        if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home") {
            if (systemInteractor.isDebugMenuShowed) {
                systemInteractor.isDebugMenuShowed = false
                viewState.hideDebugMenu()
            } else {
                systemInteractor.isDebugMenuShowed = true
                showDebugMenu()
            }
        }
    }

    private fun showDebugMenu() {
        viewState.setEndpoints(endpoints)
        viewState.setEndpoint(systemInteractor.endpoint.map())
        viewState.showDebugMenu()
    }

    override fun currencyChanged() {
        val currencyModel = sessionInteractor.currency.map()
        viewState.setCurrency(currencyModel.name)
        viewState.showFragment(CLOSE_FRAGMENT)
        logEvent(Analytics.CURRENCY_PARAM, currencyModel.code)
    }

    fun passwordChanged() {
        viewState.showFragment(CLOSE_FRAGMENT)
    }

    fun changeLocale(selected: Int): Locale {
        localeWasChanged = true
        val localeModel = locales.get(selected)
        sessionInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name, localeModel.locale)
        saveGeneralSettings(true)
        logEvent(Analytics.LANGUAGE_PARAM, localeModel.name)

        Locale.setDefault(sessionInteractor.locale)
        initConfigs()
        viewState.setCalendarModes(calendarModes)

        return sessionInteractor.locale
    }

    fun changeFirstDayOfWeek(selected: Int) {
        with(daysOfWeek[selected]) {
            systemInteractor.firstDayOfWeek = delegate.day
            viewState.setFirstDayOfWeek(name)
        }
    }

    fun changeCalendarMode(selected: String) {
        systemInteractor.lastCarrierTripsTypeView = selected
        viewState.setCalendarMode(selected)
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
            router.exit() //Without restarting app
        }
    }

    fun onLogout() {
        utils.launchSuspend {
            clearAllCachedData()
            router.exit()
        }
        logEvent(Analytics.LOG_OUT_PARAM, Analytics.EMPTY_VALUE)
    }

    fun onDistanceUnitSwitched(isChecked: Boolean) {
        sessionInteractor.distanceUnit = when (isChecked) {
            true  -> DistanceUnit.MI
            false -> DistanceUnit.KM
        }.apply { logEvent(Analytics.UNITS_PARAM, name) }
        saveGeneralSettings()
    }

    fun onEmailNotificationSwitched(isChecked: Boolean) {
        sessionInteractor.isEmailNotificationEnabled = isChecked
        saveAccount()
    }

    fun onLogsClicked() = router.navigateTo(Screens.ShareLogs)

    fun onResetOnboardingClicked() { systemInteractor.isOnboardingShowed = false }

    fun onResetRateClicked() { reviewInteractor.shouldAskRateInMarket = true }

    fun onClearAccessTokenClicked() { systemInteractor.accessToken = "" }

    fun onDriverCoordinatesSwitched(checked: Boolean) =
            carrierTripInteractor.permissionChanged(checked)

    fun onCurrencyClicked() {
        showingFragment = CURRENCIES_VIEW
        viewState.showFragment(CURRENCIES_VIEW)
    }

    fun onPasswordClicked() {
        router.navigateTo(Screens.ChangePassword())
    }

    fun onProfileFieldClicked() {
        router.navigateTo(Screens.ProfileSettings())
    }

    override fun onBackCommandClick() {
        if (showingFragment != null) {
            viewState.showFragment(CLOSE_FRAGMENT)
            return
        }
        if (localeWasChanged) {
            localeWasChanged = false
            val screen = when (systemInteractor.lastMode) {
                Screens.CARRIER_MODE   -> Screens.Carrier()
                Screens.PASSENGER_MODE -> Screens.MainPassenger()
                else                   -> throw IllegalArgumentException("Wrong last mode in onBackCommandClick in ${this.javaClass.name}")
            }
            router.backTo(screen)
        } else super.onBackCommandClick()
    }

    private fun initConfigs() {
        endpoints = systemInteractor.endpoints.map { it.map() }
        locales = systemInteractor.locales.map { it.map() }
        calendarModes = listOf(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR, Screens.CARRIER_TRIPS_TYPE_VIEW_LIST)
        daysOfWeek = GTDayOfWeek.getWeekDays().map { DayOfWeekModel(it) }
        restart = false
    }

    private fun logEvent(param: String, value: String) {
        val map = mutableMapOf<String, Any>()
        map[param] = value

        analytics.logEvent(Analytics.EVENT_SETTINGS, createStringBundle(param, value), map)
    }

    override fun restartApp() { viewState.restartApp() }
}
