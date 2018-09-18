package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripDetailsView: BaseView{
    fun setTripInfo(transferId: Long,
                    from: String,
                    to: String,
                    dateTime: String,
                    distance: Int,
                    countPassengers: Int?,
                    passengerName: String?,
                    countChild: Int,
                    flightNumber: String?,
                    comment: String?,
                    pay: String?)
}