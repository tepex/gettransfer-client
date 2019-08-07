package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils

import kotlinx.android.synthetic.main.fragment_select_currency.*

import timber.log.Timber

class SelectCurrencyFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    private lateinit var adapterPopular: CurrenciesListAdapter
    private lateinit var adapterAll: CurrenciesListAdapter
    override val layout = R.layout.fragment_select_currency

    @InjectPresenter
    internal lateinit var presenter: SelectCurrencyPresenter

    @ProvidePresenter
    fun createSelectCurrencyPresenter() = SelectCurrencyPresenter()

    private var listener: CurrencyChangedListener? = null

    @CallSuper
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CurrencyChangedListener) {
            listener = context
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)

        @Suppress("UnsafeCallOnNullableType")
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.sh_divider_light_gray)?.let { drawable ->
            itemDecorator.setDrawable(drawable)
        }

        adapterAll = CurrenciesListAdapter(presenter::changeCurrency)
        rvAllCurrencies.adapter = adapterAll
        rvAllCurrencies.itemAnimator = DefaultItemAnimator()
        rvAllCurrencies.addItemDecoration(itemDecorator)

        adapterPopular = CurrenciesListAdapter(presenter::changeCurrency)
        rvPopularCurrencies.adapter = adapterPopular
        rvPopularCurrencies.itemAnimator = DefaultItemAnimator()
        rvPopularCurrencies.addItemDecoration(itemDecorator)
    }

    /**
     * Update UI after finished start fragment
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {
            adapterAll.notifyDataSetChanged()
            adapterPopular.notifyDataSetChanged()
        }
    }

    override fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel) {
        adapterAll.setNewSelectedCurrency(selected)
        adapterAll.update(all)
    }

    override fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel) {
        adapterPopular.setNewSelectedCurrency(selected)
        adapterPopular.update(popular)
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
