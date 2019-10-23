package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.presenter.RatingLastTripPresenter

import com.kg.gettransfer.presentation.ui.dialogs.BaseMapBottomSheetDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RatingLastTripView

import kotlinx.android.synthetic.main.view_last_trip_rate.*

@Suppress("TooManyFunctions")
class RatingLastTripFragment : BaseMapBottomSheetDialogFragment(), RatingLastTripView {

    override val layout: Int = R.layout.view_last_trip_rate

    @InjectPresenter
    internal lateinit var presenter: RatingLastTripPresenter

    @ProvidePresenter
    fun providePresenter() = RatingLastTripPresenter()

    override fun getPresenter(): RatingLastTripPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = arguments?.getLong(EXTRA_TRANSFER_ID) ?: 0L
    }

    @CallSuper
    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tv_transfer_details.setOnClickListener { presenter.onTransferDetailsClick() }
        ivClose.setOnClickListener { presenter.onReviewCanceled() }
        rate_bar_last_trip.setOnRatingBarChangeListener { _, rating, _ ->
            presenter.onRateClicked(rating)
        }
    }

    @CallSuper
    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        initMapFragment(R.id.rate_map)
    }

    override fun setupReviewForLastTrip(transfer: Transfer) {
        tv_transfer_number_rate.apply { text = text.toString().plus(" #${transfer.id}") }
        tv_transfer_date_rate.text = SystemUtils.formatDateTime(transfer.dateToLocal)
        tv_vehicle_model_rate.text = arguments?.getString(EXTRA_VEHICLE)
        val color = arguments?.getString(EXTRA_COLOR) ?: ""
        context?.let { carColor_rate.setImageDrawable(Utils.getCarColorFormRes(it, color)) }
    }

    override fun cancelReview() {
        dismiss()
    }

    override fun thanksForRate() {
        val parent = activity
        if (parent is MainNavigateActivity) {
            parent.thanksForRate()
        }
    }

    override fun showDetailedReview() {
        if (fragmentManager?.fragments?.firstOrNull { it.tag == RatingDetailDialogFragment.RATE_DIALOG_TAG } == null) {
            RatingDetailDialogFragment
                .newInstance()
                .show(requireFragmentManager(), RatingDetailDialogFragment.RATE_DIALOG_TAG)
        }
    }

    override fun setTransferNotFoundError(transferId: Long) {
        val act = activity
        if (act is BaseView) {
            act.setTransferNotFoundError(transferId)
        }
    }

    companion object {
        const val RATING_LAST_TRIP_TAG = "rating_last_trip_tag"
        private const val EXTRA_TRANSFER_ID = "transfer_id"
        private const val EXTRA_VEHICLE = "vehicle"
        private const val EXTRA_COLOR = "color"

        fun newInstance(transferId: Long, vehicle: String, color: String) = RatingLastTripFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_TRANSFER_ID, transferId)
                putString(EXTRA_VEHICLE, vehicle)
                putString(EXTRA_COLOR, color)
            }
        }
    }
}
