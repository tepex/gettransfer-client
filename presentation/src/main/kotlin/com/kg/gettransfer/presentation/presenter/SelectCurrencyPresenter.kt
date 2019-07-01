package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.presentation.mapper.CurrencyMapper
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.view.SelectCurrencyView
import org.koin.core.KoinComponent
import org.koin.core.get

@InjectViewState
class SelectCurrencyPresenter : BasePresenter<SelectCurrencyView>(), KoinComponent {

    private val currencyMapper = get<CurrencyMapper>()

    private val currencies = sessionInteractor.currencies.map { currencyMapper.toView(it) }
    private val popularCurrencies = currencies.filter { Currency.POPULAR_CURRENCIES.contains(it.code) }

    @CallSuper
    override fun attachView(view: SelectCurrencyView) {
        super.attachView(view)
        viewState.setCurrencies(currencies.filter { !Currency.POPULAR_CURRENCIES.contains(it.code) },
                popularCurrencies, sessionInteractor.currency.let { currencyMapper.toView(it) })
    }

    fun changeCurrency(selected: CurrencyModel) {
        sessionInteractor.currency = selected.delegate
        saveGeneralSettings()
        viewState.currencyChanged()
    }
}