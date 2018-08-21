package com.kg.gettransfer.presentation.ui

import android.graphics.Color

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.presenter.SearchPresenter

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*

import timber.log.Timber

class AddressAdapter(private val presenter: SearchPresenter,
	                 private var list: List<GTAddress>): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
	                 
	companion object {
		private var selected = RecyclerView.NO_POSITION
	}
	
	init {
		selected = RecyclerView.NO_POSITION
	}
	                 
	override fun getItemCount(): Int {
		return list.size
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = 
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))
	
	override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
		holder.bind(list.get(pos)) {
			notifyDataSetChanged()
			presenter.onAddressSelected(it)
		}
	}

	class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
		fun bind(item: GTAddress, listener: ClickHandler) = with(containerView) {
			addressItem.text = item.primary
			addressSecondaryItem.text = item.secondary
			setSelected(selected == adapterPosition)
			setOnClickListener {
				selected = adapterPosition
				listener(item) 
			}
		}
	}
}

// Just for test
typealias ClickHandler = (GTAddress) -> Unit
