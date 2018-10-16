package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.popular_address_list_item.*

class PopularAddressAdapter(private val presenter: SearchPresenter, private val list: List<PopularPlace>): RecyclerView.Adapter<PopularAddressAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            PopularAddressAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_address_list_item, parent, false))


    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: PopularAddressAdapter.ViewHolder, pos: Int) {
        holder.bind(list[pos])
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer{
        fun bind(place: PopularPlace){
            popular_title.text = containerView.context.getString(place.title)
            popular_icon.setImageDrawable(containerView.context.getDrawable(place.icon))
        }
    }
}