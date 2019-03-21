package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.model.TotalPriceModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_carrier_trip_info_layout.view.*

class CarrierTripInfoItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(R.layout.view_carrier_trip_info_layout, this, true)

    fun setInfo(item: CarrierTripBaseModel, totalPrice: TotalPriceModel? = null, showDetails: Boolean = false) {
        val transferStatus = when (item.tripStatus) {
            CarrierTripBaseModel.FUTURE_TRIP -> context.getString(R.string.LNG_WILL_START_IN).plus(" ")
                    .plus(Utils.durationToString(context, Utils.convertDuration(item.timeToTransfer)))
            CarrierTripBaseModel.IN_PROGRESS_TRIP -> context.getString(R.string.LNG_IN_PROGRESS)
            CarrierTripBaseModel.PAST_TRIP -> context.getString(R.string.LNG_RIDE_STATUS_COMPLETED)
            else -> ""
        }
        setColorStyle(item.tripStatus)

        transferId.text = context.getString(R.string.LNG_TRANSFER).plus(" #${item.transferId}").plus(" ${transferStatus?: ""}")
        tripDateTime.text = item.dateTime
        tvTripFrom.text = item.from

        if (item.to != null) {
            if (showDetails) {
                item.distance?.let {
                    setDistance(bsTvDistance, it)
                    bsLayoutDistance.isVisible = true
                }
                item.time?.let {
                    setDuration(bsTvDuration, it, false)
                    bsLayoutDuration.isVisible = true
                }
            } else {
                setDistance(tripDistance, item.distance)
            }
            tvTripTo.text = item.to
            changeViewForHourlyTransfer(false)
        } else if (item.duration != null) {
            if (showDetails) {
                setDuration(bsTvDuration, item.duration, true)
                bsLayoutDuration.isVisible = true
            } else {
                setPrice(tripPrice, item.price)
                setDuration(tripDistance, item.duration, true)
            }
            setDuration(tv_duration, item.duration, true)
            changeViewForHourlyTransfer(true)
        }

        if (showDetails) {
            setPrice(bsTvPrice, item.price, bsTvTotalPrice, totalPrice)
            tripDistance.isVisible = false
            tripPrice.isVisible = false
            layoutDistanceDurationPrice.isVisible = true
        } else {
            setPrice(tripPrice, item.price)
            vehicleName.text = item.vehicle.name
            vehicleNumber.text = item.vehicle.registrationNumber
            imgChildSeats.isVisible = item.countChild > 0
            imgComment.isVisible = item.comment != null
            layoutVehicleInfo.isVisible = true
        }
    }

    private fun setDistance(textView: TextView, distance: Int?) {
        textView.text = SystemUtils.formatDistance(context, distance, false)
    }

    private fun setDuration(textView: TextView, duration: Int?, isHourlyTransfer: Boolean) {
        textView.text = when (isHourlyTransfer) {
            true -> HourlyValuesHelper.getValue(duration!!, context)
            false -> Utils.durationToString(context, Utils.convertDuration(duration?: 0))
        }
    }

    private fun setPrice(textViewPrice: TextView, price: String, textViewTotalPrice: TextView? = null, totalPrice: TotalPriceModel? = null) {
        if (textViewTotalPrice != null){
            var textTotalPrice = context.getString(R.string.LNG_TOTAL_PRICE).toUpperCase().plus(": $price")
            totalPrice?.let {
                textViewPrice.text = it.remainsToPay
                textTotalPrice = textTotalPrice.plus("\n(${100 - it.paidPercentage}% ")
                        .plus(context.getString(R.string.LNG_RIDE_NOT_PAID)).plus(")")
            }
            textViewTotalPrice.text = textTotalPrice
        } else {
            textViewPrice.text = price
        }
    }

    private fun setColorStyle(tripStatus: String) {
        when (tripStatus) {
            CarrierTripBaseModel.PAST_TRIP        -> setStyleCanceled()
            CarrierTripBaseModel.IN_PROGRESS_TRIP -> setStyleActive()
            CarrierTripBaseModel.FUTURE_TRIP      -> setStyleActive()
        }
    }

    private fun setStyleCanceled() {
        transferId.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDateTime.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDateTime.background = null
        tripPrice.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDistance.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
    }

    private fun setStyleActive() {
        transferId.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_green))
        tripDateTime.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        tripDateTime.background = ContextCompat.getDrawable(context, R.drawable.back_carrier_trips_items_date)
        tripPrice.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_green))
        tripDistance.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack))
    }

    private fun changeViewForHourlyTransfer(isHourlyTransfer: Boolean) {
        rl_hourly_info.isVisible = isHourlyTransfer
        tvMarkerTo.isVisible = !isHourlyTransfer
        if (isHourlyTransfer) {
            tvTripTo.text = ""
            //tripDistance.text = ""
        }
    }
}