package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat

import android.util.AttributeSet
import android.view.LayoutInflater

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_carrier_trip_info_layout.view.*

class CarrierTripInfoItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(R.layout.view_carrier_trip_info_layout, this, true)

    fun setInfo(item: CarrierTripBaseModel) {
        val transferStatus = when (item.tripStatus) {
            CarrierTripBaseModel.FUTURE_TRIP -> context.getString(R.string.LNG_WILL_START_IN).plus(" ")
                    .plus(Utils.durationToString(context, Utils.convertDuration(item.timeToTransfer)))
            CarrierTripBaseModel.IN_PROGRESS_TRIP -> context.getString(R.string.LNG_IN_PROGRESS)
            CarrierTripBaseModel.PAST_TRIP -> context.getString(R.string.LNG_RIDE_STATUS_COMPLETED)
            else -> ""
        }
        setColorStyle(item.tripStatus)

        textDateTime.text = SystemUtils.formatDateTimeWithShortMonth(item.dateLocal)
        textTransferStatus.text = context.getString(R.string.LNG_TRANSFER).plus(" #${item.transferId}").plus(" ${transferStatus?: ""}")
        tvTripFrom.text = item.from

        if (item.to != null) {
            item.distance?.let {
                textDistance.text = SystemUtils.formatDistance(context, it, false)
                textDistance.isVisible = true
            }
            tvTripTo.text = item.to
            changeViewForHourlyTransfer(false)
        } else if (item.duration != null) {
            tv_duration.text = HourlyValuesHelper.getValue(item.duration, context)
            changeViewForHourlyTransfer(true)
        }

        vehiceName.text = item.vehicle.name
        vehiceNumber.text = item.vehicle.registrationNumber
        textCountChild.text = context.getString(R.string.X_SIGN).plus(" ${item.countChild}")
        (item.countChild > 0).let {
            imgCountChild.isVisible = it
            textCountChild.isVisible = it
        }
        imgComment.isVisible = item.comment != null
    }

    private fun setColorStyle(tripStatus: String) {
        when (tripStatus) {
            CarrierTripBaseModel.PAST_TRIP        -> setStyleCanceled()
            CarrierTripBaseModel.IN_PROGRESS_TRIP -> setStyleActive()
            CarrierTripBaseModel.FUTURE_TRIP      -> setStyleActive()
        }
    }

    private fun setStyleActive() {
        ContextCompat.getColor(context, R.color.colorTextBlack).let {
            textDateTime.setTextColor(it)
            textDistance.setTextColor(it)
            tvTripFrom.setTextColor(it)
            tvTripTo.setTextColor(it)
            vehiceNumber.setTextColor(it)
            imgCountChild.setColorFilter(it)
            textCountChild.setTextColor(it)
            imgComment.setColorFilter(it)
        }
        ivMarkersLine.setColorFilter(ContextCompat.getColor(context, R.color.color_gtr_orange))
        textTransferStatus.setTextColor(ContextCompat.getColor(context, R.color.color_gtr_green))
        tvMarkerFrom.isEnabled = true
        tvMarkerTo.isEnabled = true
        tv_duration.isEnabled = true
        ivHourlyPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_hourly_driver_vector))
    }

    private fun setStyleCanceled() {
        ContextCompat.getColor(context, R.color.color_gtr_grey).let {
            textDateTime.setTextColor(it)
            textDistance.setTextColor(it)
            textTransferStatus.setTextColor(it)
            tvTripFrom.setTextColor(it)
            tvTripTo.setTextColor(it)
            vehiceNumber.setTextColor(it)
            textCountChild.setTextColor(it)
        }
        ContextCompat.getColor(context, R.color.color_gtr_light_grey).let {
            ivMarkersLine.setColorFilter(it)
            imgCountChild.setColorFilter(it)
            imgComment.setColorFilter(it)
        }
        tvMarkerFrom.isEnabled = false
        tvMarkerTo.isEnabled = false
        tv_duration.isEnabled = false
        ivHourlyPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_hourly_driver_vector_gray))
    }

    private fun changeViewForHourlyTransfer(isHourlyTransfer: Boolean) {
        rl_hourly_info.isVisible = isHourlyTransfer
        tvMarkerTo.isVisible = !isHourlyTransfer
        ivMarkersLine.isVisible = !isHourlyTransfer
        if (isHourlyTransfer) {
            tvTripTo.text = ""
        }
    }
}