package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.popular_address_list_item.*

class AddressAdapter(private val presenter: SearchPresenter,
                     private var list: List<GTAddress>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLastAddresses: Boolean = true

    companion object {
        private var selected = RecyclerView.NO_POSITION

    }

    init {
        selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder as AddressViewHolder).bindViews(list[pos], isLastAddresses)
        {
            notifyDataSetChanged()
            presenter.onAddressSelected(it)
        }
    }

    class AddressViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bindViews(address: GTAddress, isLastAddress: Boolean, listener: ClickHandler) = with(containerView) {
            addressItem.text = address.address
            addressSecondaryItem.text = address.secondary
            setSelected(selected == adapterPosition)
            icon_for_last_place.visibility = if(!isLastAddress) View.GONE else View.VISIBLE
            setOnClickListener {
                selected = adapterPosition
                listener(address)
            }
        }
    }

    fun updateList(list: List<GTAddress>) {
        this.list = list
        notifyDataSetChanged()
    }
}

// Just for test
typealias ClickHandler = (GTAddress) -> Unit
