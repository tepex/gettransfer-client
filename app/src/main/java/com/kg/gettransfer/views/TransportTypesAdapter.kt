package com.kg.gettransfer.views


import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.models.TransportType
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesAdapter(
        realmResults: RealmResults<TransportType>,
        autoUpdate: Boolean) : RealmRecyclerViewAdapter<TransportType, TransportTypesAdapter.ViewHolder>(realmResults, autoUpdate) {

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val title: TextView = container.findViewById(R.id.tvName)
        val pax: TextView = container.findViewById(R.id.tvPax)
        val luggage: TextView = container.findViewById(R.id.tvLuggage)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.transport_type_item, parent, false)
        return ViewHolder(v as ConstraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = getItem(position)
        holder?.title?.text = item?.title
        holder?.pax?.text = item?.paxMax.toString()
        holder?.luggage?.text = item?.luggageMax.toString()
    }
}
