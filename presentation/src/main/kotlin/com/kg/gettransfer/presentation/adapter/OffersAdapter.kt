package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.offer_expanded.view.*
import kotlinx.android.synthetic.main.offer_expanded_no_photo.view.*
import kotlinx.android.synthetic.main.offer_tiny.view.*
import kotlinx.android.synthetic.main.view_offer_bottom.view.*

class OffersAdapter(private val offers: MutableList<OfferItemModel>,
                    private val clickHandler: OfferClickListener) : RecyclerView.Adapter<OffersAdapter.OfferItemViewHolder>() {


    override fun getItemCount() = offers.size
    override fun onBindViewHolder(p0: OfferItemViewHolder, p1: Int) =
            p0.bindOffer(offers[p0.adapterPosition], clickHandler)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        when (p1) {
            OFFER_EXPANDED -> R.layout.offer_expanded
            OFFER_NO_PHOTO -> R.layout.offer_expanded_no_photo
            else -> R.layout.offer_tiny
        }
                .let { LayoutInflater.from(p0.context).inflate(it, p0, false) }
                .let { OfferItemViewHolder(it) }




    override fun getItemViewType(position: Int) =
            if (viewType == PRESENTATION.TINY) OFFER_TINY
            else
                with(offers[position]) {
                    when {
                        this is OfferModel -> if (vehicle.photos.isEmpty()) OFFER_NO_PHOTO else OFFER_EXPANDED
                        else -> OFFER_EXPANDED
                    }
                }


    class OfferItemViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bindOffer(offerItem: OfferItemModel, clickListener: OfferClickListener) {
            with(containerView) {
                when (itemViewType) {
                    OFFER_EXPANDED -> {
                        OfferItemBindDelegate.bindOfferExpanded(this, offerItem)
                        hangListeners(offer_bottom.btn_book, imgOffer_mainPhoto, clickListener, offerItem)
                    }
                    OFFER_NO_PHOTO -> {
                        OfferItemBindDelegate.bindOfferNoPhoto(this, offerItem as OfferModel)
                        containerView.offer_bottom_noPhoto.btn_book.setOnClickListener { clickListener(offerItem, false) }
                        /*  set listener to view to open bottom sheet  */
                    }
                    else           -> {
                        OfferItemBindDelegate.bindOfferTiny(this, offerItem)
                        hangListeners(btn_book_tiny, img_car_photo_tiny, clickListener, offerItem)
                    }
                }
                setOnClickListener { clickListener(offerItem, true) }
            }
        }

        private fun hangListeners(bookView: View, initDetails: View, clickHandler: OfferClickListener, offerItem: OfferItemModel) {
            bookView.setOnClickListener { clickHandler(offerItem, false) }
            initDetails.setOnClickListener { clickHandler(offerItem, true) }
        }
    }

    fun add(offer: OfferModel) {
        offers.add(offer)
        notifyItemInserted(offers.size - 1)
    }

    fun changeItemRepresentation() {
        viewType =
                if (viewType == PRESENTATION.TINY) PRESENTATION.EXPANDED
                else PRESENTATION.TINY
        notifyDataSetChanged()
    }

    companion object {
        const val OFFER_EXPANDED = 1
        const val OFFER_NO_PHOTO = 2
        const val OFFER_TINY = 3

        var viewType: PRESENTATION = PRESENTATION.TINY
        enum class PRESENTATION {
            EXPANDED, TINY
        }
    }
}
typealias OfferClickListener = (OfferItemModel, Boolean) -> Unit