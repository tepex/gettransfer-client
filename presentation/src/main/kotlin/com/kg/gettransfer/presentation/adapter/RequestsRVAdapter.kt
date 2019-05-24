package com.kg.gettransfer.presentation.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView

import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.ui.TransferRequestItem

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info_enabled.*

class RequestsRVAdapter(
    @LayoutRes private val layout: Int,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RequestsRVAdapter.ViewHolder>() {

    private val transfers = mutableListOf<TransferModel>()
    private var eventsCount = mapOf<Long, Int>()

    override fun getItemCount() = transfers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RequestsRVAdapter.ViewHolder(
                    TransferRequestItem(parent.context, layout).containerView
            )

    override fun onBindViewHolder(holder: RequestsRVAdapter.ViewHolder, pos: Int) {
        val transfer = transfers[pos]
        holder.bind(transfer, eventsCount[transfer.id] ?: 0, listener)
    }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: TransferModel, eventsCount: Int, listener: ItemClickListener) = with(containerView) {
            (this as TransferRequestItem).setInfo(item)
            showEvents(item, eventsCount)
            setOnClickListener { listener(item) }
        }

        private fun showEvents(item: TransferModel, eventsCount: Int) {
            if (eventsCount == 0 || (!item.showOfferInfo && item.statusCategory != Transfer.STATUS_CATEGORY_ACTIVE)) {
                tvEventsCount.isVisible = false
            } else {
                tvEventsCount.isVisible = true
                tvEventsCount.text = eventsCount.toString()
            }
        }
    }

    fun updateTransfers(tr: List<TransferModel>, removeAll: Boolean = true) {
        if (removeAll) transfers.clear()
        val start = transfers.size
        transfers.addAll(tr)
        if (removeAll)
            notifyDataSetChanged()
        else notifyItemRangeInserted(start, transfers.size)
    }

    fun updateEvents(eventsCount: Map<Long, Int>) {
        this.eventsCount = eventsCount
        notifyDataSetChanged()
    }
}

typealias ItemClickListener = (TransferModel) -> Unit
