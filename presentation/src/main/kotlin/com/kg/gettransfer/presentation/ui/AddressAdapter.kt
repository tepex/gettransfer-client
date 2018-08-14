package com.kg.gettransfer.presentation.ui

import android.graphics.Color

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.SearchPresenter

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.address_list_item.view.*

import timber.log.Timber

class AddressAdapter(private val presenter: SearchPresenter,
	                 private val list: List<String>): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
	companion object {
		var selectedPos = RecyclerView.NO_POSITION
	}
	
	override fun getItemCount(): Int {
		return list.size
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = 
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))
	
	override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
		holder.bind(list.get(pos), { presenter.onDestinationAddressSelected(it) })
	}
	
	class ViewHolder(override val containerView: View): 
		RecyclerView.ViewHolder(containerView), LayoutContainer {
			
		fun bind(item: String, listener: ClickHandler) = with(containerView) {
			addressItem.text = item
			setSelected(selectedPos == adapterPosition)
			setOnClickListener {
				selectedPos = adapterPosition
				setSelected(selectedPos == adapterPosition)
				listener(item) 
			}
		}
	}
}

// Just for test
typealias ClickHandler = (String) -> Unit
