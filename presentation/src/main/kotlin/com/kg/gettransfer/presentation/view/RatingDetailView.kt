package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.ReviewRate

interface RatingDetailView : BaseView {

	@StateStrategyType(AddToEndSingleStrategy::class)
	fun showProgress(isShow: Boolean)

	@StateStrategyType(AddToEndSingleStrategy::class)
	fun setRatingCommon(rating: Float)
	@StateStrategyType(AddToEndSingleStrategy::class)
	fun setRatingVehicle(rating: Float)
	@StateStrategyType(AddToEndSingleStrategy::class)
	fun setRatingDriver(rating: Float)
	@StateStrategyType(AddToEndSingleStrategy::class)
	fun setRatingPunctuality(rating: Float)
	@StateStrategyType(AddToEndSingleStrategy::class)
	fun setDividersVisibility()
	@StateStrategyType(AddToEndSingleStrategy::class)
	fun showComment(comment: String)

	@StateStrategyType(OneExecutionStateStrategy::class)
	fun showCommentDialog(comment: String)

	@StateStrategyType(OneExecutionStateStrategy::class)
	fun exitAndReportSuccess(list: List<ReviewRate>, comment: String)
}