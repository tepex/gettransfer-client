package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SelectCurrencyPresenter : BasePresenter<SelectCurrencyView>(), KoinComponent {

    private val currencies = systemInteractor.currencies.map { it.map() }
    private val popularCurrencies = currencies.filter { Currency.POPULAR.contains(it.code) }
    private var currencyChangedListener: CurrencyChangedListener? = null

    override fun attachView(view: SelectCurrencyView) {
        super.attachView(view)
        viewState.setCurrencies(
            currencies.filter { !Currency.POPULAR.contains(it.code) },
            popularCurrencies,
            sessionInteractor.currency.map()
        )
    }

    fun addCurrencyChangedListener(currencyChangedListener: CurrencyChangedListener) {
        this.currencyChangedListener = currencyChangedListener
    }

    fun removeCurrencyChangedListener() {
        currencyChangedListener = null
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        saveGeneralSettings()
        currencyChangedListener?.currencyChanged(selected)
    }
}
