package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter

import com.kg.gettransfer.presentation.ui.SystemUtils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_trips_info.view.*

class TripsRVAdapter(
    private val presenter: CarrierTripsPresenter,
    private var trips: List<CarrierTripBaseModel>
) : RecyclerView.Adapter<TripsRVAdapter.ViewHolder>() {

    override fun getItemCount() = trips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripsRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_trips_info, parent, false))

    override fun onBindViewHolder(holder: TripsRVAdapter.ViewHolder, pos: Int) =
        holder.bind(trips.get(pos)) { presenter.onTripSelected(it) }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: CarrierTripBaseModel, listener: ClickOnCarrierTripHandler) = with (containerView) {
            tvTransferRequestNumber.text = context.getString(R.string.LNG_RIDE_NUMBER).plus(item.transferId)
            tvFrom.text = item.from
            tvTo.text = item.to ?: ""
            //tvOrderDateTime.text = context.getString(R.string.transfer_date_local, item.dateTime)
            tvOrderDateTime.text = item.dateTime
            tvDistance.text = SystemUtils.formatDistance(context, item.distance, true)
            tvPrice.text = item.pay
            tvVehicle.text = item.vehicle.name

            ivChildSeat.isInvisible = item.countChild == 0
            ivComment.isInvisible   = item.comment.isNullOrEmpty()

            setOnClickListener {
                selected = adapterPosition
                listener(item.id)
            }
        }
    }

    companion object {
        private var selected = RecyclerView.NO_POSITION
    }
}

typealias ClickOnCarrierTripHandler = (Long) -> Unit
