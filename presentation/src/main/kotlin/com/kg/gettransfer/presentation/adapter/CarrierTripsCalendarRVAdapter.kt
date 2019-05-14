package com.kg.gettransfer.presentation.adapter

import com.kg.gettransfer.R
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.kg.gettransfer.presentation.presenter.CarrierTripsCalendarPresenter
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_carrier_trips_calendar_rv_item.view.*
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

class CarrierTripsCalendarRVAdapter(
        private val presenter: CarrierTripsCalendarPresenter,
        private val carrierTrips: List<CarrierTripBaseModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemCount() = carrierTrips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_calendar_rv_item, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(carrierTrips[position]){ tripId, transferId -> presenter.onTripSelected(tripId, transferId) }
    }

    class ViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(item: CarrierTripBaseModel, listener: ClickOnCarrierTripHandler) = with(containerView) {
            if (item.duration != null) {
                layoutHourlyDuration.isVisible = true
                textDuration.text = HourlyValuesHelper.getValue(item.duration, context)
            } else {
                textDistance.isVisible = true
                textDistance.text = SystemUtils.formatDistance(context, item.distance, false)
            }
            textFromPointName.text = item.from
            textTime.text = SystemUtils.formatTime(item.dateLocal)
            textDate.text = context.getString(R.string.text_in_parentheses, SystemUtils.formatDate(item.dateLocal))
            setOnClickListener { listener(item.id, item.transferId) }
        }
    }
}
