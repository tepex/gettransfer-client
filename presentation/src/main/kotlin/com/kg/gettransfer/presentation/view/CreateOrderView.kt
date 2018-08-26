package com.kg.gettransfer.presentation.view

import android.widget.TextView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.domain.model.GTAddress

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView: MvpView {
    fun finish()

    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setCurrencies(currencies: List<CurrencyModel>)
    
    fun setRoute(route: Pair<GTAddress, GTAddress>)
    fun setCounters(textViewCounter: TextView, count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String)
    fun setComment(comment: String)
    
}
