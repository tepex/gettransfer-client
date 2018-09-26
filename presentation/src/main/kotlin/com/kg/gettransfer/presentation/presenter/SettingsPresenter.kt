package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.presentation.model.Mappers

import com.kg.gettransfer.presentation.view.SettingsView

import java.util.Currency
import java.util.Locale

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SettingsPresenter(cc: CoroutineContexts,
                        router: Router,
                        systemInteractor: SystemInteractor): BasePresenter<SettingsView>(cc, router, systemInteractor) {

    private val currencies = Mappers.getCurrenciesModels(systemInteractor.getCurrencies())
    private val locales = Mappers.getLocalesModels(systemInteractor.getLocales())
    private val distanceUnits = Mappers.getDistanceUnitsModels(systemInteractor.getDistanceUnits())
    
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ ->
                Timber.d("result from login")
                saveAccount()
        })
    }
    
    @CallSuper
    override fun attachView(view: SettingsView) {
        super.attachView(view)
        viewState.setCurrencies(currencies)
        viewState.setLocales(locales)
        viewState.setDistanceUnits(distanceUnits)
            
		val locale = systemInteractor.locale
        val localeModel = locales.find { it.delegate.language == locale.getLanguage() }
        viewState.setLocale(localeModel?.name ?: "")
            
        val currency = systemInteractor.currency
        val currencyModel = currencies.find { it.delegate == currency }
        viewState.setCurrency(currencyModel?.name ?: "")           
        viewState.setDistanceUnit(systemInteractor.distanceUnit.name)
            
        viewState.setLogoutButtonEnabled(systemInteractor.isLoggedIn())
    }
    
    fun changeCurrency(selected: Int) {
        val currencyModel = currencies.get(selected)
        systemInteractor.currency = currencyModel.delegate
        viewState.setCurrency(currencyModel.name)
        saveAccount()
    }

    fun changeLocale(selected: Int) {
        val localeModel = locales.get(selected)
        systemInteractor.locale = localeModel.delegate
        viewState.setLocale(localeModel.name)
        saveAccount()
    }

    fun changeDistanceUnit(selected: Int) {
        val distanceUnit = distanceUnits.get(selected)
        systemInteractor.distanceUnit = distanceUnit.delegate
        viewState.setDistanceUnit(distanceUnit.name)
        saveAccount()
    }
    
    fun onLogout() {
        systemInteractor.logout()
        router.exit()
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
}
