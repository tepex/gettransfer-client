package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.CurrencyModel
import kotlinx.android.synthetic.main.toolbar.*

class SelectCurrencyFragment : SelectCurrencyBottomFragment() {

    override val layout = R.layout.fragment_select_currency

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar_title.text = getString(R.string.LNG_CURRENCIES_CHOOSE)
        toolbar_btnBack.isVisible = true
        toolbar_btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun currencyChanged(currency: CurrencyModel) {
        super.currencyChanged(currency)
        // Hide fragment after changed value
        findNavController().navigateUp()
    }
}
