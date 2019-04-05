package com.kg.gettransfer.presentation.presenter
package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.presentation.mapper.ReviewRateMapper
import com.kg.gettransfer.presentation.model.ReviewRateModel
import com.kg.gettransfer.presentation.view.RatingDetailView
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.inject


@InjectViewState
class RatingDetailPresenter : BasePresenter<RatingDetailView>() {

	private val reviewInteractor: ReviewInteractor by inject()
	private val reviewRateMapper: ReviewRateMapper by inject()

	internal var offerId = 0L
		set(value) {
			reviewInteractor.offerIdForReview = value
			field = value
		}

	internal var currentCommonRating = 0F
	internal var currentComment = ""

	internal var hintComment: String = ""

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()
		viewState.setRatingCommon(currentCommonRating)
		updateSecondaryRatings()
		viewState.showProgress(false)
		if (currentComment.isNotEmpty())
			viewState.showComment(currentComment)
		else
			viewState.showComment(hintComment)
	}

	fun onClickSend(list: List<ReviewRateModel>, comment: String, commonRating: Float) = utils.launchSuspend {
		viewState.showProgress(true)
		viewState.blockInterface(true)
		fetchResult(WITHOUT_ERROR) {
			reviewInteractor.sendRates(
				list.map { reviewRateMapper.fromView(it) },
				if (comment == hintComment)
					""
				else
					comment
			)
		}.also { result ->
			logAverageRate(list.map { it.rateValue }.average())
			logDetailRate(list, comment)
			viewState.blockInterface(false)
			viewState.showProgress(false)
			result.error?.let {
				viewState.setError(it)
			} ?: viewState.exitAndReportSuccess(
				commonRating,
				if (comment == hintComment)
					""
				else
					comment
			)
		}
	}

	fun onClickComment(currentComment: String) {
		viewState.showCommentEditor(
			if (currentComment != hintComment)
				currentComment
			else
				""
		)
	}

	fun commentChanged(newComment: String) {
		viewState.showComment(
			if (newComment.isEmpty())
				hintComment
			else
				newComment
		)
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
		viewState.setRatingVehicle(currentCommonRating)
		viewState.setRatingDriver(currentCommonRating)
		viewState.setRatingPunctuality(currentCommonRating)
	}

	private fun logAverageRate(rate: Double) =
		analytics.logEvent(
			Analytics.REVIEW_AVERAGE,
			createStringBundle(Analytics.REVIEW,rate.toString()),
			mapOf(Analytics.REVIEW to rate)
		)

	private fun logDetailRate(list: List<ReviewRateModel>, comment: String) {
		val map = mutableMapOf<String, String?>()
		val bundle = Bundle()
		list.forEach {
			val key = analytics.reviewDetailKey(it.rateType.type)
			bundle.putInt(key, it.rateValue)
			map[key] = it.rateValue.toString()
		}
		map[Analytics.REVIEW_COMMENT] = comment
		bundle.putString(Analytics.REVIEW_COMMENT, comment)
		analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_DETAILED, bundle, map)
	}
}