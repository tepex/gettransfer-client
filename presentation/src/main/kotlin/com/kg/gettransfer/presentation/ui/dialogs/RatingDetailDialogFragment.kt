package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import android.widget.RatingBar

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.model.ReviewRate

import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.extensions.setUneditable

import com.kg.gettransfer.presentation.presenter.RatingDetailPresenter
import com.kg.gettransfer.presentation.ui.RateTripAnimationFragment

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment.Companion.COMMENT_DIALOG_TAG
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment.Companion.COMMENT_REQUEST_CODE

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RatingDetailView

import kotlinx.android.synthetic.main.dialog_fragment_rating_detail.*
import kotlinx.android.synthetic.main.view_rate_field.*

@Suppress("TooManyFunctions")
class RatingDetailDialogFragment : BaseBottomSheetDialogFragment(), RatingDetailView {

    override val layout: Int = R.layout.dialog_fragment_rating_detail
    private var ratingListener: OnRatingChangeListener? = null
    private var isExitWithResult = false

    private val rateAnimation by lazy { RateTripAnimationFragment() }

    private val commonRateListener = RatingBar.OnRatingBarChangeListener { _, rate, _ ->
        presenter.onCommonRatingChanged(rate)
    }
    private val vehicleRateListener = RatingBar.OnRatingBarChangeListener { _, rate, _ ->
        presenter.vehicleRating = rate
        presenter.ratingChanged()
    }
    private val driverRateListener = RatingBar.OnRatingBarChangeListener { _, rate, _ ->
        presenter.driverRating = rate
        presenter.ratingChanged()
    }
    private val punctualityRateListener = RatingBar.OnRatingBarChangeListener { _, rate, _ ->
        presenter.communicationRating = rate
        presenter.ratingChanged()
    }

    @InjectPresenter
    lateinit var presenter: RatingDetailPresenter

    @ProvidePresenter
    fun providePresenter() = RatingDetailPresenter()

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ratingListener = activity as? OnRatingChangeListener
    }

    @CallSuper
    override fun onDetach() {
        ratingListener = null
        super.onDetach()
    }

    @CallSuper
    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        btnSend.setOnClickListener { presenter.onClickSend() }
        ivClose.setOnClickListener { dismiss() }
        etComment.setThrottledClickListener { presenter.onClickComment(etComment.text.toString().trim()) }
        etComment.setUneditable()
        commonRate.onRatingBarChangeListener = commonRateListener
        vehicleRate.rate_bar.onRatingBarChangeListener = vehicleRateListener
        driverRate.rate_bar.onRatingBarChangeListener = driverRateListener
        punctualityRate.rate_bar.onRatingBarChangeListener = punctualityRateListener
    }

    private fun showRateAnimation() {
        if (!rateAnimation.isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                replace(android.R.id.content, rateAnimation)
                commit()
            }
        }
    }

    override fun setRatingCommon(rating: Float) {
        commonRate.onRatingBarChangeListener = null
        commonRate.rating = rating
        commonRate.onRatingBarChangeListener = commonRateListener
    }

    override fun setRatingDriver(rating: Float) {
        driverRate.rate_bar.onRatingBarChangeListener = null
        driverRate.rate_bar.rating = rating
        driverRate.rate_bar.onRatingBarChangeListener = driverRateListener
        driverRate.isVisible = true
    }

    override fun setRatingPunctuality(rating: Float) {
        punctualityRate.rate_bar.onRatingBarChangeListener = null
        punctualityRate.rate_bar.rating = rating
        punctualityRate.rate_bar.onRatingBarChangeListener = punctualityRateListener
        punctualityRate.isVisible = true
    }

    override fun setRatingVehicle(rating: Float) {
        vehicleRate.rate_bar.onRatingBarChangeListener = null
        vehicleRate.rate_bar.rating = rating
        vehicleRate.rate_bar.onRatingBarChangeListener = vehicleRateListener
        vehicleRate.isVisible = true
    }

    override fun setDividersVisibility() {
        driverRate.divider.isVisible = punctualityRate.isVisible || vehicleRate.isVisible
        punctualityRate.divider.isVisible = driverRate.isVisible
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        commonRate.setIsIndicator(!block)
        vehicleRate.rate_bar.setIsIndicator(!block)
        driverRate.rate_bar.setIsIndicator(!block)
        punctualityRate.rate_bar.setIsIndicator(!block)
        etComment.isEnabled = !block
    }

    override fun showComment(comment: String) {
        etComment.setText(comment)
    }

    override fun showCommentDialog(comment: String) {
        val commentDialog = CommentDialogFragment.newInstance(comment)
        commentDialog.setTargetFragment(this@RatingDetailDialogFragment, COMMENT_REQUEST_CODE)
        commentDialog.show(requireFragmentManager(), COMMENT_DIALOG_TAG)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == COMMENT_REQUEST_CODE) {
            val comment = data?.getStringExtra(CommentDialogFragment.EXTRA_COMMENT) ?: ""
            presenter.comment = comment
            setComment(comment)
        }
    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        context?.let { Utils.showError(it, finish, getString(errId, *args)) }
    }

    override fun setError(e: ApiException) {
        if (e.code != ApiException.NETWORK_ERROR) {
            context?.let { Utils.showError(it, false, getString(R.string.LNG_ERROR) + ": " + e.message) }
        }
    }

    override fun setError(e: DatabaseException) {}

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        val act = activity
        if (act is BaseView) {
            act.setTransferNotFoundError(transferId, dismissCallBack)
        }
    }

    override fun exitAndReportSuccess(list: List<ReviewRate>, comment: String) {
        ratingListener?.onRatingChanged(list, comment)
        isExitWithResult = true
        dismiss()
    }

    @CallSuper
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isExitWithResult) {
            ratingListener?.onRatingChangeCancelled()
            showRateAnimation()
        }
    }

    private fun setComment(comment: String) = etComment.setText(comment)

    interface OnRatingChangeListener {
        fun onRatingChanged(list: List<ReviewRate>, comment: String)
        fun onRatingChangeCancelled()
    }

    companion object {
        const val RATE_DIALOG_TAG = "rate_dialog_tag"

        fun newInstance() = RatingDetailDialogFragment()
    }
}
