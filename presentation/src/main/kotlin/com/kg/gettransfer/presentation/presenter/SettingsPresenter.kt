package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.mapper.CurrencyMapper
import com.kg.gettransfer.presentation.mapper.DayOfWeekMapper
import com.kg.gettransfer.presentation.mapper.EndpointMapper
import com.kg.gettransfer.presentation.mapper.LocaleMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.CarrierTripsMainView

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.get
import java.lang.IllegalArgumentException
import java.util.Locale

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {
    //private lateinit var currencies: List<CurrencyModel>
    private lateinit var locales: List<LocaleModel>
    //private lateinit var distanceUnits: List<DistanceUnitModel>
    private lateinit var endpoints: List<EndpointModel>
    private lateinit var calendarModes: List<String>
    private lateinit var daysOfWeek: List<DayOfWeekModel>
    private lateinit var daysOfWeek1: List<DayOfWeekModel1>

    private val localeMapper       = get<LocaleMapper>()
    private val currencyMapper     = get<CurrencyMapper>()
    //private val distanceUnitMapper = get<DistanceUnitMapper>()
    private val dayOfWeekMapper    = get<DayOfWeekMapper>()
    private val endpointMapper     = get<EndpointMapper>()
    private val reviewInteractor   = get<ReviewInteractor>()
    private val profileMapper      = get<ProfileMapper>()

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
        const val PASSWORD_VIEW   = 2
    }

    /*companion object {
        private const val CURRENCY_GBP = "GBP"
        private const val LOCALE_RU = "ru"
        private const val GBP_RU = "Фунт стерлингов"
    }*/

    @CallSuper
    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()
        initGeneralSettings()
        if (accountManager.isLoggedIn) initLoggedInUserSettings()
        if (isDriverMode) initCarrierSettings()
        if (BuildConfig.FLAVOR == "dev") initDebugSettings()
    }

    private fun initGeneralSettings(){
        viewState.initGeneralSettingsLayout()

        /*viewState.setCurrencies(currencies)
        val currency = systemInteractor.currency
        val currencyModel = currencies.find { it.delegate == currency }
        viewState.setCurrency(currencyModel?.name ?: "")*/
        viewState.setCurrency(sessionInteractor.currency.let { currencyMapper.toView(it) }.name)

        viewState.setLocales(locales)
        val locale = sessionInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.language }
        viewState.setLocale(localeModel?.name ?: "", locale.language)

        viewState.setDistanceUnit(sessionInteractor.distanceUnit == DistanceUnit.mi)
        viewState.setLogoutButtonEnabled(accountManager.hasAccount)
    }

    private fun initLoggedInUserSettings() {
        viewState.initLoggedInUserSettings(accountManager.remoteProfile.let { profileMapper.toView(it) })
        viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
    }

    private fun initCarrierSettings(){
        viewState.initCarrierLayout()

        viewState.setCalendarModes(calendarModes)
        if (systemInteractor.lastCarrierTripsTypeView.isEmpty()) {
            systemInteractor.lastCarrierTripsTypeView = Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR
        }
        viewState.setCalendarMode(systemInteractor.lastCarrierTripsTypeView)

        viewState.setDaysOfWeek(daysOfWeek1)
        viewState.setFirstDayOfWeek(daysOfWeek1[systemInteractor.firstDayOfWeek - 1].name)
    }

    private fun initDebugSettings() {
        viewState.initDebugLayout()

        viewState.setEndpoints(endpoints)
        viewState.setEndpoint(endpointMapper.toView(systemInteractor.endpoint))
    }

    /*fun changeCurrency(selected: Int) {
        val currencyModel = currencies.get(selected)
        systemInteractor.currency = currencyModel.delegate
        viewState.setCurrency(currencyModel.name)
        saveAccount()
        logEvent(Analytics.CURRENCY_PARAM, currencyModel.code)
    }*/

    override fun currencyChanged() {
        val currencyModel = sessionInteractor.currency.let { currencyMapper.toView(it) }
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
        //viewState.setCurrencies(currencies)
        viewState.setCalendarModes(calendarModes)

        return sessionInteractor.locale
    }

    fun changeFirstDayOfWeek(selected: Int) {
        with(daysOfWeek1[selected]) {
            systemInteractor.firstDayOfWeek = delegate.day
            viewState.setFirstDayOfWeek(name)
        }
    }

    fun changeCalendarMode(selected: String) {
        systemInteractor.lastCarrierTripsTypeView = selected
        viewState.setCalendarMode(selected)
    }

    /*fun changeDistanceUnit(selected: Int) {
        val distanceUnit = distanceUnits.get(selected)
        systemInteractor.distanceUnit = distanceUnit.delegate
        viewState.setDistanceUnit(distanceUnit.name)
        saveAccount()
        logEvent(Analytics.UNITS_PARAM, distanceUnit.name)
    }*/

    fun changeEndpoint(selected: Int) {
        val endpoint = endpoints.get(selected)
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
            true  -> DistanceUnit.mi
            false -> DistanceUnit.km
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

    fun onClearAccessTokenClicked() { sessionInteractor.accessToken = "" }

    fun onDriverCoordinatesSwitched(checked: Boolean) =
            carrierTripInteractor.permissionChanged(checked)

    fun onCurrencyClicked() {
        showingFragment = CURRENCIES_VIEW
        viewState.showFragment(CURRENCIES_VIEW)
    }

    fun onPasswordClicked() {
        showingFragment = PASSWORD_VIEW
        viewState.showFragment(PASSWORD_VIEW)
    }

    @CallSuper
    override fun onBackCommandClick() {
        if (showingFragment != null) {
            viewState.showFragment(CLOSE_FRAGMENT)
            return
        }
        if (localeWasChanged) {
            localeWasChanged = false
            val screen = when (systemInteractor.lastMode) {
                Screens.CARRIER_MODE -> Screens.Carrier()
                Screens.PASSENGER_MODE -> Screens.MainPassenger()
                else                   -> throw IllegalArgumentException("Wrong last mode in onBackCommandClick in ${this.javaClass.name}")
            }
            router.backTo(screen)
        } else super.onBackCommandClick()
    }

    private fun initConfigs() {
        endpoints = systemInteractor.endpoints.map { endpointMapper.toView(it) }
        /*currencies = systemInteractor.currencies.map {
            currencyMapper.toView(it)*//*.apply {
                if (it.code == CURRENCY_GBP) {
                    if (systemInteractor.locale.language == LOCALE_RU)
                        this.name = "$GBP_RU ($symbol)"
                }
            }*//*
        }*/
        locales = sessionInteractor.locales.map { localeMapper.toView(it) }
        //distanceUnits = systemInteractor.distanceUnits.map { distanceUnitMapper.toView(it) }
        calendarModes = listOf(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR, Screens.CARRIER_TRIPS_TYPE_VIEW_LIST)
     //   daysOfWeek = GTDayOfWeek.values().toList().map { dayOfWeekMapper.toView(it) }
        daysOfWeek1 = GTDayOfWeek.getWeekDays().map { DayOfWeekModel1(it) }
        restart = false
    }

    private fun logEvent(param: String, value: String) {
        val map = mutableMapOf<String, Any>()
        map[param] = value

        analytics.logEvent(Analytics.EVENT_SETTINGS, createStringBundle(param, value), map)
    }

    override fun restartApp() { viewState.restartApp() }
}
