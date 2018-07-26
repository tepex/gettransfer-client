package com.kg.gettransfer.adapter


import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.module.TransportTypes
import com.kg.gettransfer.realm.Offer
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_offer.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class OffersAdapter(
        realmResults: RealmResults<Offer>)
    : RealmRecyclerViewAdapter<Offer, OffersAdapter.ViewHolder>(realmResults, true),
        KoinComponent {

    private val transportTypes: TransportTypes by inject()

    var icl: View.OnClickListener = View.OnClickListener {
        Log.i("OffersAdapter", "Item clicked unspecified")
    }

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val carrier: TextView = container.findViewById(R.id.tvCarrier)
        val price: TextView = container.findViewById(R.id.tvPrice)
        val priceSmall: TextView = container.findViewById(R.id.tvPriceSmall)
        val vehicle: TextView = container.findViewById(R.id.tvVehicle)
        val vehicleType: TextView = container.findViewById(R.id.tvVehicleType)
        val facilities: TextView = container.findViewById(R.id.tvFacilities)
        val ratingBar: RatingBar = container.findViewById(R.id.ratingBar)
        val transfers: TextView = container.findViewById(R.id.tvTransfers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_offer, parent, false)

        v.setOnClickListener(icl)
        v.btnBook.setOnClickListener(icl)

        v.btnBook.background.colorFilter = LightingColorFilter(
                ContextCompat.getColor(parent.context, R.color.colorYellow), 0)

        return ViewHolder(v as ConstraintLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = holder.itemView.context

        val item = getItem(position) ?: return

        holder.vehicle.text = (item.vehicle?.name ?: "Unknown vehicle")
        holder.vehicleType.text = transportTypes.typesMap[item.vehicle?.transportTypeID]?.title

        holder.carrier.text = item.carrier?.title(c)

        val facilities = item.facilities(c)
        holder.facilities.text = facilities
        holder.facilities.visibility = if (facilities == null) GONE else VISIBLE

        val defaultCurrency = item.price?.base?.defaultCurrency
        val preferredCurrency = item.price?.base?.preferredCurrency
        if (preferredCurrency != null) {
            holder.price.text = preferredCurrency
            holder.priceSmall.text = defaultCurrency ?: "No price"
        } else {
            holder.price.text = defaultCurrency ?: "No price"
        }

        holder.ratingBar.rating = (item.carrier?.rating?.average?.toFloat() ?: 0f) / 5f

        holder.transfers.text = (item.carrier?.completedTransfers ?: 0).toString()

        holder.itemView.setTag(R.id.key_id, item.id)
        holder.itemView.btnBook.setTag(R.id.key_id, item.id)
    }
}
