package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.model.CurrencyModel
import kotlinx.android.synthetic.main.fragment_select_currency.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

class SelectCurrencyFragment : SelectCurrencyBottomFragment() {

    override val layout = R.layout.fragment_select_currency

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.ivBack.setThrottledClickListener { findNavController().navigateUp() }
        toolbar.toolbar_title.text = getString(R.string.LNG_CURRENCIES_CHOOSE)
    }

    override fun currencyChanged(currency: CurrencyModel) {
        findNavController().navigateUp()
    }
}
