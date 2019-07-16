package com.kg.gettransfer.presentation.adapter

import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.model.CurrencyModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_currency_item.view.*

class CurrenciesListAdapter(
        private val currencies: List<CurrencyModel>,
        private var selectedCurrency: CurrencyModel,
        private val listener: SelectCurrencyListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = currencies.size

    fun setNewSelectedCurrency(newSelectedCurrency: CurrencyModel) {selectedCurrency = newSelectedCurrency}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) =
            (holder as ViewHolderCurrency).bind(currencies[pos], currencies[pos].code == selectedCurrency.code, listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolderCurrency(LayoutInflater.from(parent.context).inflate(R.layout.view_currency_item, parent, false))

    class ViewHolderCurrency(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(currency: CurrencyModel, isSelected: Boolean, listener: SelectCurrencyListener) = with(containerView) {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(currencySymbol, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            currencySymbol.text = currency.symbol
            currencyName.text = currency.nameWithoutSymbol
            imgSelected.isVisible = isSelected
            setThrottledClickListener { listener(currency) }
        }
    }
}

typealias SelectCurrencyListener = (CurrencyModel) -> Unit