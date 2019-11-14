package com.kg.gettransfer.presentation.ui

import android.content.Context

import androidx.annotation.CallSuper

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import kotlinx.android.synthetic.main.layout_select_currency.*

@Suppress("TooManyFunctions")
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

    override fun currencyChanged(currency: CurrencyModel) {
        listener?.currencyChanged(currency)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: ApiException) {}

    override fun setError(e: DatabaseException) {}

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {}

    @CallSuper
    override fun onDestroyView() {
        listener = null
        rvAllCurrencies.adapter = null
        rvPopularCurrencies.adapter = null
        super.onDestroyView()
    }
}
