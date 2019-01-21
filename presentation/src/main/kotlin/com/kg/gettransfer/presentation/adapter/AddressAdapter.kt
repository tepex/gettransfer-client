package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.extensions.*

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*

class AddressAdapter(
    private var list: List<GTAddress>,
    private val selectListener: (GTAddress) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLastAddresses = true

    init {
        selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder as AddressViewHolder).bindViews(list[pos], isLastAddresses) {
            notifyDataSetChanged()
            selectListener(it)
        }
    }

    class AddressViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bindViews(address: GTAddress, isLastAddress: Boolean, listener: ClickHandler) = with(containerView) {
            addressItem.text = address.address
            addressSecondaryItem.text = address.secondary
            setSelected(selected == adapterPosition)

            icon_for_last_place.isVisible = isLastAddress
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

    fun removeItem(item: GTAddress) {
        list.indexOf(item).also {
            list.minusElement(item)
            notifyItemRemoved(it)
        }

    }

    companion object {
        private var selected = RecyclerView.NO_POSITION
    }
}

// Just for test
typealias ClickHandler = (GTAddress) -> Unit
