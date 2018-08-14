package com.kg.gettransfer.presentation.ui

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.address_list_item.view.*

import timber.log.Timber

class AddressAdapter(val listener: (String) -> Unit): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
	//val list = arrayOf("item 1", "item 2", "item 3")
	val list = ArrayList<String>()
	var selectedPos = RecyclerView.NO_POSITION
	
	init {
		for(i in 1..40) list.add("item $i")
	}
	
	override fun getItemCount(): Int {
		return list.size
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = 
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))
	
	override fun onBindViewHolder(holder: ViewHolder, pos: Int) = holder.bind(list.get(pos), listener)

	class ViewHolder(override val containerView: View): 
		RecyclerView.ViewHolder(containerView), LayoutContainer {
			
		fun bind(item: String, listener: (String) -> Unit) = with(containerView) {
			addressItem.text = item
			setOnClickListener { listener(item) }
		}
	}
}
