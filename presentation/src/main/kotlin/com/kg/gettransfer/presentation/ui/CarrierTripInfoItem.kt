package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_carrier_trip_info_layout.view.*

class CarrierTripInfoItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(R.layout.view_carrier_trip_info_layout, this, true)

    fun setInfo(item: CarrierTripModel, isCarrierTripDetailsActivity: Boolean) {
        val transferStatus = when(item.tripStatus) {
            CarrierTripModel.FUTURE_TRIP -> context.getString(R.string.LNG_WILL_START_IN).plus(" ")
                    .plus(Utils.durationToString(context, Utils.convertDuration(item.timeToTransfer)))
            CarrierTripModel.IN_PROGRESS_TRIP -> context.getString(R.string.LNG_IN_PROGRESS)
            CarrierTripModel.PAST_TRIP -> context.getString(R.string.LNG_RIDE_STATUS_CANCELED)
            else -> ""
        }
        setColorStyle(item.tripStatus)

        transferId.text = context.getString(R.string.LNG_TRANSFER).plus(" #${item.transferId}").plus(" ${transferStatus?: ""}")
        tripDateTime.text = item.dateTimeString
        tvTripFrom.text = item.from

        if(item.to != null) {
            if(isCarrierTripDetailsActivity){
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
        } else if(item.duration != null){
            if(isCarrierTripDetailsActivity){
                setDuration(bsTvDuration, item.duration, true)
                bsLayoutDuration.isVisible = true
            } else {
                setPrice(tripPrice, item.price)
                setDuration(tripDistance, item.duration, true)
            }
            setDuration(tv_duration, item.duration, true)
            changeViewForHourlyTransfer(true)
        }

        if(isCarrierTripDetailsActivity){
            setPrice(bsTvPrice, item.price, bsTvTotalPrice, item.remainsToPay, item.paidPercentage)
            tripDistance.isVisible = false
            tripPrice.isVisible = false
            layoutDistanceDurationPrice.isVisible = true
        } else {
            setPrice(tripPrice, item.price)
            vehicleName.text = item.vehicleName
            vehicleNumber.text = item.vehicleNumber
            imgChildSeats.isVisible = item.countChild > 0
            imgComment.isVisible = item.comment != null
            layoutVehicleInfo.isVisible = true
        }
    }

    private fun setDistance(textView: TextView, distance: Int?){
        textView.text = SystemUtils.formatDistance(context, distance, false)
    }

    private fun setDuration(textView: TextView, duration: Int?, isHourlyTransfer: Boolean){
        textView.text = when (isHourlyTransfer){
            true -> HourlyValuesHelper.getValue(duration!!, context)
            false -> Utils.durationToString(context, Utils.convertDuration(duration?: 0))
        }
    }

    private fun setPrice(textViewPrice: TextView, price: String, textViewTotalPrice: TextView? = null, totalPrice: String? = null, percentage: Int? = null){
        textViewPrice.text = price
        totalPrice?.let {
            textViewTotalPrice!!.text = context.getString(R.string.LNG_TOTAL_PRICE).toUpperCase()
                    .plus(": $it \n")
                    .plus("(${100 - percentage!!}% ")
                    .plus(context.getString(R.string.LNG_RIDE_NOT_PAID))
                    .plus(")")
        }
    }

    private fun setColorStyle(tripStatus: String){
        when(tripStatus){
            CarrierTripModel.PAST_TRIP        -> setStyleCanceled()
            CarrierTripModel.IN_PROGRESS_TRIP -> setStyleActive()
            CarrierTripModel.FUTURE_TRIP      -> setStyleActive()
        }
    }

    private fun setStyleCanceled(){
        transferId.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDateTime.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDateTime.background = null
        tripPrice.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
        tripDistance.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_gray))
    }

    private fun setStyleActive(){
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