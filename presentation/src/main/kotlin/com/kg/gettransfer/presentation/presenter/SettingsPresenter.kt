package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.Mappers

import com.kg.gettransfer.presentation.view.SettingsView

import java.util.Locale

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SettingsPresenter(cc: CoroutineContexts,
                        router: Router,
                        systemInteractor: SystemInteractor): BasePresenter<SettingsView>(cc, router, systemInteractor) {

    private lateinit var currencies: List<CurrencyModel>
    private val locales = (systemInteractor.locales?.let { Mappers.getLocalesModels(it) } ?: emptyList<LocaleModel>()).filter { it.locale == "EN" || it.locale == "RU" }
    private val distanceUnits = systemInteractor.distanceUnits?.let { Mappers.getDistanceUnitsModels(it) } ?: emptyList<DistanceUnitModel>()
    private val endpoints = systemInteractor.endpoints.map { Mappers.getEndpointModel(it) }

    private var localeWasChanged = false

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> saveAccount() })
    }

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
        currencies = systemInteractor.currencies?.let { Mappers.getCurrenciesModels(it) } ?: emptyList<CurrencyModel>()

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
        systemInteractor.logout()
        val endpoint = endpoints.get(selected)
        systemInteractor.endpoint = endpoint.delegate
        viewState.setEndpoint(endpoint)
        router.exit() //Without restarting app
        //viewState.restartApp() //For restart app
    }

    fun onLogout() {
        systemInteractor.logout()
        router.exit()
        logEvent(LOG_OUT_PARAM, EMPTY_VALUE)
    }

    fun onLogsClicked() {
        router.navigateTo(Screens.SHARE_LOGS)
    }

    private fun saveAccount() {
        viewState.blockInterface(true)
        utils.launchAsyncTryCatchFinally({
            utils.asyncAwait { systemInteractor.putAccount() }
        }, { e ->
            if(e is ApiException && e.isNotLoggedIn()) {}
            else viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }

    override fun onBackCommandClick() {
        if (localeWasChanged) {
            localeWasChanged = false
            router.navigateTo(Screens.MAIN)
        }
        else super.onBackCommandClick()
    }

    private fun logEvent(param: String, value: String) {
        mFBA.logEvent(EVENT, createSingeBundle(param, value))
    }
}
