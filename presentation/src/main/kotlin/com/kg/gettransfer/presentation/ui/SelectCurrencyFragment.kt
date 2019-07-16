package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import kotlinx.android.synthetic.main.fragment_select_currency.*

@Suppress("TooManyFunctions")
class SelectCurrencyFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    override val layout = R.layout.fragment_select_currency

    @InjectPresenter
    internal lateinit var presenter: SelectCurrencyPresenter

    @ProvidePresenter
    fun createSelectCurrencyPresenter() = SelectCurrencyPresenter()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAllCurrencies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvPopularCurrencies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
        val parentActivity = activity
        if (parentActivity is BaseActivity) {
            val parentPresenter = parentActivity.getPresenter()
            if (parentPresenter is CurrencyChangedListener) presenter.addCurrencyChangedListener(parentPresenter)
        }

        presenter.init()
    }

    @CallSuper
    override fun onDestroyView() {
        presenter.removeCurrencyChangedListener()
        super.onDestroyView()
    }

    override fun setCurrencies(all: List<CurrencyModel>, popular: List<CurrencyModel>, selected: CurrencyModel) {
        setRecyclerViewAdapter(rvAllCurrencies, all, selected)
        setRecyclerViewAdapter(rvPopularCurrencies, popular, selected)
    }

    private fun setRecyclerViewAdapter(recyclerView: RecyclerView, list: List<CurrencyModel>, selected: CurrencyModel) {
        recyclerView.adapter = CurrenciesListAdapter(list, selected) { currency ->
            presenter.changeCurrency(currency)
            changeSelectedCurrency(currency)
        }
    }

    private fun changeSelectedCurrency(newSelectedCurrency: CurrencyModel) {
        setNewSelectedCurrency(rvAllCurrencies, newSelectedCurrency)
        setNewSelectedCurrency(rvPopularCurrencies, newSelectedCurrency)
    }

    private fun setNewSelectedCurrency(recyclerView: RecyclerView, newSelectedCurrency: CurrencyModel) {
        val adapter = recyclerView.adapter
        if (adapter is CurrenciesListAdapter) {
            adapter.setNewSelectedCurrency(newSelectedCurrency)
            adapter.notifyDataSetChanged()
        }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setTransferNotFoundError(transferId: Long) {}
}
