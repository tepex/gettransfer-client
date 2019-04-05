package com.kg.gettransfer.presentation.presenter
package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.view.RatingDetailView


@InjectViewState
class RatingDetailPresenter : BasePresenter<RatingDetailView>() {

	internal var transferId = 0L
	internal var currentCommonRating = 0F

	internal var hintComment: String = ""

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()
		viewState.setRatingCommon(currentCommonRating)
		updateSecondaryRatings()
		viewState.showProgress(false)
	}

	fun onClickComment(currentComment: String) {
		if (currentComment != hintComment)
			viewState.showCommentEditor(currentComment)
	}

	fun commentChanged(newComment: String) {
		viewState.showComment(newComment)
	}

	fun onCommonRatingChanged(rating: Float) {
		currentCommonRating = rating
		updateSecondaryRatings()
	}

	fun onSecondaryRatingsChanged(vararg rating: Float) {
		currentCommonRating = rating.sum() / rating.size
		viewState.setRatingCommon(currentCommonRating)
	}

	private fun updateSecondaryRatings() {
		viewState.setRatingDriver(currentCommonRating)
		viewState.setRatingPunctuality(currentCommonRating)
		viewState.setRatingVehicle(currentCommonRating)
	}
}