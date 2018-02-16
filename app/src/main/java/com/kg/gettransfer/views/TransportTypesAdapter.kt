package com.kg.gettransfer.views


import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.TransportType
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesAdapter(
        realmResults: RealmResults<TransportType>,
        autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<TransportType, TransportTypesAdapter.ViewHolder>(realmResults, autoUpdate) {

    private var selected: ArrayList<Boolean> = ArrayList(30)

    init {
        for (i: Int in 0..30) selected.add(false)
    }


    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val image: ImageView = container.findViewById(R.id.ivImage)

        val title: TextView = container.findViewById(R.id.tvName)
        val pax: TextView = container.findViewById(R.id.tvPax)
        val luggage: TextView = container.findViewById(R.id.tvLuggage)

        var selected: Boolean = false
            set(value) {
                itemView.setBackgroundColor(if (value) 0xffffcc4c.toInt() else 0)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.item_transport_type, parent, false)
        return ViewHolder(v as ConstraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return

        val item = getItem(position)
        holder.title.text = item?.title
        holder.pax.text = item?.paxMax.toString()
        holder.luggage.text = item?.luggageMax.toString()
        holder.selected = selected[position]
        holder.itemView.setOnClickListener {
            selected[position] = !selected[position]
            holder.selected = selected[position]
        }
    }

    fun getSelectedIds(): ArrayList<Int> {
        val a = ArrayList<Int>()
        (0..30)
                .filter { selected[it] }
                .mapNotNull { getItem(it) }
                .forEach { a.add(it.id) }
        return a
    }
}
