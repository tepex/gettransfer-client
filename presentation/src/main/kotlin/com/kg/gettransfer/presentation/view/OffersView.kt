package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface OffersView: BaseView {
    fun setTransfer(transferModel: TransferModel)
    fun setDate(date: String)
}
