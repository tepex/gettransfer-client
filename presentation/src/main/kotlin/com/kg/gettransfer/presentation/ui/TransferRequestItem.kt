package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.support.annotation.LayoutRes

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat

import android.util.AttributeSet
import android.view.LayoutInflater

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info_enabled.view.*

import java.util.Calendar

class TransferRequestItem @JvmOverloads constructor(
        context: Context,
        @LayoutRes layout: Int,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(layout, this, true)

    fun setInfo(item: TransferModel) {
        tvTransferRequestNumber.text = context.getString(R.string.LNG_RIDE_NUMBER).plus(item.id)
        tvTransferRequestStatus.text = when (item.status) {
            Transfer.Status.NEW -> {
                if (item.offersCount > 0 && !item.isBookNow()) context.getString(R.string.LNG_BOOK_OFFER)
                else context.getString(R.string.LNG_WAIT_FOR_OFFERS)
            }
            Transfer.Status.PERFORMED -> {
                if (item.dateTime.after(Calendar.getInstance().time)) context.getString(R.string.LNG_TRANSFER_WILL_START)
                        .plus("")
                        .plus(Utils.durationToString(context, Utils.convertDuration(item.timeToTransfer)))
                else context.getString(R.string.LNG_TRANSFER_IN_PROGRESS)
            }
            else -> item.statusName?.let { context.getString(R.string.LNG_TRANSFER_WAS)
                    .plus(" ")
                    .plus(context.getString(item.statusName).toLowerCase()) }
        }
        tvTransferRequestStatus.setTextColor(ContextCompat.getColor(context,
                when (item.status) {
                    Transfer.Status.OUTDATED  -> R.color.color_transfer_details_text_red
                    Transfer.Status.PERFORMED -> R.color.color_gtr_green
                    else                      -> R.color.colorTransferRequestText
                }))
        tvFrom.text = item.from
        if (item.to != null) {
            tvTo.text = item.to
            tvDistance.text = SystemUtils.formatDistance(context, item.distance, true)
            changeViewForHourlyTransfer(false)
            item.dateTimeReturn?.let {
                ivReturnIcon.isVisible = true
                ivMarkersLine.isVisible = false
            }
        } else if (item.duration != null) {
            changeViewForHourlyTransfer(true)
            tv_duration.text = HourlyValuesHelper.getValue(item.duration, context)
        }
        tvOrderDateTime.text = SystemUtils.formatDateTime(item.dateTime)

        (item.dateTimeReturn == null).also {
            ivReturnIcon.isVisible = !it
            ivMarkersLine.isVisible = it
        }
    }

    private fun changeViewForHourlyTransfer(isHourlyTransfer: Boolean) {
        rl_hourly_info.isVisible = isHourlyTransfer
        tvMarkerTo.isVisible = !isHourlyTransfer
        if (isHourlyTransfer) {
            tvTo.text = ""
            tvDistance.text = ""
        }
    }
}