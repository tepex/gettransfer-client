package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.Transfer

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsView: MvpView {
    fun finish()
    fun setRequestsFragments(transfersActive: ArrayList<Transfer>, transfersAll: ArrayList<Transfer>,
                             transfersCompleted: ArrayList<Transfer>, distanceUnit: String)
}