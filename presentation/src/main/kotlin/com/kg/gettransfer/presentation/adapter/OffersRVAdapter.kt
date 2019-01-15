package com.kg.gettransfer.presentation.adapter

import android.content.Context
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
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.*
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_offer.*
import kotlinx.android.synthetic.main.view_offer.view.*
import kotlinx.android.synthetic.main.view_offer_car_name_and_options.view.*
import java.util.*

class OffersRVAdapter(
        private val offers: MutableList<OfferItem>,
        private val listener: SelectOfferClickListener
) : RecyclerView.Adapter<OffersRVAdapter.ViewHolder>() {

    override fun getItemCount() = offers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OffersRVAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_offer, parent, false))

    override fun onBindViewHolder(holder: OffersRVAdapter.ViewHolder, pos: Int) {
        holder.bind(offers[pos], listener)
    }

    override fun getItemViewType(position: Int) = when (offers[position]) {
        is OfferModel        -> R.string.LNG_RIDES
        is BookNowOfferModel -> R.string.LNG_MADE_RIDES
    }

    fun add(offer: OfferModel) {
        offers.add(offer)
        notifyDataSetChanged()
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: OfferItem, listener: SelectOfferClickListener) = with(containerView) {
            when (item) {
                is OfferModel -> {
                    tvCarrierId.text = "#${item.carrier.id}"
                    setCompletedTransfers(context.getString(R.string.LNG_MADE)
                            .plus(" ${item.carrier.completedTransfers} ").plus(context.getString(R.string.LNG_RIDES)))
                    setCostDefault(item.price.base.def)
                    item.price.base.preferred?.let {
                        tvCostPreferred.text = Utils.formatPrice(context, it)
                        tvCostPreferred.isVisible = true
                    }
                    item.price.withoutDiscount?.let { setWithoutDiscount(it) }
                    setOfferCarPhoto(containerView, item)
                    setLanguages(context, item.carrier.languages)
                }
                is BookNowOfferModel -> {
                    setCompletedTransfers(String.format(context.resources.getString(R.string.LNG_MADE_RIDES), 10))
                    setCostDefault(item.base.def)
                    item.withoutDiscount?.let { setWithoutDiscount(it) }
                    setBookNowCarPhoto(context, item)
                    setLanguages(context, listOf(LocaleModel(Locale.ENGLISH)))
                }
            }

            setOnClickListener { listener(item, false) }
            btnSelect.setOnClickListener { listener(item, false) }
            column1.setOnClickListener { listener(item, true) }
        }

        private fun setLanguages(context: Context, languages: List<LocaleModel>) {
            val lp = createLayoutParams()

            for (row in 0 until languages.size.ceil(TRANSPORT_TYPES_COLUMNS)) {
                val layout = createLinearLayout(context)
                for (col in 0 until TRANSPORT_TYPES_COLUMNS) {
                    val i = row * TRANSPORT_TYPES_COLUMNS + col
                    if (i == languages.size) break
                    layout.addView(ImageView(context).apply {
                        setImageResource(Utils.getLanguageImage(languages[i].delegate.language))
                        layoutParams = lp
                    }, col)
                }
                layoutLanguages.addView(layout, row)
            }
        }

        private fun createLinearLayout(context: Context): LinearLayout {
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.gravity = Gravity.CENTER
            return layout
        }

        private fun createLayoutParams(): LinearLayout.LayoutParams {
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 4, 8, 4)
            return lp
        }

        private fun setBookNowCarPhoto(context: Context, bookNow: BookNowOfferModel) {
            layoutWithCarImage.isVisible = true
            bookNow.transportType.id.let { TransportTypeMapper.getImageById(it) }.let { carPhoto.setImageResource(it) }
            carPhoto.layoutParams.height = 400
            carPhoto.requestLayout()
            ratingBar.rating = 4f
            ivLike.isVisible = true
            tvCountPersonsOnCarImage.text = bookNow.transportType.paxMax.let { Utils.formatPersons(bottomLayoutForImage.context, it) }
            tvCountBaggageOnCarImage.text = bookNow.transportType.luggageMax.let { Utils.formatLuggage(bottomLayoutForImage.context, it) }
            bottomLayoutForImage.tvVehicleName.text = bookNow.transportType.id.toString()
                    .plus("\n").plus(bookNow.transportType.id.let { TransportTypeMapper.getDescriptionById(it) }.let { context.getString(it) })
        }

        private fun setOfferCarPhoto(view: View, item: OfferModel) {
            if (item.vehicle.photos.isNotEmpty()) {
                layoutWithCarImage.isVisible = true
                Glide.with(view).load(item.vehicle.photos.first()).into(carPhoto)
                if (item.vehicle.photos.size > 1) ivManyPhotos.isVisible = true
                item.carrier.ratings.average?.let { ratingBar.rating = it }
                if (item.carrier.approved) ivLike.isVisible = true
                setTexts(bottomLayoutForImage, tvCountPersonsOnCarImage, tvCountBaggageOnCarImage, item)
            } else {
                layoutNoCarImage.isVisible = true
                tvVehicleType.setText(item.vehicle.transportType.nameId!!)
                setTexts(bottomLayoutNoImage, tvCountPersons, tvCountBaggage, item)
            }
        }

        private fun setWithoutDiscount(withoutDiscount: MoneyModel) {
            tvCostWithoutDiscountDefault.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            tvCostWithoutDiscountDefault.text = withoutDiscount.def
            if (withoutDiscount.preferred != null) with(tvCostWithoutDiscountPreferred) {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                text = Utils.formatPrice(context, withoutDiscount.preferred)
                isVisible = true
            }
            layoutCostWithoutDiscount.isVisible = true
        }

        private fun setCostDefault(text: String) {
            tvCostDefault.text = text
        }

        private fun setCompletedTransfers(text: String) {
            tvCompletedTransfers.text = text
        }

        private fun setTexts(layout: View, textViewPax: TextView, textViewBaggage: TextView, item: OfferModel) {
            layout.tvVehicleName.text =
                if (item.vehicle.color == null) item.vehicle.name
                else Utils.getVehicleNameWithColor(
                    layout.context,
                    item.vehicle.name,
                    item.vehicle.color
                )
            layout.imgOptionFreeWiFi.isVisible = item.wifi
            layout.imgOptionFreeWater.isVisible = item.refreshments

            textViewPax.text = Utils.formatPersons(layout.context, item.vehicle.transportType.paxMax)
            textViewBaggage.text = Utils.formatLuggage(layout.context, item.vehicle.transportType.luggageMax)
        }
    }

    companion object {
        const val TRANSPORT_TYPES_COLUMNS = 2
    }
}

typealias SelectOfferClickListener = (OfferItem, Boolean) -> Unit
