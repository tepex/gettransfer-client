package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.Mappers

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.yandex.metrica.YandexMetrica

import java.util.Locale

import timber.log.Timber

@InjectViewState
class SettingsPresenter: BasePresenter<SettingsView>() {
    private lateinit var currencies: List<CurrencyModel>
    private val locales = Mappers.getLocalesModels(systemInteractor.locales).filter { it.locale == "EN" || it.locale == "RU" } 
    private val distanceUnits = Mappers.getDistanceUnitsModels(systemInteractor.distanceUnits)
    private val endpoints = systemInteractor.endpoints.map { Mappers.getEndpointModel(it) }

    private var localeWasChanged = false

    companion object {
        @JvmField val EVENT = "settings"
        
        @JvmField val CURRENCY_PARAM = "currency"
        @JvmField val UNITS_PARAM    = "units"
        @JvmField val LANGUAGE_PARAM = "language"
        @JvmField val LOG_OUT_PARAM  = "logout"
        
        @JvmField val EMPTY_VALUE = ""
    }

    @CallSuper
    override fun attachView(view: SettingsView) {
        super.attachView(view)
        currencies = Mappers.getCurrenciesModels(systemInteractor.currencies)

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

        viewState.setEndpoint(Mappers.getEndpointModel(systemInteractor.endpoint))
        viewState.setLogoutButtonEnabled(systemInteractor.account.user.loggedIn)
    }

    fun changeCurrency(selected: Int) {
        val currencyModel = currencies.get(selected)
        systemInteractor.currency = currencyModel.delegate
        viewState.setCurrency(currencyModel.name)
        saveAccount()
        logEvent(CURRENCY_PARAM, currencyModel.code)
    }

    fun changeLocale(selected: Int): Locale {
        localeWasChanged = true
        val localeModel = locales.get(selected)
        systemInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name)
        if(systemInteractor.account.user.loggedIn) saveAccount()
        logEvent(LANGUAGE_PARAM, localeModel.name)
        return systemInteractor.locale
    }

    fun changeDistanceUnit(selected: Int) {
        val distanceUnit = distanceUnits.get(selected)
        systemInteractor.distanceUnit = distanceUnit.delegate
        viewState.setDistanceUnit(distanceUnit.name)
        saveAccount()
        logEvent(UNITS_PARAM, distanceUnit.name)
    }

    fun changeEndpoint(selected: Int) {
        utils.runAlien { systemInteractor.logout() }
        val endpoint = endpoints.get(selected)
        systemInteractor.endpoint = endpoint.delegate
        viewState.setEndpoint(endpoint)
        router.exit() //Without restarting app
        //viewState.restartApp() //For restart app
    }

    fun onLogout() {
        utils.runAlien { systemInteractor.logout() }
        router.exit()
        logEvent(LOG_OUT_PARAM, EMPTY_VALUE)
    }

    fun onLogsClicked() = router.navigateTo(Screens.ShareLogs)

    private fun saveAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { systemInteractor.putAccount() }
        result.error?.let { if(!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
    }

    /*
    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
    */

    override fun onBackCommandClick() {
        if(localeWasChanged) {
            localeWasChanged = false
            router.navigateTo(Screens.Main)
        }
        else super.onBackCommandClick()
    }

    private fun logEvent(param: String, value: String) {
        val map = HashMap<String, Any>()
        map[param] = value

        mFBA.logEvent(EVENT, createSingeBundle(param, value))
        eventsLogger.logEvent(EVENT, createSingeBundle(param, value))
        YandexMetrica.reportEvent(EVENT, map)
    }
}
