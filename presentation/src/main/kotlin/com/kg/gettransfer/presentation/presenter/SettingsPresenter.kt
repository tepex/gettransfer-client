package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.di.ENDPOINTS

import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DayOfWeekModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.sys.domain.Endpoint
import com.kg.gettransfer.sys.domain.SetAccessTokenInteractor
import com.kg.gettransfer.sys.domain.SetBackgroundCoordinatesInteractor
import com.kg.gettransfer.sys.domain.SetDebugMenuShowedInteractor
import com.kg.gettransfer.sys.domain.SetEndpointInteractor
import com.kg.gettransfer.sys.domain.SetFirstDayOfWeekInteractor
import com.kg.gettransfer.sys.domain.SetLastCarrierTripsTypeViewInteractor
import com.kg.gettransfer.sys.domain.SetOnboardingShowedInteractor

import com.kg.gettransfer.sys.presentation.ConfigsManager
import com.kg.gettransfer.sys.presentation.EndpointModel
import com.kg.gettransfer.sys.presentation.map

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Suppress("TooManyFunctions")
@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    private val endpoints: List<EndpointModel> = get<List<Endpoint>>(named(ENDPOINTS)).map { it.map() }

    private lateinit var calendarModes: List<String>
    private lateinit var daysOfWeek: List<DayOfWeekModel>

    private var localeWasChanged = false
    private var restart = true

    private var count = 0
    private var startMillis = 0L

    private val worker: WorkerManager by inject { parametersOf("SettingsPresenter") }
    private val configsManager: ConfigsManager by inject()

    private val setDebugMenuShowed: SetDebugMenuShowedInteractor by inject()
    private val setFirstDayOfWeek: SetFirstDayOfWeekInteractor by inject()
    private val setLastCarrierTripsTypeView: SetLastCarrierTripsTypeViewInteractor by inject()
    private val setEndpoint: SetEndpointInteractor by inject()
    private val setOnboardingShowed: SetOnboardingShowedInteractor by inject()
    private val setAccessToken: SetAccessTokenInteractor by inject()
    private val setBackgroundCoordinates: SetBackgroundCoordinatesInteractor by inject()

    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()
        initGeneralSettings()
        initProfileSettings()
        initDriverSettings()
        initDebugMenu()
        viewState.hideSomeDividers()
    }

    private fun initGeneralSettings() {
        viewState.initGeneralSettingsLayout()

        worker.main.launch {
            viewState.setCurrency(sessionInteractor.currency.map().name)
            val locale = sessionInteractor.locale
            val localeModel   = withContext(worker.bg) {
                configsManager.configs.availableLocales.filter { Configs.LOCALES_FILTER.contains(it.language) }
                        .map { it.map() }
                        .find { it.delegate.language == locale.language }
            }
            viewState.setLocale(localeModel?.name ?: "", locale.language)
            viewState.setDistanceUnit(sessionInteractor.distanceUnit == DistanceUnit.MI)
        }
    }

    private fun initProfileSettings() {
        viewState.initProfileField(accountManager.isLoggedIn, accountManager.remoteProfile)
        if (accountManager.isLoggedIn) {
            viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
        } else {
            viewState.hideEmailNotifications()
        }
    }

    private fun initDriverSettings() = worker.main.launch {
        if (accountManager.isLoggedIn && getPreferences().getModel().lastMode == Screens.CARRIER_MODE) {
            viewState.initDriverLayout(getPreferences().getModel().isBackgroundCoordinatesAccepted())
            viewState.setCalendarModes(calendarModes)
            viewState.setDaysOfWeek(daysOfWeek)
            viewState.setCalendarMode(getPreferences().getModel().lastCarrierTripsTypeView)
            viewState.setFirstDayOfWeek(daysOfWeek[getPreferences().getModel().firstDayOfWeek - 1].name)
        } else {
            viewState.hideDriverLayout()
        }
    }

    private fun initDebugMenu() = worker.main.launch {
        if (BuildConfig.FLAVOR == "dev" || getPreferences().getModel().isDebugMenuShowed) {
            showDebugMenu()
        }
    }

    private fun showDebugMenu() = worker.main.launch {
        viewState.setEndpoints(endpoints)
        viewState.setEndpoint(getPreferences().getModel().endpoint!!.map())
        viewState.showDebugMenu()
    }

    fun currencyChanged(currency: CurrencyModel) {
        saveGeneralSettings()
        viewState.setCurrency(currency.name)
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.CURRENCY_PARAM, currency.code)
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

    fun onDriverCoordinatesSwitched(checked: Boolean) = worker.main.launch {
        withContext(worker.bg) { setBackgroundCoordinates(checked) }
    }

    fun changeCalendarMode(selected: String) = worker.main.launch {
        withContext(worker.bg) { setLastCarrierTripsTypeView(selected) }
        viewState.setCalendarMode(selected)
    }

    fun changeFirstDayOfWeek(selected: Int) = worker.main.launch {
        with(daysOfWeek[selected]) {
            withContext(worker.bg) { setFirstDayOfWeek(delegate.day) }
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

    private fun switchDebugSettings() {
        if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home") {
            worker.main.launch {
                if (getPreferences().getModel().isDebugMenuShowed) {
                    withContext(worker.bg) { setDebugMenuShowed(false) }
                    viewState.hideDebugMenu()
                } else {
                    withContext(worker.bg) { setDebugMenuShowed(true) }
                    showDebugMenu()
                }
            }
        }
    }

    fun changeEndpoint(selected: Int) = worker.main.launch {
        val endpoint = endpoints[selected]
        viewState.setEndpoint(endpoint)
        viewState.blockInterface(true)
        withContext(worker.bg) {
            setEndpoint(endpoint.delegate)
            accountManager.logout()
        }
        configsManager.coldStart(worker.backgroundScope)
        fetchResult { sessionInteractor.coldStart() }
        viewState.blockInterface(false)
        restart = true
        router.exit() // Without restarting app
    }

    fun onResetOnboardingClicked() {
        worker.main.launch {
            withContext(worker.bg) { setOnboardingShowed(false) }
        }
    }

    fun onResetRateClicked() { reviewInteractor.shouldAskRateInMarket = true }

    fun onClearAccessTokenClicked() = worker.main.launch {
        withContext(worker.bg) { setAccessToken("") }
    }

    fun onCurrencyClicked() {
        if (!accountManager.remoteAccount.isBusinessAccount) {
            viewState.showCurrencyChooser()
        }
    }

    fun onLanguageClicked() {
        viewState.showLanguageChooser()
    }

    fun onProfileFieldClicked() {
        if (accountManager.isLoggedIn) router.navigateTo(Screens.ProfileSettings())
        else router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, null))
    }

    fun onShareClick() {
        log.debug("Share action")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun initConfigs() {
        calendarModes = listOf(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR, Screens.CARRIER_TRIPS_TYPE_VIEW_LIST)
        daysOfWeek = GTDayOfWeek.getWeekDays().map { DayOfWeekModel(it) }
        restart = false
    }

    fun onForceCrashClick() {
        error("This is force crash")
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        private const val COUNTS_FOR_DEBUG = 7
        private const val TIME_FOR_DEBUG = 3000L
    }
}
