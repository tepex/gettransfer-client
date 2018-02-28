package com.kg.gettransfer.views


import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.Offer
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_offer.view.*


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class OffersAdapter(
        realmResults: RealmResults<Offer>,
        autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<Offer, OffersAdapter.ViewHolder>(realmResults, autoUpdate) {

    private val icl: View.OnClickListener = View.OnClickListener {
        Log.i("OffersAdapter", "Item clicked")
//        val context = it.context
//        val intent = Intent(context, TransferActivity::class.java)
//        intent.putExtra("id", it.getTag(R.id.key_id) as Int)
//        startActivity(context, intent, null)
    }

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val carrier: TextView = container.findViewById(R.id.tvCarrier)
        val price: TextView = container.findViewById(R.id.tvPrice)
        val vehicle: TextView = container.findViewById(R.id.tvVehicle)
        val facilities: TextView = container.findViewById(R.id.tvFacilities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.item_offer, parent, false)
        v.setOnClickListener(icl)
        v.tvBook.background.colorFilter = LightingColorFilter(0xffffcc4c.toInt(), 0x0)
        return ViewHolder(v as ConstraintLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.carrier.text = item.carrier?.title ?: "Carrier #" + item.carrier?.id
        holder.vehicle.text = (item.vehicle?.name
                ?: "Unknown vehicle") + ("\n (" + "Economy ×3 ×3" + ")")
        holder.facilities.text = "WiFi   Water   Music"
        holder.price.text = item.price?.toString() ?: "No price"
        holder.itemView.setTag(R.id.key_id, item.id)
    }
}
