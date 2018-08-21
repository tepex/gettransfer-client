package com.kg.gettransfer.presentation.ui

import android.graphics.Color

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.model.Address
import com.kg.gettransfer.presentation.presenter.SearchPresenter

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*

import timber.log.Timber

class AddressAdapter(private val presenter: SearchPresenter,
	                 private var list: List<Address>): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
	override fun getItemCount(): Int {
		return list.size
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = 
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))
	
	override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
		holder.bind(list.get(pos)) { presenter.onAddressSelected(it) }
	}

	class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
		fun bind(item: Address, listener: ClickHandler) = with(containerView) {
			addressItem.text = item.address
			setSelected(item.selected)
			setOnClickListener {
				item.selected = true
				setSelected(true)
				listener(item) 
			}
		}
	}
}

// Just for test
typealias ClickHandler = (GTAddress) -> Unit
