package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel
import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter

import kotlinx.android.extensions.LayoutContainer

import kotlinx.android.synthetic.main.view_carrier_trip_info_item_rv.view.*
import kotlinx.android.synthetic.main.view_carrier_trips_end_today.*
import kotlinx.android.synthetic.main.view_carrier_trips_subtitle.view.*
import kotlinx.android.synthetic.main.view_carrier_trips_title.view.*

class CarrierTripsRVAdapter(
    private val presenter: CarrierTripsPresenter,
    private var tripsItems: List<CarrierTripsRVItemModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_HOLDER_TITLE          = 0
        private const val VIEW_HOLDER_SUBTITLE       = 1
        private const val VIEW_HOLDER_ITEM           = 2
        private const val VIEW_HOLDER_END_TODAY_VIEW = 3
    }

    override fun getItemCount() = tripsItems.size

    override fun getItemViewType(position: Int) = when (tripsItems[position].type) {
        CarrierTripsRVItemModel.TYPE_TITLE    -> VIEW_HOLDER_TITLE
        CarrierTripsRVItemModel.TYPE_SUBTITLE -> VIEW_HOLDER_SUBTITLE
        CarrierTripsRVItemModel.TYPE_ITEM     -> VIEW_HOLDER_ITEM
        else                                  -> VIEW_HOLDER_END_TODAY_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_HOLDER_TITLE    -> ViewHolderTitle(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_title, parent, false))
        VIEW_HOLDER_SUBTITLE -> ViewHolderSubtitle(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_subtitle, parent, false))
        VIEW_HOLDER_ITEM     -> ViewHolderItem(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trip_info_item_rv, parent, false))
        else                 -> ViewHolderEndToday(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_end_today, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val trip = tripsItems[pos]
        when (holder.itemViewType) {
            VIEW_HOLDER_TITLE          -> (holder as ViewHolderTitle).bind(trip.titleText!!)
            VIEW_HOLDER_SUBTITLE       -> (holder as ViewHolderSubtitle).bind(trip.isToday!!, trip.titleText!!)
            VIEW_HOLDER_ITEM           -> (holder as ViewHolderItem).bind(trip.item!!) { tripId, transferId -> presenter.onTripSelected(tripId, transferId) }
            VIEW_HOLDER_END_TODAY_VIEW -> (holder as ViewHolderEndToday).bind(tripsItems[pos - 1].type != CarrierTripsRVItemModel.TYPE_ITEM)
        }
    }

    class ViewHolderTitle(override val containerView: View):
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(text: String) = with(containerView) {
            titleText.text = text
        }
    }

    class ViewHolderSubtitle(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(isToday: Boolean, text: String) = with(containerView) {
            subtitleText.text = if (isToday) context.getString(R.string.LNG_TRIPS_TODAY).toUpperCase().plus(": $text")
                                else text
        }
    }

    class ViewHolderItem(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(item: CarrierTripBaseModel, listener: ClickOnCarrierTripHandler) = with(containerView) {
            layoutCarrierTripInfo.setInfo(item, null)
            setOnClickListener { listener(item.id, item.transferId) }
        }
    }

    class ViewHolderEndToday(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer{

        fun bind(noHaveTripsToday: Boolean) {
            textNoTripsToday.isVisible = noHaveTripsToday
        }
    }
}

typealias ClickOnCarrierTripHandler = (Long, Long) -> Unit
