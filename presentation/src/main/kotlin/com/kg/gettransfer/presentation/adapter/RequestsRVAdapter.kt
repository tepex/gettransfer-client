package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import java.text.SimpleDateFormat
import java.util.Locale

class RequestsRVAdapter(private var transfers: List<Transfer>, private var distanceUnit: String):
        RecyclerView.Adapter<RequestsRVAdapter.ViewHolder>() {

	companion object {
		private var selected = RecyclerView.NO_POSITION
		private val DATE_TIME_FULL_FORMAT = SimpleDateFormat(Utils.DATE_TIME_FULL_PATTERN, Locale.US)
		private val DATE_TIME_FORMAT = SimpleDateFormat(Utils.DATE_TIME_PATTERN, Locale.US)
	}

    override fun getItemCount(): Int = transfers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RequestsRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_request_info, parent, false))

    override fun onBindViewHolder(holder: RequestsRVAdapter.ViewHolder, pos: Int) {
        holder.bind(transfers.get(pos), distanceUnit)
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: Transfer, distanceUnit: String) = with(containerView) {
            tvTransferRequestNumber.text = String.format(resources.getString(R.string.transfer_request_num), item.id)
            tvFrom.text = item.from.name
            tvTo.text = item.to!!.name
            tvOrderDateTime.text = changeDateFormat(item.dateToLocal)
            tvDistance.text = String.format(resources.getString(R.string.distance), item.distance, distanceUnit)
        }

        fun changeDateFormat(dateTime: String): String {
            val newDate = DATE_TIME_FULL_FORMAT.parse(dateTime)
            return DATE_TIME_FORMAT.format(newDate)
        }
    }
}
