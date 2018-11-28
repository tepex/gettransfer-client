package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info.*

class RequestsRVAdapter(
    private val transfers: List<TransferModel>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RequestsRVAdapter.ViewHolder>() {

    override fun getItemCount() = transfers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RequestsRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_request_info, parent, false))

    override fun onBindViewHolder(holder: RequestsRVAdapter.ViewHolder, pos: Int) =
        holder.bind(transfers.get(pos), listener)

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: TransferModel, listener: ItemClickListener) = with(containerView) {
            tvTransferRequestNumber.text = context.getString(R.string.LNG_RIDE_NUMBER).plus(item.id)
            tvFrom.text = item.from
            if (item.to != null) {
                tvTo.text = item.to
                tvDistance.text = Utils.formatDistance(context, item.distance, item.distanceUnit, true)
            } else if(item.duration != null) {
                rl_hourly_info.isVisible = true
                tvMarkerTo.isVisible = false
                tv_duration.text = HourlyValuesHelper.getValue(item.duration, context)
            }
            //tvOrderDateTime.text = context.getString(R.string.transfer_date_local, item.dateTime)
            tvOrderDateTime.text = Utils.getFormattedDate(item.locale, item.dateTime)
            setOnClickListener { listener(item) }
        }
    }
}

typealias ItemClickListener = (TransferModel) -> Unit
