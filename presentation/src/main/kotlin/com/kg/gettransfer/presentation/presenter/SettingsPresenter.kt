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
    var account: Account? = null
    
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
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
    
    fun changeCurrency(selected: Int) {
        viewState.setCurrency(configs.currencies.get(selected).name)
    }

    fun changeLocale(selected: Int) {
        viewState.setLocale(configs.locales.get(selected).name)
    }

    fun changeDistanceUnit(selected: Int) {
        viewState.setDistanceUnit(configs.distanceUnits.get(selected))
    }
    
    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}
