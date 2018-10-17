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
                     private var list: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var selected = RecyclerView.NO_POSITION

        const val GOOGLE_ADDRESS = 1
        const val POPULAR_ADDRESS = 2
    }

    init {
        selected = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GOOGLE_ADDRESS -> AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_item, parent, false))
            else -> PopularPlaceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_address_list_item, parent, false))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder as UpdateViewHolder).bindViews(list[pos])
        {
            notifyDataSetChanged()
            if(holder is AddressViewHolder)
                presenter.onAddressSelected(it as GTAddress)
            else if(holder is PopularPlaceViewHolder)
                presenter.onPopularSelected(it as PopularPlace)
        }
    }

    class AddressViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer, UpdateViewHolder {
        override fun bindViews(address: Any, listener: ClickHandler) = with(containerView) {
            val mAddress = address as GTAddress
            addressItem.text = mAddress.address
            addressSecondaryItem.text = mAddress.secondary
            setSelected(selected == adapterPosition)
            setOnClickListener {
                selected = adapterPosition
                listener(mAddress)
            }
        }
    }

    class PopularPlaceViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer, UpdateViewHolder {

        override fun bindViews(address: Any, listener: ClickHandler) = with(containerView) {
            val mAddress = address as PopularPlace
            popular_title.text = mAddress.title
            popular_icon.setImageDrawable(containerView.context.getDrawable(mAddress.icon))
            setOnClickListener {
                listener(mAddress)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is GTAddress) GOOGLE_ADDRESS else POPULAR_ADDRESS
    }

    fun updateList(list: List<Any>){
        this.list = list
        notifyDataSetChanged()
    }

    interface UpdateViewHolder {
        fun bindViews(address: Any, listener: ClickHandler)
    }
}

// Just for test
typealias ClickHandler = (Any) -> Unit
