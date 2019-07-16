package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsCalendarFragmentView : BaseView {
    fun setCalendarIndicators(calendarItems: Map<String, List<CarrierTripBaseModel>>)
    fun setItemsInRVDailyTrips(items: List<CarrierTripBaseModel>)
    fun selectDate(selectedDate: String)
}