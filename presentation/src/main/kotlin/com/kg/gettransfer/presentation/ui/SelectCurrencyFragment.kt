package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.presenter.SelectCurrencyFragmentPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView
import kotlinx.android.synthetic.main.fragment_select_currency.*

class SelectCurrencyFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    @InjectPresenter
    internal lateinit var presenter: SelectCurrencyFragmentPresenter

    @ProvidePresenter
    fun createSelectCurrencyFragmentPresenter() = SelectCurrencyFragmentPresenter()

    private lateinit var mPresenter: BasePresenter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_select_currency, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = (activity as BaseActivity).getPresenter()
        rvAllCurrencies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvPopularCurrencies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun setCurrencies(all: List<CurrencyModel>, popular: List<CurrencyModel>, selected: CurrencyModel) {
        setRecyclerViewAdapter(rvAllCurrencies, all, selected)
        setRecyclerViewAdapter(rvPopularCurrencies, popular, selected)
    }

    private fun setRecyclerViewAdapter(recyclerView: RecyclerView, list: List<CurrencyModel>, selected: CurrencyModel) {
        recyclerView.adapter = CurrenciesListAdapter(list, selected) {
            presenter.changeCurrency(it)
            changeSelectedCurrency(it)
        }
    }

    private fun changeSelectedCurrency(newSelectedCurrency: CurrencyModel) {
        setNewSelectedCurrency(rvAllCurrencies, newSelectedCurrency)
        setNewSelectedCurrency(rvPopularCurrencies, newSelectedCurrency)
    }

    private fun setNewSelectedCurrency(recyclerView: RecyclerView, newSelectedCurrency: CurrencyModel) {
        (recyclerView.adapter as CurrenciesListAdapter).apply {
            setNewSelectedCurrency(newSelectedCurrency)
            notifyDataSetChanged()
        }
    }

    override fun currencyChanged() { mPresenter.currencyChanged() }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
}