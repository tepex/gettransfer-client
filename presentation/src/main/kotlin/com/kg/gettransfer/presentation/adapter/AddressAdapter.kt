package com.kg.gettransfer.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import androidx.core.view.isVisible

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*

class AddressAdapter(
    private var list: List<GTAddress>,
    private val selectListener: (GTAddress) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder as AddressViewHolder).bindViews(list[pos]) {
            notifyDataSetChanged()
            selectListener(it)
        }
    }

    class AddressViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bindViews(address: GTAddress, listener: ClickHandler) = with(containerView) {
            addressItem.text = address.address
            with(addressSecondaryItem) {
                isVisible = !address.variants?.second.isNullOrEmpty()
                addressSecondaryItem.text = address.variants?.second
            }
            isSelected = selected == adapterPosition

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
