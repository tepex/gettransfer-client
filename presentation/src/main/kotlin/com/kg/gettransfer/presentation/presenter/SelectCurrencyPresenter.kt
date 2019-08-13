package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import com.kg.gettransfer.sys.presentation.ConfigsManager

import org.koin.core.KoinComponent
import org.koin.core.inject

@InjectViewState
class SelectCurrencyPresenter : MvpPresenter<SelectCurrencyView>(), KoinComponent {

    private val sessionInteractor: SessionInteractor by inject()
    private val configsManager: ConfigsManager by inject()

    override fun attachView(view: SelectCurrencyView?) {
        super.attachView(view)
        val selectedCurrency = sessionInteractor.currency.map()
        val currencies = configsManager.configs.supportedCurrencies.map { it.map() }
        viewState.setCurrencies(currencies, selectedCurrency)
        val popularCurrencies = currencies.filter { Currency.POPULAR.contains(it.code) }
        viewState.setPopularCurrencies(popularCurrencies, selectedCurrency)
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        viewState.sendEvent(selected)
    }
}
