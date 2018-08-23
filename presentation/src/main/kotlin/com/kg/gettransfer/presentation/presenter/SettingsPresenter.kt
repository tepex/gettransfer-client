package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.presentation.view.SettingsView

import java.util.Currency
import java.util.Locale

import kotlinx.coroutines.experimental.Job

import timber.log.Timber

@InjectViewState
class SettingsPresenter(private val cc: CoroutineContexts,
                        private val apiInteractor: ApiInteractor): MvpPresenter<SettingsView>() {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)
    private lateinit var configs: Configs
    lateinit var currencies: List<CurrencyModel>
    lateinit var locales: List<LocaleModel>
    
    fun onBackCommandClick() {
        viewState.finish()
    }
    
    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            configs = utils.asyncAwait { apiInteractor.configs() }
            currencies = configs.supportedCurrencies.map {
                CurrencyModel("${it.displayName} (${it.hackedSymbol})", it.currencyCode)
            }
            locales = configs.availableLocales.map { LocaleModel(it.getDisplayLanguage(it), it.language) }
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
    
    fun changeCurrency(selected: Int) {
        viewState.setSettingsCurrency(currencies.get(selected).name)
    }

    fun changeLanguage(selected: Int) {
        viewState.setSettingsLanguage(locales.get(selected).name)
    }

    fun changeDistanceUnit(which: Int){
        val distanceUnits = arrayOf("km", "ml")
        viewState.setSettingsDistanceUnits(distanceUnits[which])
    }
    
    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}

/* Dirty hack for ruble, Yuan ¥ */
val Currency.hackedSymbol: String
    get() = when(currencyCode) {
        "RUB" -> "\u20BD"
        "CNY" -> "¥"
        else  -> symbol
    }
class CurrencyModel(val name: String, val code: String): CharSequence by name {
    override fun toString(): String = name
}

class LocaleModel(val name: String, val code: String): CharSequence by name {
    override fun toString(): String = name
}
