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
import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import com.kg.gettransfer.presentation.view.CarrierTripsMainView

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.get
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

    internal var currenciesFragmentIsShowing = false
    private var localeWasChanged = false
    private var restart = true
    val isDriverMode get() =
        systemInteractor.lastMode == Screens.CARRIER_MODE
    val isBackGroundAccepted get() =
        carrierTripInteractor.bgCoordinatesPermission != CarrierTripsMainView.BG_COORDINATES_REJECTED

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
        if (isDriverMode) { initCarrierSettings() }
        if (BuildConfig.FLAVOR == "dev") { initDebugSettings() }
    }

    private fun initGeneralSettings(){
        viewState.initGeneralSettingsLayout()

        /*viewState.setCurrencies(currencies)
        val currency = systemInteractor.currency
        val currencyModel = currencies.find { it.delegate == currency }
        viewState.setCurrency(currencyModel?.name ?: "")*/
        viewState.setCurrency(systemInteractor.currency.let { currencyMapper.toView(it) }.name)

        viewState.setLocales(locales)
        val locale = systemInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.language }
        viewState.setLocale(localeModel?.name ?: "", locale.language)

        viewState.setDistanceUnit(systemInteractor.distanceUnit == DistanceUnit.mi)

        viewState.setLogoutButtonEnabled(systemInteractor.account.user.loggedIn)
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
        val currencyModel = systemInteractor.currency.let { currencyMapper.toView(it) }
        viewState.setCurrency(currencyModel.name, true)
        logEvent(Analytics.CURRENCY_PARAM, currencyModel.code)
    }

    fun changeLocale(selected: Int): Locale {
        localeWasChanged = true
        val localeModel = locales.get(selected)
        systemInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name, localeModel.locale)
        if (systemInteractor.account.user.loggedIn) putAccount()
        else saveNoAccount()
        logEvent(Analytics.LANGUAGE_PARAM, localeModel.name)

        Locale.setDefault(systemInteractor.locale)
        initConfigs()
        //viewState.setCurrencies(currencies)
        viewState.setCalendarModes(calendarModes)

        return systemInteractor.locale
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

    private fun putAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { systemInteractor.putAccount() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        if (result.error == null) viewState.restartApp()
        viewState.blockInterface(false)
    }

    private fun saveNoAccount() = utils.launchSuspend {
        val result = utils.asyncAwait { systemInteractor.putNoAccount() }
        if (result.error == null) viewState.restartApp()
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
            utils.asyncAwait { systemInteractor.logout() }
            utils.asyncAwait { systemInteractor.coldStart() }
            viewState.blockInterface(false)
            restart = true
            router.exit() //Without restarting app
        }
    }

    fun onLogout() {
        utils.launchSuspend {
            utils.asyncAwait { systemInteractor.unregisterPushToken() }
            utils.asyncAwait { systemInteractor.logout() }

            utils.asyncAwait { transferInteractor.clearTransfersCache() }
            utils.asyncAwait { offerInteractor.clearOffersCache() }
            utils.asyncAwait { carrierTripInteractor.clearCarrierTripsCache() }
            router.exit()
        }
        logEvent(Analytics.LOG_OUT_PARAM, Analytics.EMPTY_VALUE)
    }

    fun onDistanceUnitSwitched(isChecked: Boolean) {
        systemInteractor.distanceUnit = when (isChecked) {
            true  -> DistanceUnit.mi
            false -> DistanceUnit.km
        }.apply { logEvent(Analytics.UNITS_PARAM, name) }
        saveAccount()
    }

    fun onLogsClicked() = router.navigateTo(Screens.ShareLogs)

    fun onResetOnboardingClicked() { systemInteractor.isOnboardingShowed = false }

    fun onResetRateClicked() { reviewInteractor.shouldAskRateInMarket = true }

    fun onClearAccessTokenClicked() { systemInteractor.accessToken = "" }

    fun onDriverCoordinatesSwitched(checked: Boolean) =
            carrierTripInteractor.permissionChanged(checked)

    @CallSuper
    override fun onBackCommandClick() {
        if (currenciesFragmentIsShowing) {
            viewState.showCurrenciesFragment(false)
            return
        }
        if (localeWasChanged) {
            localeWasChanged = false
            router.navigateTo(Screens.ChangeMode(systemInteractor.lastMode))
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
        locales = systemInteractor.locales.map { localeMapper.toView(it) }
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
}
