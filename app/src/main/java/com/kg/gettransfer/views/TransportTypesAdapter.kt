package com.kg.gettransfer.views


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.kg.gettransfer.R
import android.widget.FrameLayout
import com.kg.gettransfer.data.TransportType
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesAdapter(
        context: Context,
        realmResults: RealmResults<TransportType>,
        automaticUpdate: Boolean,
        animateResults: Boolean) : RealmRecyclerViewAdapter<TransportType, TransportTypesAdapter.ViewHolder>(realmResults, true) {

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val title: TextView = container.findViewById(R.id.name)
        val pax: TextView = container.findViewById(R.id.pax)
        val luggage: TextView = container.findViewById(R.id.luggage)
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
