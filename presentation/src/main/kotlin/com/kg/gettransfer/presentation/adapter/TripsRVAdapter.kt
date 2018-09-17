package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.presentation.ui.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_trips_info.view.*
import java.text.SimpleDateFormat
import java.util.*

class TripsRVAdapter(private var trips: List<CarrierTrip>, private var distanceUnit: DistanceUnit):
        RecyclerView.Adapter<TripsRVAdapter.ViewHolder>() {

    companion object {
        private val DATE_TIME_FULL_FORMAT = SimpleDateFormat(Utils.DATE_TIME_FULL_PATTERN, Locale.US)
        private val DATE_TIME_FORMAT = SimpleDateFormat(Utils.DATE_TIME_PATTERN, Locale.US)
    }

    override fun getItemCount(): Int = trips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TripsRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_trips_info, parent, false))

    override fun onBindViewHolder(holder: TripsRVAdapter.ViewHolder, pos: Int) {
        holder.bind(trips.get(pos), distanceUnit)
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: CarrierTrip, distanceUnit: DistanceUnit) = with(containerView) {
            tvTransferRequestNumber.text = context.getString(R.string.transfer_request_num, item.transferId)
            tvFrom.text = item.from.name
            tvTo.text = item.to.name
            tvOrderDateTime.text = changeDateFormat(item.dateLocal)
            tvDistance.text = Utils.formatDistance(context, R.string.distance, distanceUnit, item.distance)
            tvPrice.text = item.price
            tvVehicle.text = item.vehicle.name
            if(item.childSeats == 0) ivChildSeat.visibility = View.INVISIBLE
            if(item.comment == null || item.comment == "") ivComment.visibility = View.INVISIBLE
        }

        fun changeDateFormat(dateTime: String): String {
            val newDate = DATE_TIME_FULL_FORMAT.parse(dateTime)
            return DATE_TIME_FORMAT.format(newDate)
        }
    }
}