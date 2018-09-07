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

import java.util.Currency
import java.util.Locale

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
            Timber.d("account: $account")
            
			val locale = account.locale ?: Locale.getDefault()
            val localeModel = configs.locales.find { it.delegate.language == locale.language }
            viewState.setLocale(localeModel?.name ?: "")
            
            val currency = account.currency ?: Currency.getInstance(locale)
            val currencyModel = configs.currencies.find { it.delegate == currency }
            viewState.setCurrency(currencyModel?.name ?: "")
            
            viewState.setDistanceUnit(account.distanceUnit ?: configs.distanceUnits.first())
                
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
    
    fun onLogout() {
        Timber.d("account logout")
        apiInteractor.logout()
        router.exit()
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
