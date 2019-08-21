package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import com.kg.gettransfer.sys.presentation.ConfigsManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SelectCurrencyPresenter : MvpPresenter<SelectCurrencyView>(), KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("SelectCurrencyPresenter") }
    private val sessionInteractor: SessionInteractor by inject()
    private val configsManager: ConfigsManager by inject()

    override fun attachView(view: SelectCurrencyView?) {
        super.attachView(view)
        worker.main.launch {
            val selectedCurrency = withContext(worker.bg) { sessionInteractor.currency.map() }
            val currencies = withContext(worker.bg) { configsManager.configs.supportedCurrencies.map { it.map() } }
            viewState.setCurrencies(currencies, selectedCurrency)

            val popularCurrencies = withContext(worker.bg) { currencies.filter { Currency.POPULAR.contains(it.code) } }
            viewState.setPopularCurrencies(popularCurrencies, selectedCurrency)
        }
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        viewState.sendEvent(selected)
    }


    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

}
