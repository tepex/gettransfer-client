package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.OfferModel

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*

class OffersRVAdapter(private val offers: List<OfferModel>, private val listener: SelectOfferClickListener):
        RecyclerView.Adapter<OffersRVAdapter.ViewHolder>() {

    companion object {
        private var selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int = offers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            OffersRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_offer_new, parent, false))

    override fun onBindViewHolder(holder: OffersRVAdapter.ViewHolder, pos: Int) {
        holder.bind(offers.get(pos), listener)
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: OfferModel, listener: SelectOfferClickListener) = with(containerView) {
            /*tvTransferRequestNumber.text = context.getString(R.string.transfer_request_num, item.id)
            tvFrom.text = item.from
            tvTo.text = item.to
            tvOrderDateTime.text = context.getString(R.string.transfer_date_local, item.dateTime)
            tvDistance.text = Utils.formatDistance(context, item.distance, item.distanceUnit)
            setOnClickListener { listener(item) }*/
            tvVehicleType.text = item.transportType
            tvCountPersons.text = " X " + item.paxMax
            tvCountBaggage.text = " X " + item.baggageMax
            tvVehicleName.text = item.transportName
            tvCarrierId.text = "#" + item.carrierId.toString()
            tvCompletedTransfers.text = "made " + item.completedTransfers + " transfers"
            tvCost.text = item.price
            //tvCarName.text = item.transportName
        }
    }
}

typealias SelectOfferClickListener = (OfferModel) -> Unit
