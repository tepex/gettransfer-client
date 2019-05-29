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

	internal var offerId
		get() = reviewInteractor.offerIdForReview
		set(value) {
			reviewInteractor.offerIdForReview = value
		}

	internal var vehicleRating = 0F
	internal var driverRating = 0F
	internal var punctualityRating = 0F
	internal var currentComment
		get() = reviewInteractor.comment
		set(value) { reviewInteractor.comment = value }

	internal var hintComment: String = ""

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()

		viewState.setRatingVehicle(vehicleRating)
		viewState.setRatingDriver(driverRating)
		viewState.setRatingPunctuality(punctualityRating)
		onSecondaryRatingsChanged(vehicleRating, driverRating, punctualityRating)

		viewState.showProgress(false)
	}

	fun onClickSend(list: List<ReviewRateModel>, comment: String) = utils.launchSuspend {
		viewState.showProgress(true)
		viewState.blockInterface(true)
		fillRates(list)
		writeComment(comment)
		val rateResult = fetchResult { reviewInteractor.sendRates() }
		val commentResult = fetchResult { reviewInteractor.pushComment() }
		if (!rateResult.isError() && !commentResult.isError()) {
			viewState.exitAndReportSuccess(
					list,
					getFeedBackText(comment)
			)
			reviewInteractor.releaseRepo()
		}
		logAverageRate(list.map { it.rateValue }.average())
		logDetailRate(list, comment)
		viewState.blockInterface(false)
		viewState.showProgress(false)
	}

	fun onClickComment(currentComment: String) {
		viewState.showCommentEditor(getFeedBackText(currentComment))
	}

	private fun fillRates(list: List<ReviewRateModel>) {
		list.forEach {
			reviewInteractor.rates.add(reviewRateMapper.fromView(it))
		}
	}

	private fun writeComment(text: String) {
		reviewInteractor.comment = text
	}

	fun commentChanged(newComment: String) {
		reviewInteractor.comment = newComment
		updateViewComment()
	}

	private fun updateViewComment() {
		if (currentComment.isNotEmpty())
			viewState.showComment(currentComment)
		else
			viewState.showHint(hintComment)
	}

	fun onCommonRatingChanged(rating: Float) {
		updateSecondaryRatings(rating)
	}

	fun onSecondaryRatingsChanged(vararg rating: Float) {
		val currentCommonRating = rating.sum() / rating.size
		viewState.setRatingCommon(currentCommonRating)
	}

	private fun updateSecondaryRatings(rating: Float) {
		viewState.setRatingVehicle(rating)
		viewState.setRatingDriver(rating)
		viewState.setRatingPunctuality(rating)
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

	private fun getFeedBackText(text: String) =
			if (text == hintComment) ""
			else text
}