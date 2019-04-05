package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.extensions.show
import com.kg.gettransfer.presentation.model.ReviewRateModel
import com.kg.gettransfer.presentation.presenter.RatingDetailPresenter
import com.kg.gettransfer.presentation.ui.TextEditorActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.RatingDetailView
import com.willy.ratingbar.BaseRatingBar
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.btnSend
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.commonRate
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.driverRate
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.ivClose
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.progressBar
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.punctualityRate
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.tvComment
import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.vehicleRate
import kotlinx.android.synthetic.main.view_rate_field.rate_bar


class RatingDetailDialogFragment : BaseBottomSheetDialogFragment(), RatingDetailView {

	override val layout: Int = R.layout.dialog_fragment_rating_detail

	private val offerId: Long
		get() = arguments?.getLong(OFFER_ID) ?: 0L

	private val vehicleRating: Float
		get() = arguments?.getFloat(VEHICLE_RATING) ?: 0F

	private val driverRating: Float
		get() = arguments?.getFloat(DRIVER_RATING) ?: 0F

	private val punctualityRating: Float
		get() = arguments?.getFloat(PUNCTUALITY_RATING) ?: 0F

	private val currentComment: String
		get() = arguments?.getString(CURRENT_COMMENT) ?: ""

	private var ratingListener: OnRatingChangeListener? = null

	private val commonRateListener = BaseRatingBar.OnRatingChangeListener { baseRatingBar, fl ->
		presenter.onCommonRatingChanged(fl)
	}

	private val vehicleRateListener = BaseRatingBar.OnRatingChangeListener { baseRatingBar, fl ->
		presenter.onSecondaryRatingsChanged(fl, driverRate.rate_bar.rating, punctualityRate.rate_bar.rating)
	}

	private val driverRateListener = BaseRatingBar.OnRatingChangeListener { baseRatingBar, fl ->
		presenter.onSecondaryRatingsChanged(fl, vehicleRate.rate_bar.rating, punctualityRate.rate_bar.rating)
	}

	private val punctualityRateListener = BaseRatingBar.OnRatingChangeListener { baseRatingBar, fl ->
		presenter.onSecondaryRatingsChanged(fl, driverRate.rate_bar.rating, vehicleRate.rate_bar.rating)
	}

	@InjectPresenter
	lateinit var presenter: RatingDetailPresenter

	@ProvidePresenter
	fun providePresenter() = RatingDetailPresenter()

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		ratingListener = (activity as? OnRatingChangeListener)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		presenter.offerId = offerId
		presenter.vehicleRating = vehicleRating
		presenter.driverRating = driverRating
		presenter.punctualityRating = punctualityRating
		presenter.hintComment = getString(R.string.LNG_PAYMENT_YOUR_COMMENT)
		presenter.currentComment = currentComment
	}

	override fun initUx(savedInstanceState: Bundle?) {
		super.initUx(savedInstanceState)
		btnSend.setOnClickListener {
			presenter.onClickSend(createListOfDetailedRates(), tvComment.text.toString(), commonRate.rating)
		}
		ivClose.setOnClickListener { dismiss() }
		tvComment.setOnClickListener { presenter.onClickComment(tvComment.text.toString()) }
		commonRate.setOnRatingChangeListener(commonRateListener)
		vehicleRate.rate_bar.setOnRatingChangeListener(vehicleRateListener)
		driverRate.rate_bar.setOnRatingChangeListener(driverRateListener)
		punctualityRate.rate_bar.setOnRatingChangeListener(punctualityRateListener)

	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK && requestCode == TextEditorActivity.REQUEST_CODE) {
			data?.let {
				presenter.commentChanged(it.getStringExtra(TextEditorActivity.CORRECTED_TEXT).orEmpty())
			}
		}
	}

	override fun showProgress(isShow: Boolean) {
		btnSend.show(!isShow, false)
		progressBar.show(isShow)
	}

	override fun setRatingCommon(rating: Float) {
		commonRate.setOnRatingChangeListener(null)
		commonRate.rating = rating
		commonRate.setOnRatingChangeListener(commonRateListener)
	}

	override fun setRatingVehicle(rating: Float) {
		vehicleRate.rate_bar.setOnRatingChangeListener(null)
		vehicleRate.rate_bar.rating = rating
		vehicleRate.rate_bar.setOnRatingChangeListener(vehicleRateListener)
	}

	override fun setRatingDriver(rating: Float) {
		driverRate.rate_bar.setOnRatingChangeListener(null)
		driverRate.rate_bar.rating = rating
		driverRate.rate_bar.setOnRatingChangeListener(driverRateListener)
	}

	override fun setRatingPunctuality(rating: Float) {
		punctualityRate.rate_bar.setOnRatingChangeListener(null)
		punctualityRate.rate_bar.rating = rating
		punctualityRate.rate_bar.setOnRatingChangeListener(punctualityRateListener)
	}

	override fun blockInterface(block: Boolean, useSpinner: Boolean) {
		commonRate.setIsIndicator(!block)
		vehicleRate.rate_bar.setIsIndicator(!block)
		driverRate.rate_bar.setIsIndicator(!block)
		punctualityRate.rate_bar.setIsIndicator(!block)
		tvComment.isEnabled = !block
	}

	override fun showComment(comment: String) {
		tvComment.text = comment
	}

	override fun showCommentEditor(comment: String) {
		Intent(context, TextEditorActivity::class.java).apply {
			putExtra(TextEditorActivity.TEXT_FOR_CORRECTING, comment)
			startActivityForResult(this, TextEditorActivity.REQUEST_CODE)
		}
	}

	override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
		context?.let {
			val errMessage = getString(errId, *args)
			Utils.showError(it, finish, errMessage)
		}
	}

	override fun setError(e: ApiException) {
		context?.let {
			if (e.code != ApiException.NETWORK_ERROR) Utils.showError(it, false, getString(R.string.LNG_ERROR) + ": " + e.message)
		}
	}

	override fun setError(e: DatabaseException) {
		//empty
	}

	override fun exitAndReportSuccess(list: List<ReviewRateModel>, comment: String) {
		ratingListener?.onRatingChanged(list, comment)
		dismiss()
	}

	private fun createListOfDetailedRates() = listOf(
		ReviewRateModel(ReviewRate.RateType.DRIVER, driverRate.rate_bar.rating.toInt()),
		ReviewRateModel(ReviewRate.RateType.PUNCTUALITY, punctualityRate.rate_bar.rating.toInt()),
		ReviewRateModel(ReviewRate.RateType.VEHICLE, vehicleRate.rate_bar.rating.toInt())
	)

	companion object {
		private const val OFFER_ID = "offer id"
		private const val VEHICLE_RATING = "vehicle rating"
		private const val DRIVER_RATING = "driver rating"
		private const val PUNCTUALITY_RATING = "punctuality rating"
		private const val CURRENT_COMMENT = "current comment"

		fun newInstance(
				vehicle: Float,
				driver: Float,
				punctuality: Float,
				transferId: Long,
				feedback: String
		) = RatingDetailDialogFragment().apply {
			arguments = Bundle().apply {
				putLong(OFFER_ID, transferId)
				putFloat(VEHICLE_RATING, vehicle)
				putFloat(DRIVER_RATING, driver)
				putFloat(PUNCTUALITY_RATING, punctuality)
				putString(CURRENT_COMMENT, feedback)
			}
		}
	}

	interface OnRatingChangeListener {
		fun onRatingChanged(list: List<ReviewRateModel>, comment: String)
	}

}