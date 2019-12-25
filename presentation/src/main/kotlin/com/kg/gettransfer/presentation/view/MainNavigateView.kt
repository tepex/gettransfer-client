package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface MainNavigateView : BaseView, GooglePlayView {
    fun setEventCount(isVisible: Boolean, count: Int)
    fun showDetailedReview()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showRateForLastTrip(transferId: Long, vehicle: String, color: String)
    fun showNewDriverAppDialog()

    companion object {
        val EXTRA_RATE_TRANSFER_ID = "${NewTransferMapView::class.java.name}.rate_transfer_id"
        val EXTRA_RATE_VALUE = "${NewTransferMapView::class.java.name}.rate_value"
        val SHOW_ABOUT = "${NewTransferMapView::class.java.name}.show_about"
    }
}
