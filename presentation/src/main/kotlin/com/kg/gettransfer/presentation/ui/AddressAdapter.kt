package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.address_list_item.view.*

import timber.log.Timber

class AddressAdapter(private val context: Context): RecyclerView.Adapter<ViewHolder>() {
	//val list = arrayOf("item 1", "item 2", "item 3")
	val list = ArrayList<String>()
	
	init {
		for(i in 1..40) list.add("item $i")
	}
	
	override fun getItemCount(): Int {
		return list.size
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(context).inflate(R.layout.address_list_item, parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.addressItem?.text = list.get(position)
	}
}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
	val addressItem = view.address_item
}
