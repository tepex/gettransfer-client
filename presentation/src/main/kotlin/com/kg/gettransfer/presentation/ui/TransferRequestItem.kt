package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import androidx.core.view.isVisible

import com.kg.gettransfer.presentation.adapter.BtnCallClickListener
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.RequestsView

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*
import java.util.Date

class TransferRequestItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_transfer_request_info, this, true)

    fun setStyle(requestType: Int) {
        when (requestType) {
            RequestsView.TransferTypeAnnotation.TRANSFER_ACTIVE -> {
                tvTransferRequestNumber.setTextColor(ContextCompat.getColor(context, R.color.colorTransferRequestText))
                ContextCompat.getColor(context, R.color.color_gtr_green).run {
                    tvOrderDateTime.setTextColor(this)
                    tvDistance.setTextColor(this)
                }
                ContextCompat.getDrawable(context, R.drawable.back_circle_marker_filled_orange).run {
                    tvMarkerFrom.background = this
                    tvMarkerTo.background = this
                }
                ivReturnIcon.setImageResource(R.drawable.ic_roundtrip_arrows)
                ivMarkersLine.setImageResource(R.drawable.ic_markers_line_orange)
                tv_duration.background = ContextCompat.getDrawable(context, R.drawable.back_hours_info_orange)
                ivHourlyPoint.setImageResource(R.drawable.ic_hourly_orange)
                tvEventsCount.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_red)
            }
            RequestsView.TransferTypeAnnotation.TRANSFER_ARCHIVE -> {
                ContextCompat.getColor(context, R.color.color_grey_48).run {
                    tvTransferRequestNumber.setTextColor(this)
                    tvOrderDateTime.setTextColor(this)
                    tvDistance.setTextColor(this)
                }
                ContextCompat.getDrawable(context, R.drawable.back_circle_marker_filled_grey).run {
                    tvMarkerFrom.background = this
                    tvMarkerTo.background = this
                }
                ivReturnIcon.setImageResource(R.drawable.ic_roundtrip_arrows_grey)
                ivMarkersLine.setImageResource(R.drawable.ic_markers_line_grey)
                tv_duration.background = ContextCompat.getDrawable(context, R.drawable.back_hours_info_grey)
                ivHourlyPoint.setImageResource(R.drawable.ic_hourly_grey)
                tvEventsCount.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_grey)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    @Suppress("ComplexMethod", "LongMethod")
    fun setInfo(item: TransferModel, requestType: Int) {
        tvTransferRequestNumber.text = context.getString(R.string.LNG_RIDE_NUMBER).plus(item.id)
        tvTransferRequestStatus.text = when (item.status) {
            Transfer.Status.NEW -> when {
                item.isBookNow() ||
                item.isPaymentInProgress()      -> getMatchedTransferStatusText(item.timeToTransfer)
                item.offersCount > 0 ||
                item.bookNowOffers.isNotEmpty() -> context.getString(R.string.LNG_BOOK_OFFER)
                else                            -> context.getString(R.string.LNG_WAIT_FOR_OFFERS)
            }
            Transfer.Status.PERFORMED ->
                if (item.dateTimeTZ.after(Date())) {
                    getMatchedTransferStatusText(item.timeToTransfer)
                } else {
                    context.getString(R.string.LNG_TRANSFER_IN_PROGRESS)
                }
            else -> item.statusName?.run { context.getString(R.string.LNG_TRANSFER_WAS)
                .plus(" ")
                .plus(context.getString(item.statusName).toLowerCase())
            }
        }
        tvTransferRequestStatus.setTextColor(ContextCompat.getColor(
            context,
            when (item.status) {
                Transfer.Status.OUTDATED  -> R.color.color_gtr_red
                Transfer.Status.PERFORMED -> R.color.color_gtr_green
                Transfer.Status.COMPLETED -> R.color.colorTextBlack
                Transfer.Status.NEW       ->
                    if (item.isBookNow() || item.isPaymentInProgress()) {
                        R.color.color_gtr_green
                    } else {
                        R.color.colorTransferRequestText
                    }
                else                      -> R.color.colorTransferRequestText
            }))
        ContextCompat.getColor(
            context,
            if (requestType == RequestsView.TransferTypeAnnotation.TRANSFER_ARCHIVE &&
                item.status != Transfer.Status.COMPLETED) {
                R.color.color_grey_48
            } else {
                R.color.colorTextBlack
            }
        ).run {
            tvFrom.setTextColor(this)
            tvTo.setTextColor(this)
        }

        tvFrom.text = item.from
        tvOrderDateTime.text = SystemUtils.formatDateTime(item.dateTime)

        if (item.to != null) {
            tvTo.text = item.to
            tvDistance.text =
                SystemUtils.formatDistance(context, item.distance, false, false)
        } else {
            tvTo.text = ""
            tvDistance.text = ""
        }

        if (item.duration != null) {
            tv_duration.text = HourlyValuesHelper.getValue(item.duration, context)
        }
        changeViewForHourlyTransfer(item.duration != null)

        (item.dateTimeReturn == null).also { visible ->
            ivReturnIcon.isVisible = !visible
            ivMarkersLine.isVisible = visible
        }
    }

    private fun getMatchedTransferStatusText(timeToTransfer: Int) =
        context.getString(R.string.LNG_TRANSFER_WILL_START)
            .plus("")
            .plus(Utils.durationToString(context, Utils.convertDuration(timeToTransfer)))

    private fun changeViewForHourlyTransfer(isHourlyTransfer: Boolean) {
        rl_hourly_info.isVisible = isHourlyTransfer
        tvMarkerTo.isVisible = !isHourlyTransfer
    }

    fun showEvents(item: TransferModel, offerInfoShowed: Boolean, eventsCount: Int) {
        @Suppress("ComplexCondition")
        if (eventsCount == 0 ||
            item.isPaymentInProgress()
        ) {
            tvEventsCount.isVisible = false
            btnChat.setCounter(0)
        } else {
            if (offerInfoShowed) {
                tvEventsCount.isVisible = false
                btnChat.setCounter(eventsCount)
            } else {
                tvEventsCount.isVisible = true
                tvEventsCount.text = eventsCount.toString()
            }
        }
    }

    fun showOfferInfo(
        offer: Offer?,
        haveDriverCoordinates: Boolean,
        onCallClick: BtnCallClickListener,
        onChatClick: OnClickListener,
        onDriverCoordinatesClick: OnClickListener
    ) {
        if (offer == null) {
            offerInfo.isVisible = false
            return
        }
        offerInfo.isVisible = true
        with(offer.vehicle) {
            carNumber.text = registrationNumber ?: ""
            color?.let { Utils.setCarColorInTextView(context, carColor, it) }
            carName.text = model
        }
        btnCall.isVisible = offer.phoneToCall != null
        offer.phoneToCall?.let { phone -> btnCall.setOnClickListener { onCallClick(phone) } }
        with(btnDriverCoordinates) {
            isVisible = haveDriverCoordinates
            if (haveDriverCoordinates) {
                setOnClickListener(onDriverCoordinatesClick)
            }
        }
        btnChat.setOnClickListener(onChatClick)
    }
}
