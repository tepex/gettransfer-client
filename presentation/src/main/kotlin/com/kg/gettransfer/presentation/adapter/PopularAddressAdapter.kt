package com.kg.gettransfer.presentation.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PopularPlace

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.popular_address_list_item.*

class PopularAddressAdapter(
    private val list: List<PopularPlace>,
    private val click: (PopularPlace) -> Unit
) : RecyclerView.Adapter<PopularAddressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PopularAddressAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_address_list_item, parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PopularAddressAdapter.ViewHolder, pos: Int) =
        holder.bind(list[pos]) { click(it) }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(place: PopularPlace, listener: PopularClickHandler) = with(containerView) {
            popular_title.text = place.title
            popular_icon.setImageDrawable(ContextCompat.getDrawable(containerView.context, place.icon))
            setOnClickListener { listener(place) }
        }
    }
}

typealias PopularClickHandler = (PopularPlace) -> Unit
