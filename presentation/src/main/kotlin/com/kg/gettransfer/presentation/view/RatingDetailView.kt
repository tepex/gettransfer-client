package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


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
	fun showComment(comment: String)

	@StateStrategyType(OneExecutionStateStrategy::class)
	fun showCommentEditor(comment: String)

	@StateStrategyType(OneExecutionStateStrategy::class)
	fun exitAndReportSuccess(averageRating: Float)
}