package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel.Type
import com.kg.gettransfer.presentation.presenter.CarrierTripsListPresenter

import kotlinx.android.extensions.LayoutContainer

import kotlinx.android.synthetic.main.view_carrier_trip_info_item_rv.view.*
import kotlinx.android.synthetic.main.view_carrier_trips_end_today.*
import kotlinx.android.synthetic.main.view_carrier_trips_subtitle.view.*
import kotlinx.android.synthetic.main.view_carrier_trips_title.view.*

class CarrierTripsRVAdapter(
        private val presenter: CarrierTripsListPresenter,
        private var tripsItems: List<CarrierTripsRVItemModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = tripsItems.size

    override fun getItemViewType(position: Int) = tripsItems[position].type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        Type.TITLE.ordinal          -> ViewHolderTitle(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_title, parent, false))
        Type.SUBTITLE.ordinal       -> ViewHolderSubtitle(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_subtitle, parent, false))
        Type.ITEM.ordinal           -> ViewHolderItem(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trip_info_item_rv, parent, false))
        Type.END_TODAY_VIEW.ordinal -> ViewHolderEndToday(LayoutInflater.from(parent.context).inflate(R.layout.view_carrier_trips_end_today, parent, false))
        else -> throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val trip = tripsItems[pos]
        when (trip.type) {
            Type.TITLE          -> (holder as ViewHolderTitle).bind(trip.titleText!!)
            Type.SUBTITLE       -> (holder as ViewHolderSubtitle).bind(trip.isToday!!, trip.titleText!!)
            Type.ITEM           -> (holder as ViewHolderItem).bind(trip.item!!) { tripId, transferId -> presenter.onTripSelected(tripId, transferId) }
            Type.END_TODAY_VIEW -> (holder as ViewHolderEndToday).bind(tripsItems[pos - 1].type != Type.ITEM)
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
            subtitleText.text =
                if (isToday) context.getString(R.string.LNG_TRIPS_TODAY).toUpperCase().plus(": $text") else text
        }
    }

    class ViewHolderItem(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(item: CarrierTripBaseModel, listener: ClickOnCarrierTripHandler) = with(containerView) {
            layoutCarrierTripInfo.setInfo(item)
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
