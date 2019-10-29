package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.Transfer

@StateStrategyType(OneExecutionStateStrategy::class)
interface RatingLastTripView : BaseMapDialogView {

    fun setupReviewForLastTrip(transfer: Transfer)

    fun cancelReview()

    fun thanksForRate()

    fun showDetailedReview()
}
