package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.di.ENDPOINTS

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DayOfWeekModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.CarrierTripsMainView
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

import java.util.Locale

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Suppress("TooManyFunctions")
@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>(), CurrencyChangedListener {

    private val endpoints: List<EndpointModel> = get<List<Endpoint>>(named(ENDPOINTS)).map { it.map() }

    private lateinit var locales: List<LocaleModel>
    private lateinit var calendarModes: List<String>
    private lateinit var daysOfWeek: List<DayOfWeekModel>

    private var localeWasChanged = false
    private var restart = true

    private val worker: WorkerManager by inject { parametersOf("SettingsPresenter") }
    private val configsManager: ConfigsManager by inject()

    private val setDebugMenuShowed: SetDebugMenuShowedInteractor by inject()
    private val setFirstDayOfWeek: SetFirstDayOfWeekInteractor by inject()
    private val setLastCarrierTripsTypeView: SetLastCarrierTripsTypeViewInteractor by inject()
    private val setEndpoint: SetEndpointInteractor by inject()
    private val setOnboardingShowed: SetOnboardingShowedInteractor by inject()
    private val setAccessToken: SetAccessTokenInteractor by inject()
    private val setBackgroundCoordinates: SetBackgroundCoordinatesInteractor by inject()

    internal var showingFragment: Int? = null

    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()
        initGeneralSettings()
        if (accountManager.isLoggedIn) {
            viewState.initProfileField()
            viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
        }

        initDebugMenu()
    }

    private fun initDebugMenu() = worker.main.launch {
        if (BuildConfig.FLAVOR == "dev" || getPreferences().getModel().isDebugMenuShowed) {
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

    fun switchDebugSettings() {
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

    private fun showDebugMenu() = worker.main.launch {
        viewState.setEndpoints(endpoints)
        viewState.setEndpoint(getPreferences().getModel().endpoint!!.map())
        viewState.showDebugMenu()
    }

    override fun currencyChanged(currency: CurrencyModel) {
        viewState.setCurrency(currency.name)
        viewState.showFragment(CLOSE_FRAGMENT)
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.CURRENCY_PARAM, currency.code)
    }

    fun changeLocale(selected: Int): Locale {
        localeWasChanged = true
        val localeModel = locales.get(selected)
        sessionInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name, localeModel.locale)
        saveGeneralSettings()
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.LANGUAGE_PARAM, localeModel.name)

        Locale.setDefault(sessionInteractor.locale)
        initConfigs()
        viewState.setCalendarModes(calendarModes)

        return sessionInteractor.locale
    }

    private fun saveGeneralSettings() = worker.main.launch {
        viewState.blockInterface(true)
        val result = withContext(worker.bg) { accountManager.saveSettings() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        if (result.error == null) {
            viewState.restartApp()
        }
        viewState.blockInterface(false)
    }

    fun changeFirstDayOfWeek(selected: Int) = worker.main.launch {
        with(daysOfWeek[selected]) {
            withContext(worker.bg) { setFirstDayOfWeek(delegate.day) }
            viewState.setFirstDayOfWeek(name)
        }
    }

    fun changeCalendarMode(selected: String) = worker.main.launch {
        withContext(worker.bg) { setLastCarrierTripsTypeView(selected) }
        viewState.setCalendarMode(selected)
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
        sessionInteractor.coldStart()
        viewState.blockInterface(false)
        restart = true
        router.exit() // Without restarting app
    }

    fun onLogout() {
        utils.launchSuspend {
            clearAllCachedData()
            router.exit()
        }
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.LOG_OUT_PARAM, Analytics.EMPTY_VALUE)
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
        saveAccount()
    }

    private fun saveAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { accountManager.putAccount(isTempAccount = false) }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
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

    fun onDriverCoordinatesSwitched(checked: Boolean) = worker.main.launch {
        withContext(worker.bg) { setBackgroundCoordinates(checked) }
    }

    fun onCurrencyClicked() {
        if (!accountManager.remoteAccount.isBusinessAccount) {
            showingFragment = CURRENCIES_VIEW
            viewState.showFragment(CURRENCIES_VIEW)
        }
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

            worker.main.launch {
                val screen = when (getPreferences().getModel().lastMode) {
                    Screens.CARRIER_MODE   -> Screens.CarrierMode
                    Screens.PASSENGER_MODE -> Screens.MainPassenger()
                    else                   ->
                        throw IllegalArgumentException("Wrong last mode in onBackCommandClick in ${this.javaClass.name}")
                }
                router.backTo(screen)
            }
        } else {
            super.onBackCommandClick()
        }
    }

    private fun initConfigs() {
        locales = configsManager.configs.availableLocales.filter { Configs.LOCALES_FILTER.contains(it.language) }
            .map { it.map() }
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
        const val CLOSE_FRAGMENT  = 0
        const val CURRENCIES_VIEW = 1
    }
}
