package com.kg.gettransfer.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate
import com.kg.gettransfer.presentation.model.OfferItemModel

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.offer_tiny.view.*

class OffersAdapter(
    private val clickHandler: OfferClickListener
) : RecyclerView.Adapter<OffersAdapter.OfferItemViewHolder>() {

    private var offers: ArrayList<OfferItemModel> = arrayListOf()
    private var isNameSignPresent = false

    fun update(offers: List<OfferItemModel>, isNameSignPresent: Boolean) {
        this.offers.clear()
        this.offers.addAll(offers)
        this.isNameSignPresent = isNameSignPresent
        notifyDataSetChanged()
    }

    override fun getItemCount() = offers.size
    override fun onBindViewHolder(p0: OfferItemViewHolder, p1: Int) =
        p0.bindOffer(offers[p0.adapterPosition], isNameSignPresent, clickHandler)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        OfferItemViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.offer_tiny, p0, false))

    class OfferItemViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindOffer(offerItem: OfferItemModel, isNameSignPresent: Boolean, clickListener: OfferClickListener) {
            with(containerView) {
                OfferItemBindDelegate.bindOfferTiny(this, offerItem, isNameSignPresent)
                hangListeners(this, btn_book_tiny, img_car_photo_tiny, clickListener, offerItem)
            }
        }

        private fun hangListeners(
            offerView: View,
            bookView: View,
            initDetails: View,
            clickHandler: OfferClickListener,
            offerItem: OfferItemModel
        ) {
            offerView.setOnClickListener { clickHandler(offerItem, true) }
            bookView.setThrottledClickListener { clickHandler(offerItem, false) }
            initDetails.setOnClickListener { clickHandler(offerItem, true) }
        }
    }
}

typealias OfferClickListener = (OfferItemModel, Boolean) -> Unit
