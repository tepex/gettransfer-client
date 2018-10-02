package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*

class OffersRVAdapter(private val offers: List<OfferModel>, private val listener: SelectOfferClickListener):
        RecyclerView.Adapter<OffersRVAdapter.ViewHolder>() {

    companion object {
        private var selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int = offers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            OffersRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_offer, parent, false))

    override fun onBindViewHolder(holder: OffersRVAdapter.ViewHolder, pos: Int) {
        holder.bind(offers.get(pos), listener)
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: OfferModel, listener: SelectOfferClickListener) = with(containerView) {
            tvVehicleType.setText(Utils.getTransportTypeName(item.transportType))
            tvCountPersons.text = context.getString(R.string.count_persons_and_baggage, item.paxMax)
            tvCountBaggage.text = context.getString(R.string.count_persons_and_baggage, item.baggageMax)
            tvVehicleName.text = item.transportName
            tvCarrierId.text = context.getString(R.string.driver_number, item.carrierId)
            tvCompletedTransfers.text = context.getString(R.string.driver_completed_transfers, item.completedTransfers)
            tvCostDefault.text = item.priceDefault
            if(item.pricePreferred != null) {
                tvCostPreferred.text = context.getString(R.string.preferred_cost, item.pricePreferred)
                tvCostPreferred.visibility = View.VISIBLE
            }
            if(item.wifi) imgOptionFreeWiFi.visibility = View.VISIBLE
            if(item.refreshments) imgOptionFreeWater.visibility = View.VISIBLE
            setOnClickListener { listener(item) }
        }
    }
}

typealias SelectOfferClickListener = (OfferModel) -> Unit
