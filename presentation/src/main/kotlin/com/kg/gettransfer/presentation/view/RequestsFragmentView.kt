package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Transfer

import java.text.Format

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsFragmentView: BaseView {
    fun setRequests(transfers: List<Transfer>, distanceUnit: DistanceUnit, dateFormat: Format)
}
