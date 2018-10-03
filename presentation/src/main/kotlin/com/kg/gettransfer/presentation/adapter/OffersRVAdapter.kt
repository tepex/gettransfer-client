package com.kg.gettransfer.presentation.adapter

import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_offer_car_name_and_options.view.*

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
            tvCarrierId.text = context.getString(R.string.driver_number, item.carrierId)
            tvCompletedTransfers.text = context.getString(R.string.driver_completed_transfers, item.completedTransfers)
            tvCostDefault.text = item.priceDefault
            if(item.pricePreferred != null) {
                tvCostPreferred.text = context.getString(R.string.preferred_cost, item.pricePreferred)
                tvCostPreferred.visibility = View.VISIBLE
            }
            if(item.vehiclePhotos.isNotEmpty()){
                layoutWithCarImage.visibility = View.VISIBLE
                Glide.with(this).load(context.getString(R.string.api_photo_url, item.vehiclePhotos[0])).into(carPhoto)
                tvCountPersonsOnCarImage.text = context.getString(R.string.count_persons_and_baggage, item.paxMax)
                tvCountBaggageOnCarImage.text = context.getString(R.string.count_persons_and_baggage, item.baggageMax)
                val stars = ratingBar.progressDrawable as LayerDrawable
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.colorStarsInRatingBar), PorterDuff.Mode.SRC_ATOP)
                ratingBar.rating = item.averageRating!!.toFloat()
                setVehicleNameAndOptions(bottomLayoutForImage, item)
            } else {
                layoutNoCarImage.visibility = View.VISIBLE
                tvVehicleType.setText(Utils.getTransportTypeName(item.transportType))
                tvCountPersons.text = context.getString(R.string.count_persons_and_baggage, item.paxMax)
                tvCountBaggage.text = context.getString(R.string.count_persons_and_baggage, item.baggageMax)
                setVehicleNameAndOptions(bottomLayoutNoImage, item)
            }
            btnSelect.setOnClickListener { listener(item) }
        }

        fun setVehicleNameAndOptions(layout: View, item: OfferModel){
            layout.tvVehicleName.text = item.transportName
            if(item.wifi) layout.bottomLayoutForImage.imgOptionFreeWiFi.visibility = View.VISIBLE
            if(item.refreshments) layout.bottomLayoutForImage.imgOptionFreeWater.visibility = View.VISIBLE
        }
    }
}

typealias SelectOfferClickListener = (OfferModel) -> Unit
