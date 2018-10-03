package com.kg.gettransfer.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_offer_car_name_and_options.view.*
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

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
            if(item.vehiclePhotos.isNotEmpty()) {
                layoutWithCarImage.visibility = View.VISIBLE
                Glide.with(this).load(context.getString(R.string.api_photo_url, item.vehiclePhotos[0])).into(carPhoto)
                ratingBar.rating = item.averageRating!!.toFloat()
                setTexts(bottomLayoutForImage, tvCountPersonsOnCarImage, tvCountBaggageOnCarImage, item, context)
            } else {
                layoutNoCarImage.visibility = View.VISIBLE
                tvVehicleType.setText(Utils.getTransportTypeName(item.transportType))
                setTexts(bottomLayoutNoImage, tvCountPersons, tvCountBaggage, item, context)
            }
            val carrierLanguages = item.carrierLanguages

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 0, 8, 0)

            var raws = carrierLanguages.size / 2
            if (carrierLanguages.size % 2 == 0) raws -= 1

            for(i in 0..raws) {
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.gravity = Gravity.CENTER
                for (j in 0..1){
                    if(i * 2 + j == carrierLanguages.size) break
                    val ivLanguage = ImageView(context)
                    ivLanguage.setImageResource(Utils.getLanguageImage(carrierLanguages[i * 2 + j].language))
                    ivLanguage.layoutParams = lp
                    layout.addView(ivLanguage)
                }
                layoutLanguages.addView(layout)
            }

            setOnClickListener { listener(item) }
        }

        fun setTexts(layout: View, textViewPax: TextView, textViewBaggage: TextView, item: OfferModel, context: Context){
            layout.tvVehicleName.text = item.transportName
            if(item.wifi) layout.bottomLayoutForImage.imgOptionFreeWiFi.visibility = View.VISIBLE
            if(item.refreshments) layout.bottomLayoutForImage.imgOptionFreeWater.visibility = View.VISIBLE
            textViewPax.text = context.getString(R.string.count_persons_and_baggage, item.paxMax)
            textViewBaggage.text = context.getString(R.string.count_persons_and_baggage, item.baggageMax)
        }
    }
}

typealias SelectOfferClickListener = (OfferModel) -> Unit
