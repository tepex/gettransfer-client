package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View

import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.presentation.adapter.CurrenciesListAdapter
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener
import com.kg.gettransfer.presentation.presenter.SelectCurrencyPresenter
import com.kg.gettransfer.presentation.view.SelectCurrencyView
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils

import kotlinx.android.synthetic.main.layout_select_currency.*

@Suppress("TooManyFunctions")
open class SelectCurrencyBottomFragment : BaseBottomSheetFragment(), SelectCurrencyView {

    private lateinit var adapterPopular: CurrenciesListAdapter
    private lateinit var adapterAll: CurrenciesListAdapter
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
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int) =
        FragmentUtils.onCreateAnimation(requireContext(), enter) {
            adapterAll.notifyDataSetChanged()
            adapterPopular.notifyDataSetChanged()
        }

    override fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel) {
        adapterAll.setNewSelectedCurrency(selected)
        adapterAll.update(all)
    }

    override fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel) {
        adapterPopular.setNewSelectedCurrency(selected)
        adapterPopular.update(popular)
    }

    override fun currencyChanged(currency: CurrencyModel) {
        listener?.currencyChanged(currency)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: ApiException) {}

    override fun setError(e: DatabaseException) {}

    override fun setTransferNotFoundError(transferId: Long) {}

    @CallSuper
    override fun onDestroyView() {
        listener = null
        rvAllCurrencies.adapter = null
        rvPopularCurrencies.adapter = null
        super.onDestroyView()
    }
}
