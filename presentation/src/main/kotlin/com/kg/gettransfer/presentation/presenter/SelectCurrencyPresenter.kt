package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectCurrencyView
import kotlinx.coroutines.*

import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

@InjectViewState
class SelectCurrencyPresenter : MvpPresenter<SelectCurrencyView>(), CoroutineScope, KoinComponent {

    private val sessionInteractor: SessionInteractor by inject()

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun attachView(view: SelectCurrencyView?) {
        super.attachView(view)
        // This launch uses the coroutineContext defined
        // by the coroutine presenter.
        launch {
            val selectedCurrency = sessionInteractor.currency.map()
            val currencies = sessionInteractor.systemInteractor.currencies.map { it.map() }
            withContext(Dispatchers.Main) {
                viewState.setCurrencies(currencies, selectedCurrency)
            }
            val popularCurrencies = currencies.filter { Currency.POPULAR.contains(it.code) }
            withContext(Dispatchers.Main) {
                viewState.setPopularCurrencies(popularCurrencies, selectedCurrency)
            }
        }
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        viewState.sendEvent(selected)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
