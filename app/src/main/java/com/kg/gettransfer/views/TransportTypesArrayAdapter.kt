package com.kg.gettransfer.views


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.TransportType


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesArrayAdapter(c: Context, values: List<TransportType>)
    : ArrayAdapter<TransportType>(c, R.layout.item_transport_type, values) {

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val image: ImageView = container.findViewById(R.id.ivImage)

        val title: TextView = container.findViewById(R.id.tvTo)
        val pax: TextView = container.findViewById(R.id.tvPax)
        val luggage: TextView = container.findViewById(R.id.tvDateTime)

        val price: TextView = container.findViewById(R.id.tvPrice)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var convertView = convertView
        val holder: ViewHolder?

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_transport_type, null)

            holder = ViewHolder(convertView as ConstraintLayout)
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as ViewHolder
        }

        //val drawable = context.resources.getDrawable(R.drawable.list_icon)

        holder.title.text = getItem(position).title

        holder.pax.text = getItem(position).paxMax.toString()
        holder.luggage.text = getItem(position).luggageMax.toString()

        holder.price.text = "$100" //getItem(position).price.toString()

        return convertView
    }
}
