package com.kg.gettransfer.presentation.adapter

import android.graphics.Paint

import android.support.v7.widget.RecyclerView

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.view.*
import kotlinx.android.synthetic.main.view_offer_car_name_and_options.view.*

class OffersRVAdapter(
    private val offers: MutableList<OfferModel>,
    private val listener: SelectOfferClickListener
) : RecyclerView.Adapter<OffersRVAdapter.ViewHolder>() {

    override fun getItemCount() = offers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            OffersRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_offer, parent, false))

    override fun onBindViewHolder(holder: OffersRVAdapter.ViewHolder, pos: Int) {
        holder.bind(offers.get(pos), listener)
    }

    fun add(offer: OfferModel) {
        offers.add(offer)
        notifyDataSetChanged()
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: OfferModel, listener: SelectOfferClickListener) = with(containerView) {
            tvCarrierId.text = "#".plus(item.carrier.id)
 //           tvCompletedTransfers.text = context.getString(R.string.LNG_MADE).plus(" ${item.carrier.completedTransfers} ").plus(context.getString(R.string.LNG_RIDES))
            tvCompletedTransfers.text = String.format(context.resources.getString(R.string.LNG_MADE_RIDES), item.carrier.completedTransfers)
            tvCostDefault.text = item.price.base.default
            item.price.base.preferred?.let {
                tvCostPreferred.text = Utils.formatPrice(context, it)
                tvCostPreferred.isVisible = true
            }
            item.price.withoutDiscount?.let {
                tvCostWithoutDiscountDefault.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvCostWithoutDiscountDefault.text = it.default
                if(it.preferred != null) with(tvCostWithoutDiscountPreferred) {
                    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    text = Utils.formatPrice(context, it.preferred)
                    isVisible = true
                }
                layoutCostWithoutDiscount.visibility = View.VISIBLE
            }
            if (item.vehicle.photos.isNotEmpty()) {
                layoutWithCarImage.isVisible = true
                Glide.with(this).load(item.vehicle.photos.first()).into(carPhoto)
                if (item.vehicle.photos.size > 1) ivManyPhotos.isVisible = true
                item.carrier.ratings.average?.let { ratingBar.rating = it }
                if (item.carrier.approved) ivLike.isVisible = true
                setTexts(bottomLayoutForImage, tvCountPersonsOnCarImage, tvCountBaggageOnCarImage, item)
            } else {
                layoutNoCarImage.isVisible = true
                tvVehicleType.setText(item.vehicle.transportType.nameId!!)
                setTexts(bottomLayoutNoImage, tvCountPersons, tvCountBaggage, item)
            }

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 4, 8, 4)

            val raws = item.carrier.languages.size.ceil(2)
            for (i in 0..raws) {
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.gravity = Gravity.CENTER
                for (j in 0..1) {
                    if (i * 2 + j == item.carrier.languages.size) break
                    val ivLanguage = ImageView(context)
                    ivLanguage.setImageResource(Utils.getLanguageImage(item.carrier.languages[i * 2 + j].delegate.language))
                    ivLanguage.layoutParams = lp
                    layout.addView(ivLanguage)
                }
                layoutLanguages.addView(layout)
            }

            setOnClickListener           { listener(item, false) }
            btnSelect.setOnClickListener { listener(item, false) }
            column1.setOnClickListener   { listener(item, true) }
        }

        private fun setTexts(layout: View, textViewPax: TextView, textViewBaggage: TextView, item: OfferModel) {
            layout.tvVehicleName.text =
                if (item.vehicle.color == null) item.vehicle.vehicleBase.name
                else Utils.getVehicleNameWithColor(layout.context, item.vehicle.vehicleBase.name, item.vehicle.color)
            layout.imgOptionFreeWiFi.isVisible = item.wifi
            layout.imgOptionFreeWater.isVisible = item.refreshments

            textViewPax.text = Utils.formatPersons(layout.context, item.vehicle.transportType.paxMax)
            textViewBaggage.text = Utils.formatLuggage(layout.context, item.vehicle.transportType.luggageMax)
        }
    }
}

typealias SelectOfferClickListener = (OfferModel, Boolean) -> Unit
