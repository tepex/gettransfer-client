package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout

import android.util.AttributeSet
import android.view.LayoutInflater

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_trip_addresses_layout.view.*

class TripAddressesInfoView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(R.layout.view_trip_addresses_layout, this, true)

    fun setInfo(item: CarrierTripBaseModel) {
        tvTripFrom.text = item.from
        if(item.to.isNullOrEmpty()) {
            tv_duration.text = HourlyValuesHelper.getValue(item.duration ?: 0, context)
            changeViewForHourlyTransfer(true)
        } else if (item.duration != null) {
            tv_duration.text = HourlyValuesHelper.getValue(item.duration, context)
            changeViewForHourlyTransfer(true)
        }
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