package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView

import kotlinx.android.synthetic.main.fragment_select_currency.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v4.content.ContextCompat


@Suppress("TooManyFunctions")
class SelectCurrencyFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    private lateinit var adapterPopular: CurrenciesListAdapter
    private lateinit var adapterAll: CurrenciesListAdapter
    override val layout = R.layout.fragment_select_currency

    @InjectPresenter
    internal lateinit var presenter: SelectCurrencyPresenter

    @ProvidePresenter
    fun createSelectCurrencyPresenter() = SelectCurrencyPresenter()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
        val parentActivity = activity
        if (parentActivity is BaseActivity) {
            val parentPresenter = parentActivity.getPresenter()
            if (parentPresenter is CurrencyChangedListener) presenter.addCurrencyChangedListener(parentPresenter)
        }

        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.sh_divider_light_gray)?.let { itemDecorator.setDrawable(it) }

        adapterAll = CurrenciesListAdapter { currency ->
            presenter.changeCurrency(currency)
            changeSelectedCurrency(currency)
        }
        rvAllCurrencies.adapter = adapterAll
        rvAllCurrencies.itemAnimator = DefaultItemAnimator()
        rvAllCurrencies.addItemDecoration(itemDecorator)

        adapterPopular = CurrenciesListAdapter { currency ->
            presenter.changeCurrency(currency)
            changeSelectedCurrency(currency)
        }
        rvPopularCurrencies.adapter = adapterPopular
        rvPopularCurrencies.itemAnimator = DefaultItemAnimator()
        rvPopularCurrencies.addItemDecoration(itemDecorator)
    }

    @CallSuper
    override fun onDestroyView() {
        presenter.removeCurrencyChangedListener()
        rvAllCurrencies.adapter = null
        rvPopularCurrencies.adapter = null
        super.onDestroyView()
    }

    // TODO move to base animation fragment
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        val animatorId: Int = if (enter) android.R.animator.fade_in else android.R.animator.fade_out
        val anim = AnimatorInflater.loadAnimator(activity, animatorId)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (enter) {
                    adapterAll.notifyDataSetChanged()
                    adapterPopular.notifyDataSetChanged()
                }
            }
        })

        return anim
    }

    override fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel) {
        adapterAll.setNewSelectedCurrency(selected)
        adapterAll.update(all)
    }

    override fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel) {
        adapterPopular.setNewSelectedCurrency(selected)
        adapterPopular.update(popular)
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
}
