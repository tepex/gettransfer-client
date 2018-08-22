package com.kg.gettransfer.presentation.view

import android.widget.TextView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.TransferType

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView: MvpView {
    fun finish()

    fun setCounters(textViewCounter: TextView, count: Int)
    fun setCurrency(currencySymbol: CharSequence)
    fun setDateTimeTransfer(dateTimeString: String)
    fun setComment(comment: String)
    fun setTransferTypeList(listTypes: List<TransferType>)
}