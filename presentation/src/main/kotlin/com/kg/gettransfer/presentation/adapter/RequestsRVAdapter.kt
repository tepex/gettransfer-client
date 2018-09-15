package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.ui.Utils

import java.text.Format
import java.util.Locale

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info.*

class RequestsRVAdapter(private val transfers: List<Transfer>,
                        private val distanceUnit: DistanceUnit,
                        private val dateFormat: Format):
        RecyclerView.Adapter<RequestsRVAdapter.ViewHolder>() {

	companion object {
		private var selected = RecyclerView.NO_POSITION
	}

    override fun getItemCount(): Int = transfers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RequestsRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_request_info, parent, false))

    override fun onBindViewHolder(holder: RequestsRVAdapter.ViewHolder, pos: Int) {
        holder.bind(transfers.get(pos), distanceUnit, dateFormat)
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: Transfer, distanceUnit: DistanceUnit, dateFormat: Format) = with(containerView) {
            tvTransferRequestNumber.text = context.getString(R.string.transfer_request_num, item.id)
            tvFrom.text = item.from.name
            tvTo.text = item.to!!.name
            tvOrderDateTime.text = context.getString(R.string.transfer_date_local, dateFormat.format(item.dateToLocal))
            tvDistance.text = Utils.formatDistance(context, R.string.distance, item.distance, distanceUnit)
        }
    }
}
