package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.presentation.mapper.ReviewRateMapper
import com.kg.gettransfer.presentation.model.ReviewRateModel
import com.kg.gettransfer.presentation.view.RatingDetailView
import com.kg.gettransfer.utilities.Analytics
import org.koin.core.inject


@InjectViewState
class RatingDetailPresenter : BasePresenter<RatingDetailView>() {

	private val reviewInteractor: ReviewInteractor by inject()
	private val reviewRateMapper: ReviewRateMapper by inject()

	internal var offerId
		get() = reviewInteractor.offerRateID
		set(value) { reviewInteractor.offerRateID = value }

	internal var vehicleRating: Float?
		get() = reviewInteractor.vehicleRating
		set(value) { reviewInteractor.vehicleRating = value }

	internal var driverRating: Float?
		get() = reviewInteractor.driverRating
		set(value) { reviewInteractor.driverRating = value }

	internal var communicationRating: Float?
		get() = reviewInteractor.communicationRating
		set(value) { reviewInteractor.communicationRating = value }

	internal var comment: String
		get() = reviewInteractor.comment
		set(value) { reviewInteractor.comment = value }


	override fun onFirstViewAttach() {
		super.onFirstViewAttach()
		initRatingFields()
		viewState.showProgress(false)
	}

	private fun initRatingFields() {
		vehicleRating?.let { viewState.setRatingVehicle(it) }
		driverRating?.let { viewState.setRatingDriver(it) }
		communicationRating?.let { viewState.setRatingPunctuality(it) }
		viewState.setDividersVisibility()
		if (comment.isNotEmpty()) viewState.showComment(comment)
		ratingChanged()
	}

	fun onCommonRatingChanged(rating: Float) {
		updateSecondaryRatings(rating)
	}

	private fun updateSecondaryRatings(rating: Float) {
		if (vehicleRating != null) {
			vehicleRating = rating
			viewState.setRatingVehicle(rating)
		}
		if (driverRating != null) {
			driverRating = rating
			viewState.setRatingDriver(rating)
		}
		if (communicationRating != null) {
			communicationRating = rating
			viewState.setRatingPunctuality(rating)
		}
	}

	fun ratingChanged() {
		listOfNotNull(vehicleRating, driverRating, communicationRating).let {
			if (it.isNotEmpty()) {
				viewState.setRatingCommon(it.sum() / it.size)
			}
		}
	}

	fun onClickSend() = utils.launchSuspend {
		val listRates = reviewInteractor.createListOfDetailedRates().map { reviewRateMapper.toView(it) }
		viewState.showProgress(true)
		viewState.blockInterface(true, true)
		val rateResult = fetchResult { reviewInteractor.sendRates(false) }
		val commentResult = fetchResult { reviewInteractor.pushComment() }
		if (!rateResult.isError() && !commentResult.isError())
			viewState.exitAndReportSuccess(listRates, comment)
		logAverageRate(listRates.map { it.rateValue }.average())
		logDetailRate(listRates, comment)
		viewState.blockInterface(false)
		viewState.showProgress(false)
	}

	fun onClickComment(comment: String) {
		viewState.showCommentDialog(comment)
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