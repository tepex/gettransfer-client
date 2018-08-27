package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.presentation.model.ConfigsModel

import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.coroutines.experimental.Job

import timber.log.Timber

@InjectViewState
class SettingsPresenter(private val cc: CoroutineContexts,
                        private val apiInteractor: ApiInteractor): MvpPresenter<SettingsView>() {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)
    
    lateinit var configs: ConfigsModel
    lateinit var account: Account
    
    fun onBackCommandClick() {
        viewState.finish()
    }
    
    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            utils.asyncAwait { 
                configs = ConfigsModel(apiInteractor.getConfigs())
                account = apiInteractor.getAccount()
            }
            viewState.setCurrencies(configs.currencies)
            viewState.setLocales(configs.locales)
            viewState.setDistanceUnits(configs.distanceUnits)
            
            Timber.d("account: %s", account)
            if(account.currency != null) {
                val currencyModel = configs.currencies.find { it.delegate == account.currency }
                viewState.setCurrency(currencyModel?.name ?: "")
            }
            if(account.locale != null) {
                val localeModel = configs.locales.find { it.delegate == account.locale }
                viewState.setLocale(localeModel?.name ?: "")
            }
            if(account.distanceUnit != null) viewState.setDistanceUnit(account.distanceUnit!!)
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
    
    fun changeCurrency(selected: Int) {
        val currencyModel = configs.currencies.get(selected)
        account.currency = currencyModel.delegate
        viewState.setCurrency(currencyModel.name)
        saveAccount()
    }

    fun changeLocale(selected: Int) {
        val localeModel = configs.locales.get(selected)
        account.locale = localeModel.delegate
        viewState.setLocale(localeModel.name)
        saveAccount()
    }

    fun changeDistanceUnit(selected: Int) {
        account.distanceUnit = configs.distanceUnits.get(selected)
        viewState.setDistanceUnit(account.distanceUnit!!)
        saveAccount()
    }
    
    private fun saveAccount() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            utils.asyncAwait { apiInteractor.setAccount(account) }
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
    
    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}
