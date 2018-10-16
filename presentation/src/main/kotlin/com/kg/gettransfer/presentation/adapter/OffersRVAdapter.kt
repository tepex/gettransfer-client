package com.kg.gettransfer.presentation.adapter

import android.content.Context

import android.graphics.PorterDuff

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

import android.text.style.ImageSpan
import android.text.SpannableStringBuilder
import android.text.Spanned

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*
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
            if(item.vehicle.photos.isNotEmpty()) {
                layoutWithCarImage.visibility = View.VISIBLE
                Glide.with(this).load(context.getString(R.string.api_photo_url, item.vehicle.photos.first())).into(carPhoto)
                ratingBar.rating = item.averageRating!!.toFloat()
                setTexts(bottomLayoutForImage, tvCountPersonsOnCarImage, tvCountBaggageOnCarImage, item, context)
            } else {
                layoutNoCarImage.visibility = View.VISIBLE
                tvVehicleType.setText(item.vehicle.transportType.nameId!!)
                setTexts(bottomLayoutNoImage, tvCountPersons, tvCountBaggage, item, context)
            }
            val carrierLanguages = item.carrierLanguages

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 4, 8, 4)

            var raws = carrierLanguages.size / 2
            if (carrierLanguages.size % 2 == 0) --raws

            for(i in 0..raws) {
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.gravity = Gravity.CENTER
                for(j in 0..1) {
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

        fun setTexts(layout: View, textViewPax: TextView, textViewBaggage: TextView, item: OfferModel, context: Context) {
            val drawableCompat = ContextCompat.getDrawable(context, R.drawable.ic_circle_car_color_indicator)
            drawableCompat!!.setColorFilter(ContextCompat.getColor(context, Utils.getColorVehicle(item.vehicle.color)), PorterDuff.Mode.SRC_IN)
            drawableCompat.setBounds(4, 0, drawableCompat.intrinsicWidth + 4, drawableCompat.intrinsicHeight)
            
            val ssBuilder = SpannableStringBuilder(item.vehicle.transportType.id + " ")
            val colorCarImageSpan = ImageSpan(drawableCompat, ImageSpan.ALIGN_BASELINE)
            ssBuilder.setSpan(colorCarImageSpan, ssBuilder.length - 1, ssBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            layout.tvVehicleName.text = ssBuilder

            if(item.wifi) layout.imgOptionFreeWiFi.visibility = View.VISIBLE
            if(item.refreshments) layout.imgOptionFreeWater.visibility = View.VISIBLE
            
            textViewPax.text = context.getString(R.string.count_persons_and_baggage, item.vehicle.transportType.paxMax)
            textViewBaggage.text = context.getString(R.string.count_persons_and_baggage, item.vehicle.transportType.luggageMax)
        }
    }
}

typealias SelectOfferClickListener = (OfferModel) -> Unit
