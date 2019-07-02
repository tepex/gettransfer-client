package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import org.koin.core.KoinComponent
import org.koin.core.get

@InjectViewState
class SelectCurrencyPresenter : BasePresenter<SelectCurrencyView>(), KoinComponent {

    private val currencies = systemInteractor.currencies.map { it.map() }
    private val popularCurrencies = currencies.filter { Currency.POPULAR.contains(it.code) }

    override fun attachView(view: SelectCurrencyView) {
        super.attachView(view)
        viewState.setCurrencies(
            currencies.filter { !Currency.POPULAR.contains(it.code) },
            popularCurrencies,
            sessionInteractor.currency.map()
        )
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        saveGeneralSettings()
        viewState.currencyChanged()
    }
}
