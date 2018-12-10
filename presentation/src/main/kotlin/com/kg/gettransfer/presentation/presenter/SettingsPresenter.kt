package com.kg.gettransfer.presentation.presenter

import android.content.Intent
import android.net.Uri
import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.mapper.CurrencyMapper
import com.kg.gettransfer.presentation.mapper.DistanceUnitMapper
import com.kg.gettransfer.presentation.mapper.EndpointMapper
import com.kg.gettransfer.presentation.mapper.LocaleMapper

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.utilities.Analytics

import com.yandex.metrica.YandexMetrica

import java.util.Locale

import org.koin.standalone.get
import org.koin.standalone.inject

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {
    private lateinit var currencies: List<CurrencyModel>
    private lateinit var locales: List<LocaleModel>
    private lateinit var distanceUnits: List<DistanceUnitModel>
    private val endpoints = systemInteractor.endpoints.map { endpointMapper.toView(it) }

    private val localeMapper: LocaleMapper by inject()
    private val currencyMapper = get<CurrencyMapper>()
    private val distanceUnitMapper = get<DistanceUnitMapper>()
    private val endpointMapper = get<EndpointMapper>()

    private var localeWasChanged = false
    private var restart = true

    @CallSuper
    override fun attachView(view: SettingsView) {
        super.attachView(view)
        if (restart) initConfigs()

        viewState.setCurrencies(currencies)
        viewState.setLocales(locales)
        viewState.setDistanceUnits(distanceUnits)

        viewState.setEndpoints(endpoints)

        val locale = systemInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.getLanguage() }
        viewState.setLocale(localeModel?.name ?: "")

        val currency = systemInteractor.currency
        val currencyModel = currencies.find { it.delegate == currency }
        viewState.setCurrency(currencyModel?.name ?: "")
        viewState.setDistanceUnit(systemInteractor.distanceUnit.name)

        viewState.setEndpoint(endpointMapper.toView(systemInteractor.endpoint))
        viewState.setLogoutButtonEnabled(systemInteractor.account.user.loggedIn)
    }

    fun changeCurrency(selected: Int) {
        val currencyModel = currencies.get(selected)
        systemInteractor.currency = currencyModel.delegate
        viewState.setCurrency(currencyModel.name)
        saveAccount()
        logEvent(Analytics.CURRENCY_PARAM, currencyModel.code)
    }

    fun changeLocale(selected: Int): Locale {
        localeWasChanged = true
        val localeModel = locales.get(selected)
        systemInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name)
        if (systemInteractor.account.user.loggedIn) saveAccount()
        logEvent(Analytics.LANGUAGE_PARAM, localeModel.name)

        Locale.setDefault(systemInteractor.locale)
        initConfigs()
        viewState.setCurrencies(currencies)

        return systemInteractor.locale
    }

    fun changeDistanceUnit(selected: Int) {
        val distanceUnit = distanceUnits.get(selected)
        systemInteractor.distanceUnit = distanceUnit.delegate
        viewState.setDistanceUnit(distanceUnit.name)
        saveAccount()
        logEvent(Analytics.UNITS_PARAM, distanceUnit.name)
    }

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
            //viewState.restartApp() //For restart app
        }
    }

    fun onLogout() {
        utils.runAlien { systemInteractor.logout() }
        router.exit()
        logEvent(Analytics.LOG_OUT_PARAM, Analytics.EMPTY_VALUE)
    }

    fun onLogsClicked() = router.navigateTo(Screens.ShareLogs)

    fun onResetOnboardingClicked() { systemInteractor.isOnboardingShowed = false }

    private fun saveAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { systemInteractor.putAccount() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
    }

    override fun onBackCommandClick() {
//        localeWasChanged = false
        if(localeWasChanged) {
            localeWasChanged = false
            router.navigateTo(Screens.Main)
        }
        else super.onBackCommandClick()
    }

    private fun initConfigs() {
        currencies = systemInteractor.currencies.map { currencyMapper.toView(it) }
        locales = systemInteractor.locales.map { localeMapper.toView(it) }
        distanceUnits = systemInteractor.distanceUnits.map { distanceUnitMapper.toView(it) }
        restart = false
    }

    private fun logEvent(param: String, value: String) {
        val map = mutableMapOf<String, Any>()
        map[param] = value

        analytics.logEvent(Analytics.EVENT_SETTINGS, createStringBundle(param, value), map)
    }
}
