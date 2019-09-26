package com.kg.gettransfer.presentation.ui

import android.content.Context

import androidx.annotation.CallSuper

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import kotlinx.android.synthetic.main.layout_select_currency.*

open class SelectCurrencyBottomFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    override val layout = R.layout.fragment_select_currency_bottom

    @InjectPresenter
    internal lateinit var presenter: SelectCurrencyPresenter

    @ProvidePresenter
    fun createSelectCurrencyPresenter() = SelectCurrencyPresenter()

    private var listener: CurrencyChangedListener? = null

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CurrencyChangedListener) {
            listener = context
        }
    }

    override fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel) {
        rvAllCurrencies.adapter = CurrenciesListAdapter(presenter::changeCurrency, all, selected)
    }

    override fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel) {
        rvPopularCurrencies.adapter = CurrenciesListAdapter(presenter::changeCurrency, popular, selected)
    }

    override fun sendEvent(currency: CurrencyModel) {
        listener?.currencyChanged(currency)
    }

    @CallSuper
    override fun onDestroyView() {
        listener = null
        rvAllCurrencies.adapter = null
        rvPopularCurrencies.adapter = null
        super.onDestroyView()
    }
}
