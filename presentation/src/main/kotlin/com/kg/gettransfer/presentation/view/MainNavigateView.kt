package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.ProfileModel

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface MainNavigateView : BaseView {
    fun setEventCount(isVisible: Boolean, count: Int)
    fun showDetailedReview()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showRateForLastTrip(transferId: Long, vehicle: String, color: String)

    companion object {
        val EXTRA_RATE_TRANSFER_ID = "${NewTransferMapView::class.java.name}.rate_transfer_id"
        val EXTRA_RATE_VALUE = "${NewTransferMapView::class.java.name}.rate_value"
    }
}
