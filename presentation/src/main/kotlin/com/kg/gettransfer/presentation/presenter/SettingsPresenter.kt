package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.ConfigsModel

import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SettingsPresenter(private val cc: CoroutineContexts,
                        private val router: Router,
                        private val apiInteractor: ApiInteractor): MvpPresenter<SettingsView>() {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)
    
    lateinit var configs: ConfigsModel
    lateinit var account: Account
    
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ ->
                Timber.d("result from login")
                saveAccount()
        })
    }
    
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
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
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
        viewState.blockInterface(true)
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            utils.asyncAwait { apiInteractor.putAccount(account) }
        }, { e ->
            if(e is ApiException && e.isNotLoggedIn()) login()
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
    
    private fun login() {
        //router.navigateTo(Screens.LOGIN) 
    }
}
