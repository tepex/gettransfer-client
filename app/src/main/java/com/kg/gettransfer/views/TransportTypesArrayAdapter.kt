package com.kg.gettransfer.views


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.http.json.PriceRange
import com.kg.gettransfer.realm.TransportType


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesArrayAdapter(
        c: Context,
        values: List<TransportType>,
        private val checked: BooleanArray,
        private val prices: Map<TransportType, PriceRange>?)
    : ArrayAdapter<TransportType>(c, R.layout.item_transport_type, values) {

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val image: ImageView = container.findViewById(R.id.ivImage)

        val title: TextView = container.findViewById(R.id.tvTo)
        val pax: TextView = container.findViewById(R.id.tvPax)
        val luggage: TextView = container.findViewById(R.id.tvDateTime)

        val price: TextView = container.findViewById(R.id.tvPrice)

        fun setSelected(b: Boolean) {
            if (b) {
                image.setBackgroundResource(R.drawable.bg_circle_lightgray)
                image.setImageResource(R.drawable.ic_local_taxi_blue_24dp)
                title.setTextColor(
                        ContextCompat.getColor(context, R.color.colorAccent))
            } else {
                image.setBackgroundResource(0)
                image.setImageResource(R.drawable.ic_local_taxi_black_24dp)
                title.setTextColor(
                        ContextCompat.getColor(context, R.color.colorTextAccentDisabled))
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var convertView = convertView
        val holder: ViewHolder?

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_transport_type, null)
            holder = ViewHolder(convertView as ConstraintLayout)
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as ViewHolder
        }

        //val drawable = context.resources.getDrawable(R.drawable.list_icon)

        holder.title.text = getItem(position).title

        holder.pax.text = getItem(position).paxMax.toString()
        holder.luggage.text = getItem(position).luggageMax.toString()

        val priceFrom = prices?.get(getItem(position))?.min
        holder.price.text =
                if (priceFrom == null) "-"
                else context.getString(R.string.from_price) + " " + priceFrom

        holder.setSelected(checked[position])

        return convertView
    }
}
